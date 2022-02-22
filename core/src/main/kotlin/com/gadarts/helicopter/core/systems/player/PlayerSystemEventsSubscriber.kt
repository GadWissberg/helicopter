package com.gadarts.helicopter.core.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.gadarts.helicopter.core.systems.SystemEventsSubscriber

interface PlayerSystemEventsSubscriber : SystemEventsSubscriber {
    fun onPlayerPrimaryWeaponShot(
        player: Entity,
        bulletModelInstance: ModelInstance,
        primaryShootingSound: Sound
    )

}
