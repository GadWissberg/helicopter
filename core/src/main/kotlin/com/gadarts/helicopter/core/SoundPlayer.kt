package com.gadarts.helicopter.core

import com.badlogic.gdx.audio.Sound

class SoundPlayer {

    fun play(sound: Sound, loop: Boolean = false): Long {
        if (!DefaultGameSettings.SFX) return -1
        val id: Long = if (!loop) {
            sound.play()
        } else {
            sound.loop()
        }
        return id
    }

}
