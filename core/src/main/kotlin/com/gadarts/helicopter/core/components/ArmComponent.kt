package com.gadarts.helicopter.core.components

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.decals.Decal

abstract class ArmComponent : GameComponent() {
    lateinit var shootingSound: Sound
    val sparkFrames = ArrayList<TextureRegion>()
    var displaySpark: Long = 0L
    lateinit var sparkDecal: Decal
    var loaded: Long = 0L

    fun init(sparkDecal: Decal, sparkFrames: List<TextureRegion>, shootingSound: Sound) {
        loaded = 0L
        displaySpark = 0L
        this.sparkDecal = sparkDecal
        this.sparkFrames.clear()
        this.sparkFrames.addAll(sparkFrames)
        this.shootingSound = shootingSound
    }

}
