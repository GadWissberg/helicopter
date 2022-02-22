package com.gadarts.helicopter.core

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.MathUtils

class SoundPlayer {

    fun playPositionalSound(
        sound: Sound,
        randomPitch: Boolean = false,
        entity: Entity,
        camera: PerspectiveCamera
    ): Long {
        if (!DefaultGameSettings.SFX) return -1
        val pitch = if (randomPitch) MathUtils.random(PITCH_MIN, PITCH_MAX) else 1F
        val volume = GeneralUtils.calculateVolumeAccordingToPosition(entity, camera)
        return sound.play(volume, pitch, 0F)
    }

    fun loopSound(sound: Sound): Long {
        if (!DefaultGameSettings.SFX) return -1
        return sound.loop(1F, 1F, 0F)
    }

    companion object {
        const val PITCH_MIN = 0.9F
        const val PITCH_MAX = 1.1F
    }
}
