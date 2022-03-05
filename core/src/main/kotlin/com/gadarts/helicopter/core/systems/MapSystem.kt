package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute.*
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.EntityBuilder
import com.gadarts.helicopter.core.GameMap
import com.gadarts.helicopter.core.GeneralUtils
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.ModelsDefinitions.PALM_TREE
import com.gadarts.helicopter.core.assets.TexturesDefinitions
import com.gadarts.helicopter.core.components.AmbComponent
import com.gadarts.helicopter.core.components.ComponentsMapper

class MapSystem : GameEntitySystem() {

    private val floors: Array<Array<Entity?>> = Array(MAP_SIZE) { arrayOfNulls(MAP_SIZE) }
    private lateinit var ambEntities: ImmutableArray<Entity>
    private lateinit var floorModel: Model

    override fun initialize(am: GameAssetManager) {
        addTrees(am)
        applyTransformOnAmbEntities()
        val builder = ModelBuilder()
        builder.begin()
        val texture = assetsManager.getAssetByDefinition(TexturesDefinitions.SAND)
        GeneralUtils.createFlatMesh(builder, "floor", 0.5F, texture, 0F)
        floorModel = builder.end()
        addGround(am, MAP_SIZE, MAP_SIZE, 0, 0)
        addGround(am, 20, MAP_SIZE, 0, -20)
    }

