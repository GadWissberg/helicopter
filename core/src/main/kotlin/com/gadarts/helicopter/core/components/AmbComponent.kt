package com.gadarts.helicopter.core.components

import com.badlogic.gdx.math.Vector3

class AmbComponent : GameComponent() {
    private val scale = Vector3()
    private val rotation = Vector3()

    override fun reset() {

    }

    fun init(scale: Vector3, rotation: Vector3) {
        this.scale.set(scale)
        this.rotation.set(rotation)
    }

}
