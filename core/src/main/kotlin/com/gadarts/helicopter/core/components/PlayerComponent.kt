package com.gadarts.helicopter.core.components

class PlayerComponent : GameComponent() {
    private var fuel: Int = INITIAL_FUEL

    override fun reset() {
    }

    fun init() {
        this.fuel = INITIAL_FUEL
    }

    companion object {
        const val INITIAL_FUEL = 100
    }
}
