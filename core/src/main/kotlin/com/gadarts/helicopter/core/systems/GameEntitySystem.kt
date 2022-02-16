package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Disposable
import com.gadarts.helicopter.core.Notifier
import com.gadarts.helicopter.core.assets.GameAssetManager

abstract class GameEntitySystem<T> : Disposable, EntitySystem(),
    Notifier<T> where T : SystemEventsSubscriber {
    abstract fun initialize(assetsManager: GameAssetManager)
}
