package com.gadarts.helicopter.core.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Screen
import com.gadarts.helicopter.core.systems.CameraSystem
import com.gadarts.helicopter.core.systems.InputSystem
import com.gadarts.helicopter.core.systems.RenderSystem
import com.gadarts.helicopter.core.systems.SystemsData


/**
 * The screen of the game itself.
 */
class GamePlayScreen(
) : Screen {


    private lateinit var engine: PooledEngine

    override fun show() {
        this.engine = PooledEngine()
        val data = SystemsData()
        engine.addSystem(CameraSystem(data))
        engine.addSystem(RenderSystem(data))
        engine.addSystem(InputSystem(data))
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
    }

}