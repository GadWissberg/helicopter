package com.gadarts.helicopter.core.components

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance

class ArmComponent : GameComponent() {
    var displayMuzzle: Boolean = false
    lateinit var modelInstance: ModelInstance
    var loaded: Boolean = true
    fun init(muzzleModel: Model) {
        loaded = true
        displayMuzzle = false
        this.modelInstance = ModelInstance(muzzleModel)
    }

    override fun reset() {
    }

}
