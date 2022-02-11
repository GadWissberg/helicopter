package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Disposable

abstract class GameEntitySystem() : Disposable, EntitySystem() {
    abstract fun initialize()
}
