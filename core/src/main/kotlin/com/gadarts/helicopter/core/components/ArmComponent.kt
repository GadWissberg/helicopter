package com.gadarts.helicopter.core.components

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.decals.Decal

class ArmComponent : GameComponent() {
    val sparkFrames = ArrayList<TextureRegion>()
    var displaySpark: Long = 0L
    lateinit var sparkDecal: Decal
    var loaded: Long = 0L

    fun init(sparkDecal: Decal, sparkFrames: List<TextureRegion>) {
        loaded = 0L
        displaySpark = 0L
        this.sparkDecal = sparkDecal
        this.sparkFrames.clear()
        this.sparkFrames.addAll(sparkFrames)
    }

    override fun reset() {
    }
}
