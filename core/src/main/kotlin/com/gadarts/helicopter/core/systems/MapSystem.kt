package com.gadarts.helicopter.core.systems

import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.EntityBuilder
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.ModelsDefinitions

class MapSystem : GameEntitySystem() {
    override fun initialize(am: GameAssetManager) {
        addTree(am, Vector3(1F, 0F, 1F))
        addTree(am, Vector3(2F, 0F, 1F))
        addTree(am, Vector3(2F, 0F, 2F))
        addTree(am, Vector3(3F, 0F, 3F))
    }

    private fun addTree(am: GameAssetManager, position: Vector3) {
        EntityBuilder.begin().addModelInstanceComponent(
            am.getAssetByDefinition(ModelsDefinitions.PALM_TREE),
            position
        ).finishAndAddToEngine()
    }

    override fun dispose() {
    }

}
