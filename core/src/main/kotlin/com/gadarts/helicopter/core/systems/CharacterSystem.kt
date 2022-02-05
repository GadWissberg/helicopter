package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.gadarts.helicopter.core.SoundPlayer
import com.gadarts.helicopter.core.components.ComponentsMapper

class CharacterSystem(data: SystemsData, private val soundPlayer: SoundPlayer) : EntitySystem() {

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        engine!!.addEntityListener(object : EntityListener {
            override fun entityAdded(entity: Entity?) {
                if (ComponentsMapper.ambSound.has(entity)) {
                    soundPlayer.play(ComponentsMapper.ambSound.get(entity).sound, loop = true)
                }
            }

            override fun entityRemoved(entity: Entity?) {
            }

        })
    }
}
