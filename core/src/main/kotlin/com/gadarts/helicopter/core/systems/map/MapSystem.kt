package com.gadarts.helicopter.core.systems.map

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelCache
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.helicopter.core.EntityBuilder
import com.gadarts.helicopter.core.GameMap
import com.gadarts.helicopter.core.GeneralUtils
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.SfxDefinitions
import com.gadarts.helicopter.core.assets.TexturesDefinitions
import com.gadarts.helicopter.core.components.AmbComponent
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.systems.GameEntitySystem

class MapSystem : GameEntitySystem() {

    private val ambSounds = listOf(
        SfxDefinitions.AMB_EAGLE,
        SfxDefinitions.AMB_WIND,
        SfxDefinitions.AMB_OUD
    )
    private var nextAmbSound: Long = TimeUtils.millis() + random(
        AMB_SND_INTERVAL_MIN,
        AMB_SND_INTERVAL_MAX
    )
    private lateinit var floors: Array<Array<Entity?>>
    private lateinit var ambEntities: ImmutableArray<Entity>
    private lateinit var floorModel: Model


    override fun initialize(am: GameAssetManager) {
        applyTransformOnAmbEntities()
        initializeAmbObjectsBoundingBox()
    }

    private fun initializeAmbObjectsBoundingBox() {
        for (entity in ambEntities) {
            if (ComponentsMapper.modelInstance.has(entity)) {
                val boxCollisionComponent = ComponentsMapper.boxCollision.get(entity)
                boxCollisionComponent.getBoundingBox(auxBoundingBox)
                auxBoundingBox.mul(ComponentsMapper.modelInstance.get(entity).modelInstance.transform)
                boxCollisionComponent.setBoundingBox(auxBoundingBox)
            }
        }
    }

    override fun resume(delta: Long) {
        nextAmbSound += delta
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val now = TimeUtils.millis()
        if (nextAmbSound < now) {
            nextAmbSound = now + random(AMB_SND_INTERVAL_MIN, AMB_SND_INTERVAL_MAX)
            soundPlayer.play(assetsManager.getAssetByDefinition(ambSounds.random()))
        }
    }

    private fun addAmbDecal(am: GameAssetManager, x: Float, z: Float) {
        val textureRegion = TextureRegion(am.getAssetByDefinition(TexturesDefinitions.BUSH))
        EntityBuilder.begin()
            .addDecalComponent(textureRegion, auxVector1.set(x, 0.18F, z))
            .addAmbComponent()
            .finishAndAddToEngine()
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        val builder = ModelBuilder()
        createFloorModel(builder)
        val tilesMapping = commonData.currentMap.tilesMapping
        floors = Array(tilesMapping.size) { arrayOfNulls(tilesMapping[0].size) }
        commonData.modelCache = ModelCache()
        addGround()
        commonData.currentMap.placedElements.forEach {
            if (it.definition != CharactersDefinitions.PLAYER) {
                addAmbModelObject(
                    assetsManager,
                    auxVector2.set(it.col.toFloat(), 0.01F, it.row.toFloat()),
                    it.definition as AmbModelDefinitions
                )
            }
        }
    }

    private fun createFloorModel(builder: ModelBuilder) {
        builder.begin()
        val texture = assetsManager.getAssetByDefinition(TexturesDefinitions.SAND)
        GeneralUtils.createFlatMesh(builder, "floor", 0.5F, texture, 0F)
        floorModel = builder.end()
    }

    private fun addGround() {
        commonData.modelCache.begin()
        val tilesMapping = commonData.currentMap.tilesMapping
        val depth = tilesMapping.size
        val width = tilesMapping[0].size
        addGroundRegion(depth, width, 0, 0)
        addAllExternalGrounds(width, depth)
        commonData.modelCache.end()
    }

    private fun addAllExternalGrounds(width: Int, depth: Int) {
        addExtGround(width, EXT_SIZE, width / 2F, -EXT_SIZE / 2F)
        addExtGround(EXT_SIZE, EXT_SIZE, -EXT_SIZE / 2F, -EXT_SIZE / 2F)
        addExtGround(EXT_SIZE, depth, -width / 2F, depth / 2F)
        addExtGround(EXT_SIZE, EXT_SIZE, -width / 2F, depth + EXT_SIZE / 2F)
        addExtGround(width, EXT_SIZE, width / 2F, depth + EXT_SIZE / 2F)
        addExtGround(EXT_SIZE, EXT_SIZE, width + EXT_SIZE / 2F, depth + EXT_SIZE / 2F)
        addExtGround(EXT_SIZE, depth, width + EXT_SIZE / 2F, depth / 2F)
        addExtGround(EXT_SIZE, EXT_SIZE, width + EXT_SIZE / 2F, -depth / 2F)
    }

    private fun addExtGround(width: Int, depth: Int, x: Float, z: Float) {
        val modelInstance = ModelInstance(floorModel)
        createAndAddGroundTileEntity(
            modelInstance,
            auxVector1.set(x, 0F, z)
        )
        modelInstance.transform.scl(width.toFloat(), 1F, depth.toFloat())
        val textureAttribute =
            modelInstance.materials.first().get(TextureAttribute.Diffuse) as TextureAttribute
        initializeExternalGroundTextureAttribute(textureAttribute, width, depth)
        commonData.modelCache.add(modelInstance)
    }

