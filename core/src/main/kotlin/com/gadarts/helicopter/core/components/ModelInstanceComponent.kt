package com.gadarts.helicopter.core.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.ModelInstance

class ModelInstanceComponent : Component {
    lateinit var modelInstance: ModelInstance

    fun init(modelInstance: ModelInstance) {
        this.modelInstance = modelInstance
    }

}
