package com.gadarts.helicopter.core.components

import com.badlogic.gdx.graphics.g3d.decals.Decal

abstract class ArmComponent : GameComponent() {
    lateinit var armProperties: ArmProperties
    var displaySpark: Long = 0L
    lateinit var sparkDecal: Decal
    var loaded: Long = 0L

    fun init(sparkDecal: Decal, armProperties: ArmProperties) {
        loaded = 0L
        displaySpark = 0L
        this.sparkDecal = sparkDecal
        this.armProperties = armProperties
    }

}