    private fun initializeExternalGroundTextureAttribute(
        textureAttribute: TextureAttribute,
        width: Int,
        depth: Int
    ) {
        textureAttribute.textureDescription.uWrap = Texture.TextureWrap.Repeat
        textureAttribute.textureDescription.vWrap = Texture.TextureWrap.Repeat
        textureAttribute.offsetU = 0F
        textureAttribute.offsetV = 0F
        textureAttribute.scaleU = width.toFloat()
        textureAttribute.scaleV = depth.toFloat()
    }

    private fun addGroundRegion(
        rows: Int,
        cols: Int,
        xOffset: Int,
        zOffset: Int
    ) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                addGroundTile(
                    row,
                    col,
                    xOffset,
                    zOffset,
                    ModelInstance(floorModel)
                )
            }
        }
    }

    private fun addGroundTile(
        row: Int,
        col: Int,
        xOffset: Int,
        zOffset: Int,
        modelInstance: ModelInstance
    ) {
        val entity = createAndAddGroundTileEntity(
            modelInstance,
            auxVector1.set(xOffset + col.toFloat() + 0.5F, 0F, zOffset + row.toFloat() + 0.5F)
        )
        commonData.modelCache.add(modelInstance)
        var current = GameMap.TILE_TYPE_EMPTY
        val rowWithOffset = row + xOffset
        val colWithOffset = col + zOffset
        if (rowWithOffset >= 0
            && colWithOffset >= 0
            && rowWithOffset < commonData.currentMap.tilesMapping.size
            && colWithOffset < commonData.currentMap.tilesMapping[0].size
        ) {
            current = commonData.currentMap.tilesMapping[rowWithOffset][colWithOffset]
            floors[rowWithOffset][colWithOffset] = entity
        }
        if (current != GameMap.TILE_TYPE_EMPTY) {
            initializeRoadTile(commonData.currentMap, row, col, modelInstance, assetsManager)
        } else {
            randomizeSand(assetsManager, modelInstance)
        }

    }

    private fun createAndAddGroundTileEntity(
        modelInstance: ModelInstance,
        position: Vector3
    ): Entity {
        return EntityBuilder.begin()
            .addModelInstanceComponent(modelInstance, position)
            .addGroundComponent()
            .finishAndAddToEngine()
    }

    private fun initializeRoadTile(
        map: GameMap,
        row: Int,
        col: Int,
        modelInstance: ModelInstance,
        am: GameAssetManager
    ) {
        val depth = map.tilesMapping.size
        val width = map.tilesMapping[0].size
        if (row >= depth || row < 0 || col >= width || col < 0) return
        val right = col < width - 1 && map.tilesMapping[row][col + 1] != GameMap.TILE_TYPE_EMPTY
        val btm = row < depth - 1 && map.tilesMapping[row + 1][col] != GameMap.TILE_TYPE_EMPTY
        val left = col > 0 && map.tilesMapping[row][col - 1] != GameMap.TILE_TYPE_EMPTY
        val top = row > 0 && map.tilesMapping[row - 1][col] != GameMap.TILE_TYPE_EMPTY
        val textureAttribute =
            modelInstance.materials.get(0).get(TextureAttribute.Diffuse) as TextureAttribute
        val roadTile = RoadTiles.getRoadTileByNeighbors(right, btm, left, top)
        if (roadTile != null) {
            textureAttribute.set(TextureRegion(am.getAssetByDefinition(roadTile.textureDefinition)))
        }
    }

    private fun randomizeSand(
        am: GameAssetManager,
        modelInstance: ModelInstance
    ) {
        if (random() > CHANCE_SAND_DEC) {
            val sandDecTexture = am.getAssetByDefinition(TexturesDefinitions.SAND_DEC)
            val attr =
                modelInstance.materials.first().get(TextureAttribute.Diffuse) as TextureAttribute
            val textureRegion = TextureRegion(sandDecTexture)
            attr.set(textureRegion)
            modelInstance.transform.rotate(
                Vector3.Y,
                random(4) * 90F
            )
        }
    }

    private fun applyTransformOnAmbEntities() {
        ambEntities = engine.getEntitiesFor(Family.all(AmbComponent::class.java).get())
        ambEntities.forEach {
            if (!ComponentsMapper.modelInstance.has(it)) return
            val scale = ComponentsMapper.amb.get(it).getScale(auxVector1)
            val transform = ComponentsMapper.modelInstance.get(it).modelInstance.transform
            transform.scl(scale).rotate(Vector3.Y, ComponentsMapper.amb.get(it).rotation)
        }
    }

    private fun addAmbModelObject(
        am: GameAssetManager,
        position: Vector3,
        def: AmbModelDefinitions,
    ) {
        val randomScale = if (def.isRandomizeScale()) random(MIN_SCALE, MAX_SCALE) else 1F
        val scale = auxVector1.set(randomScale, randomScale, randomScale)
        val model = am.getAssetByDefinition(def.getModelDefinition())
        EntityBuilder.begin()
            .addModelInstanceComponent(model, position)
            .addAmbComponent(scale, if (def.isRandomizeRotation()) random(0F, 360F) else 0F)
            .addBoxCollisionComponent(model)
            .finishAndAddToEngine()
    }

    override fun dispose() {
        floorModel.dispose()
        commonData.modelCache.dispose()
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
        private val auxVector2 = Vector3()
        private val auxBoundingBox = BoundingBox()
        private const val MIN_SCALE = 0.95F
        private const val MAX_SCALE = 1.05F
        private const val CHANCE_SAND_DEC = 0.95F
        private const val EXT_SIZE = 48
        private const val AMB_SND_INTERVAL_MIN = 7000
        private const val AMB_SND_INTERVAL_MAX = 22000
    }
}