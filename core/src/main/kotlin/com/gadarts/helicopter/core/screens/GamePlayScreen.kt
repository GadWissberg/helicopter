package com.gadarts.helicopter.core.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Screen
import com.gadarts.helicopter.core.SoundPlayer
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.systems.*
import com.gadarts.helicopter.core.systems.camera.CameraSystem
import com.gadarts.helicopter.core.systems.character.CharacterSystem
import com.gadarts.helicopter.core.systems.hud.HudSystem
import com.gadarts.helicopter.core.systems.player.PlayerSystem
import com.gadarts.helicopter.core.systems.profiling.ProfilingSystem
import com.gadarts.helicopter.core.systems.render.RenderSystem


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
        val data = CommonData(assetsManager)
        addSystems(data)
        initializeSubscriptions()
        engine.systems.forEach {
            (it as GameEntitySystem).initialize(
                assetsManager
            )
        }
    }

    private fun initializeSubscriptions() {
        val hudSystem = engine.getSystem(HudSystem::class.java)
        hudSystem.subscribeForEvents(engine.getSystem(PlayerSystem::class.java))
        val playerSystem = engine.getSystem(PlayerSystem::class.java)
        playerSystem.subscribeForEvents(engine.getSystem(RenderSystem::class.java))
        playerSystem.subscribeForEvents(engine.getSystem(CharacterSystem::class.java))
    }

    private fun addSystems(data: CommonData) {
        addSystem(CharacterSystem(), data)
        addSystem(PlayerSystem(), data)
        addSystem(RenderSystem(), data)
        addSystem(CameraSystem(), data)
        addSystem(HudSystem(), data)
        addSystem(ProfilingSystem(), data)
        addSystem(MapSystem(), data)
    }

    private fun addSystem(system: GameEntitySystem, data: CommonData) {
        system.commonData = data
        system.soundPlayer = soundPlayer
        system.assetsManager = assetsManager
        engine.addSystem(system)
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