package com.gadarts.helicopter.core.components

import com.badlogic.gdx.math.Vector3

class BulletComponent : GameComponent() {
    val initialPosition = Vector3()

    override fun reset() {

    }

    fun init(initialPosition: Vector3) {
        this.initialPosition.set(initialPosition)
    }

}