    private fun addGround(am: GameAssetManager, rows: Int, cols: Int, xOffset: Int, zOffset: Int) {
        val map = am.getAll(GameMap::class.java, com.badlogic.gdx.utils.Array())[0]
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                addGroundTile(row, col, map, xOffset, zOffset)
            }
        }

    }

    private fun addGroundTile(
        row: Int,
        col: Int,
        map: GameMap,
        xOffset: Int,
        zOffset: Int
    ) {
        val modelInstance = ModelInstance(floorModel)
        val entity = EntityBuilder.begin().addModelInstanceComponent(
            modelInstance,
            auxVector1.set(xOffset + col.toFloat() + 0.5F, 0F, zOffset + row.toFloat() + 0.5F)
        ).finishAndAddToEngine()
        var current = GameMap.TILE_TYPE_EMPTY
        val rowWithOffset = row + xOffset
        val colWithOffset = col + zOffset
        if (rowWithOffset >= 0
            && colWithOffset >= 0
            && rowWithOffset < floors.size
            && colWithOffset < floors[0].size
        ) {
            current = map.tilesMapping[rowWithOffset][colWithOffset]
            floors[rowWithOffset][colWithOffset] = entity
        }
        if (current != GameMap.TILE_TYPE_EMPTY) {
            initializeRoadTile(map, row, col, modelInstance, assetsManager)
        } else {
            randomizeSand(assetsManager, modelInstance)
        }

    }

    private fun initializeRoadTile(
        map: GameMap,
        row: Int,
        col: Int,
        modelInstance: ModelInstance,
        am: GameAssetManager
    ) {
        val right = col < MAP_SIZE - 1 && map.tilesMapping[row][col + 1] != GameMap.TILE_TYPE_EMPTY
        val btm = row < MAP_SIZE - 1 && map.tilesMapping[row + 1][col] != GameMap.TILE_TYPE_EMPTY
        val left = col > 0 && map.tilesMapping[row][col - 1] != GameMap.TILE_TYPE_EMPTY
        val top = row > 0 && map.tilesMapping[row - 1][col] != GameMap.TILE_TYPE_EMPTY
        val textureAttribute = modelInstance.materials.get(0).get(Diffuse) as TextureAttribute
        val roadTile = RoadTiles.getRoadTileByNeighbors(right, btm, left, top)
        if (roadTile != null) {
            textureAttribute.set(TextureRegion(am.getAssetByDefinition(roadTile.textureDefinition)))
        }
    }

    private fun randomizeSand(
        am: GameAssetManager,
        modelInstance: ModelInstance
    ) {
        if (MathUtils.random() > CHANCE_SAND_DEC) {
            val sandDecTexture = am.getAssetByDefinition(TexturesDefinitions.SAND_DEC)
            val attr = modelInstance.materials.first().get(Diffuse) as TextureAttribute
            val textureRegion = TextureRegion(sandDecTexture)
            attr.set(textureRegion)
            modelInstance.transform.rotate(
                Vector3.Y,
                MathUtils.random(4) * 90F
            )
        }
    }

    private fun applyTransformOnAmbEntities() {
        ambEntities = engine.getEntitiesFor(Family.all(AmbComponent::class.java).get())
        ambEntities.forEach {
            val scale = ComponentsMapper.amb.get(it).getScale(auxVector1)
            val transform = ComponentsMapper.modelInstance.get(it).modelInstance.transform
            transform.scl(scale).rotate(Vector3.Y, ComponentsMapper.amb.get(it).rotation)
        }
    }

    private fun addTrees(am: GameAssetManager) {
        addTree(am, Vector3(0F, 0F, 0F))
        addTree(am, Vector3(3F, 0F, 0F))
        addTree(am, Vector3(0F, 0F, 3F))
        addTree(am, Vector3(3F, 0F, 3F))
    }

    private fun addTree(am: GameAssetManager, position: Vector3) {
        val randomScale = MathUtils.random(MIN_SCALE, MAX_SCALE)
        val scale = auxVector1.set(randomScale, randomScale, randomScale)
        EntityBuilder.begin()
            .addModelInstanceComponent(am.getAssetByDefinition(PALM_TREE), position)
            .addAmbComponent(scale, MathUtils.random(0F, 360F))
            .finishAndAddToEngine()
    }

    override fun dispose() {
        floorModel.dispose()
    }

    /**
     * 0,0,0
     * 0,1,0
     * 0,1,0
     */
    enum class RoadTiles(val textureDefinition: TexturesDefinitions, val signature: Int) {
        VERTICAL(TexturesDefinitions.VERTICAL, 0B010010010),
        HORIZONTAL(TexturesDefinitions.HORIZONTAL, 0B000111000),
        LEFT_TO_BOTTOM(TexturesDefinitions.LEFT_TO_BOTTOM, 0B000110010),
        RIGHT_TO_BOTTOM(TexturesDefinitions.RIGHT_TO_BOTTOM, 0B000011010),
        LEFT_TO_TOP(TexturesDefinitions.LEFT_TO_TOP, 0B010110000),
        RIGHT_TO_TOP(TexturesDefinitions.RIGHT_TO_TOP, 0B010011000),
        RIGHT_END(TexturesDefinitions.RIGHT_END, 0B000011000),
        BOTTOM_END(TexturesDefinitions.BOTTOM_END, 0B000010010),
        LEFT_END(TexturesDefinitions.LEFT_END, 0B000110000),
        TOP_END(TexturesDefinitions.TOP_END, 0B010010000),
        CROSS(TexturesDefinitions.CROSS, 0B010111010),
        HORIZONTAL_BOTTOM(TexturesDefinitions.HORIZONTAL_BOTTOM, 0B000111010),
        HORIZONTAL_TOP(TexturesDefinitions.HORIZONTAL_TOP, 0B010111000),
        VERTICAL_RIGHT(TexturesDefinitions.VERTICAL_RIGHT, 0B010011010),
        VERTICAL_LEFT(TexturesDefinitions.VERTICAL_LEFT, 0B010110010);

        companion object {
            fun getRoadTileByNeighbors(
                right: Boolean,
                bottom: Boolean,
                left: Boolean,
                top: Boolean
            ): RoadTiles? {
                val signature = Integer.parseInt(
                    "0${if (top) "1" else "0"}0"
                            + "${if (left) "1" else "0"}1${if (right) "1" else "0"}"
                            + "0${if (bottom) "1" else "0"}0", 2
                )
                val values = values()
                for (element in values) {
                    if (element.signature == signature) {
                        return element
                    }
                }
                return null
            }
        }
    }

    companion object {
        private val auxVector1 = Vector3()
        private const val MIN_SCALE = 0.95F
        private const val MAX_SCALE = 1.05F
        private const val CHANCE_SAND_DEC = 0.95F
        const val MAP_SIZE = 20
    }
}
