package com.gadarts.helicopter.core.components

import com.badlogic.gdx.math.Vector2

class PlayerComponent : GameComponent() {
    private var fuel: Int = INITIAL_FUEL
    private val currentVelocity = Vector2(1F, 0F)
    var strafing: Float? = null

    override fun reset() {
    }

    fun getCurrentVelocity(output: Vector2): Vector2 {
        return output.set(currentVelocity)
    }

    fun setCurrentVelocity(input: Vector2): Vector2 {
        currentVelocity.set(input)
        return input
    }

    fun init() {
        this.fuel = INITIAL_FUEL
        this.currentVelocity.set(1F, 0F)
    }

    companion object {
        const val INITIAL_FUEL = 100
    }
}
