package com.gadarts.helicopter.core.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.components.ArmComponent
import com.gadarts.helicopter.core.systems.SystemEventsSubscriber

interface PlayerSystemEventsSubscriber : SystemEventsSubscriber {
    fun onPlayerWeaponShot(
        player: Entity,
        bulletModelInstance: ModelInstance,
        arm: ArmComponent,
    )

}
