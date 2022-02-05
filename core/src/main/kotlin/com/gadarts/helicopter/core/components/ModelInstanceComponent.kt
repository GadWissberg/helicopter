package com.gadarts.helicopter.core.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3

class ModelInstanceComponent : Component {
    lateinit var modelInstance: ModelInstance

    fun init(model: Model, position: Vector3) {
        this.modelInstance = ModelInstance(model)
        this.modelInstance.transform.translate(position)
    }

}
