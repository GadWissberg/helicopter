package com.gadarts.helicopter.core

import com.badlogic.gdx.audio.Sound

class SoundPlayer {

    fun play(sound: Sound, loop: Boolean = false) {
        if (!DefaultGameSettings.SFX) return
        if (!loop) {
            sound.play()
        } else {
            sound.loop()
        }
    }

}
