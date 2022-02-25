package com.gadarts.helicopter.core.systems

import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.EntityBuilder
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.ModelsDefinitions

class MapSystem : GameEntitySystem() {
    override fun initialize(am: GameAssetManager) {
        EntityBuilder.begin().addModelInstanceComponent(
            am.getModel(ModelsDefinitions.PALM_TREE_1),
            Vector3(1F, 0F, 1F)
        ).finishAndAddToEngine()
    }

    override fun dispose() {
    }

}
