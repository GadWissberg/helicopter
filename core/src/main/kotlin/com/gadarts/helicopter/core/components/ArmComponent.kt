package com.gadarts.helicopter.core.components

import com.badlogic.gdx.graphics.g3d.decals.Decal

class ArmComponent : GameComponent() {
    var displaySpark: Long = 0L
    lateinit var sparkDecal: Decal
    var loaded: Long = 0L

    fun init(sparkDecal: Decal) {
        loaded = 0L
        displaySpark = 0L
        this.sparkDecal = sparkDecal
    }

    override fun reset() {
    }
}
