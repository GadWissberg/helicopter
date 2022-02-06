package com.gadarts.helicopter.core.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Screen
import com.gadarts.helicopter.core.SoundPlayer
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.systems.*


/**
 * The screen of the game itself.
 */
class GamePlayScreen(
    private val assetsManager: GameAssetManager,
    private val soundPlayer: SoundPlayer
) : Screen {


    private lateinit var engine: PooledEngine

    override fun show() {
        this.engine = PooledEngine()
        val data = SystemsData(assetsManager)
        addSystems(data)
        engine.getSystem(InputSystem::class.java).initialize()
    }

    private fun addSystems(data: SystemsData) {
        engine.addSystem(CameraSystem(data))
        engine.addSystem(RenderSystem(data))
        engine.addSystem(InputSystem(data, assetsManager))
        engine.addSystem(CharacterSystem(data, soundPlayer))
        engine.addSystem(ProfilingSystem(data))
        engine.addSystem(HudSystem(data))
        engine.addSystem(PlayerSystem(data, assetsManager))
    }


    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
        engine.systems.forEach { (it as GameEntitySystem).dispose() }
    }


}