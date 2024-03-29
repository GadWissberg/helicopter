package com.gadarts.helicopter.core.systems.map

import com.gadarts.helicopter.core.assets.ModelsDefinitions

enum class AmbModelDefinitions(
    private val modelDefinition: ModelsDefinitions,
    private val randomizeScale: Boolean = false,
    private val randomizeRotation: Boolean = false,
) : ElementsDefinitions {
    PALM_TREE(ModelsDefinitions.PALM_TREE, true, true),
    ROCK(ModelsDefinitions.ROCK, true, true),
    BUILDING(ModelsDefinitions.BUILDING),
    FENCE(ModelsDefinitions.FENCE),
    LIGHT_POLE(ModelsDefinitions.LIGHT_POLE),
    BARRIER(ModelsDefinitions.BARRIER),
    CABIN(ModelsDefinitions.CABIN),
    CAR(ModelsDefinitions.CAR),
    GUARD_HOUSE(ModelsDefinitions.GUARD_HOUSE),
    ANTENNA(ModelsDefinitions.ANTENNA);

    override fun getModelDefinition(): ModelsDefinitions {
        return modelDefinition
    }

    override fun isRandomizeRotation(): Boolean {
        return randomizeRotation
    }

    override fun isRandomizeScale(): Boolean {
        return randomizeScale
    }
}