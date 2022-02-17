package com.gadarts.helicopter.core.components

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance

class ArmComponent : GameComponent() {
    var displaySpark: Long = 0L
    lateinit var modelInstance: ModelInstance
    var loaded: Long = 0L

    fun init(muzzleModel: Model) {
        loaded = 0L
        displaySpark = 0L
        this.modelInstance = ModelInstance(muzzleModel)
        this.modelInstance.transform.scale(SPARK_SCALE, 1.0F, SPARK_SCALE)
    }

    override fun reset() {
    }

    companion object {
        const val SPARK_SCALE = 0.3F
    }
}
