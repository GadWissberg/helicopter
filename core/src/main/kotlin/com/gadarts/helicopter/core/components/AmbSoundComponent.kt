package com.gadarts.helicopter.core.components

import com.badlogic.gdx.audio.Sound

class AmbSoundComponent : GameComponent() {
    lateinit var sound: Sound

    fun init(sound: Sound) {
        this.sound = sound
    }

    override fun reset() {
    }

}
