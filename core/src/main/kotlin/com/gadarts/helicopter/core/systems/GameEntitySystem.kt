package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.Disposable
import com.gadarts.helicopter.core.SoundPlayer
import com.gadarts.helicopter.core.assets.GameAssetManager

abstract class GameEntitySystem : Disposable, EntitySystem() {
    lateinit var assetsManager: GameAssetManager
    lateinit var soundPlayer: SoundPlayer
    lateinit var commonData: CommonData

    abstract fun initialize(am: GameAssetManager)
    abstract fun resume(delta: Long)
}
