package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.EntityBuilder
import com.gadarts.helicopter.core.GeneralUtils
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.ModelsDefinitions.PALM_TREE
import com.gadarts.helicopter.core.assets.TexturesDefinitions
import com.gadarts.helicopter.core.components.AmbComponent
import com.gadarts.helicopter.core.components.ComponentsMapper

class MapSystem : GameEntitySystem() {
    private lateinit var ambEntities: ImmutableArray<Entity>
    private lateinit var floors: Array<IntArray>
    private lateinit var floorModel: Model

    override fun initialize(am: GameAssetManager) {
        addTrees(am)
        applyTransformOnAmbEntities()
        val builder = ModelBuilder()
        builder.begin()
        val texture = assetsManager.getAssetByDefinition(TexturesDefinitions.SAND)
        GeneralUtils.createFlatMesh(builder, "floor", 0.5F, texture, 0.5F)
        floorModel = builder.end()
        addGround()
    }

    private fun addGround() {
        floors = Array(MAP_SIZE) { IntArray(MAP_SIZE) }
        for (row in 0..MAP_SIZE) {
            for (col in 0..MAP_SIZE) {
                EntityBuilder.begin().addModelInstanceComponent(
                    floorModel,
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
        private const val MAP_SIZE = 20
    }
}
