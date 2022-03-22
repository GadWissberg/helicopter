package com.gadarts.helicopter.core.systems.map

import com.gadarts.helicopter.core.assets.ModelsDefinitions

enum class CharactersDefinitions(
    private val modelDefinition: ModelsDefinitions,
) : ElementsDefinitions {
    PLAYER(ModelsDefinitions.APACHE);

    override fun getModelDefinition(): ModelsDefinitions {
        return modelDefinition
    }

    override fun isRandomizeRotation(): Boolean {
        return false
    }

    override fun isRandomizeScale(): Boolean {
        return false
    }
}