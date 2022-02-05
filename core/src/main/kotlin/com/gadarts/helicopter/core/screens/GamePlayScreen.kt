package com.gadarts.helicopter.core.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.EntityBuilder
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.ModelsDefinitions.*
import com.gadarts.helicopter.core.components.child.ChildModel
import com.gadarts.helicopter.core.systems.CameraSystem
import com.gadarts.helicopter.core.systems.InputSystem
import com.gadarts.helicopter.core.systems.RenderSystem
import com.gadarts.helicopter.core.systems.SystemsData


/**
 * The screen of the game itself.
 */
class GamePlayScreen(private val assetsManager: GameAssetManager) : Screen {


    private lateinit var engine: PooledEngine

    override fun show() {
        this.engine = PooledEngine()
        addPlayer()
        val data = SystemsData()
        engine.addSystem(CameraSystem(data))
        engine.addSystem(RenderSystem(data))
        engine.addSystem(InputSystem(data))
    }

    private fun addPlayer() {
        EntityBuilder.initialize(engine)
        EntityBuilder.begin()
            .addModelInstanceComponent(
                assetsManager.getModel(APACHE),
                auxVector.set(2F, 2F, 2F)
            ).addChildModelInstanceComponent(
                listOf(
                    ChildModel(
                        ModelInstance(assetsManager.getModel(PROPELLER)),
                        Vector3.Z,
                        Vector3.Zero
                    ),
                ),
                true
            )
            .finishAndAddToEngine()
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

    companion object {
        private val auxVector = Vector3()

    }
}