package com.gadarts.helicopter.core.systems.map

import com.gadarts.helicopter.core.assets.ModelsDefinitions

interface ElementsDefinitions {
    fun getModelDefinition(): ModelsDefinitions
    fun isRandomizeScale(): Boolean
    fun isRandomizeRotation(): Boolean
}
