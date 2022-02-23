package com.gadarts.helicopter.core.components

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureRegion

class ArmProperties(
    val sparkFrames: List<TextureRegion>,
    val shootingSound: Sound,
    val reloadDuration: Long,
    val speed: Float
)
