package com.gadarts.helicopter.core.systems

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.EntityBuilder
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.ModelsDefinitions.PALM_TREE

class MapSystem : GameEntitySystem() {
    override fun initialize(am: GameAssetManager) {
        addTree(am, Vector3(1F, 0F, 1F))
        addTree(am, Vector3(2F, 0F, 1F))
        addTree(am, Vector3(2F, 0F, 2F))
        addTree(am, Vector3(3F, 0F, 3F))
    }

    private fun addTree(am: GameAssetManager, position: Vector3) {
        val randomScale = MathUtils.random(MIN_SCALE, MAX_SCALE)
        val scale = auxVector1.set(randomScale, randomScale, randomScale)
        val rotation = auxVector2.set(MathUtils.random(), 0F, MathUtils.random())
        EntityBuilder.begin()
            .addModelInstanceComponent(am.getAssetByDefinition(PALM_TREE), position)
            .addAmbComponent(scale, rotation)
            .finishAndAddToEngine()
    }

    override fun dispose() {
    }

    companion object {
        private val auxVector1 = Vector3()
        private val auxVector2 = Vector3()
        private const val MIN_SCALE = 0.8F
        private const val MAX_SCALE = 1.1F
    }
}
