package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
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
        GeneralUtils.createFlatMesh(builder, "floor", 0.5F, texture, 0.5F)
        floorModel = builder.end()
        addGround(am)
    }

    private fun addGround(am: GameAssetManager) {
        val map = am.getAll(GameMap::class.java, com.badlogic.gdx.utils.Array())[0]
        for (row in 0 until MAP_SIZE) {
            for (col in 0 until MAP_SIZE) {
                val modelInstance = ModelInstance(floorModel)
//                if (map.tilesMapping[row][col] != GameMap.TILE_TYPE_EMPTY) {
//
//                }
                if (MathUtils.random() > 0.9) {
                    val sandDecTexture = am.getAssetByDefinition(TexturesDefinitions.SAND_DEC)
                    (modelInstance.materials.first()
                        .get(TextureAttribute.Diffuse) as TextureAttribute).set(
                        TextureRegion(sandDecTexture)
                    )
                }
                floors[row][col] = EntityBuilder.begin().addModelInstanceComponent(
                    modelInstance,
                    auxVector1.set(col.toFloat(), 0F, row.toFloat())
                ).finishAndAddToEngine()
            }
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

    companion object {
        private val auxVector1 = Vector3()
        private const val MIN_SCALE = 0.95F
        private const val MAX_SCALE = 1.05F
        const val MAP_SIZE = 20
    }
}
