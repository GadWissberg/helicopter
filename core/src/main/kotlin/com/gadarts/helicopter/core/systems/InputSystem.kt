package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.gadarts.helicopter.core.DefaultGameSettings
import com.gadarts.helicopter.core.assets.GameAssetManager

class InputSystem : GameEntitySystem() {


    private lateinit var debugInput: CameraInputController

    override fun dispose() {

    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        initializeInput()
    }

    private fun initializeInput() {
        if (DefaultGameSettings.DEBUG_INPUT) {
            debugInput = CameraInputController(commonData.camera)
            debugInput.autoUpdate = true
            Gdx.input.inputProcessor = debugInput
        } else {
            Gdx.input.inputProcessor = commonData.stage
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (DefaultGameSettings.DEBUG_INPUT) {
            debugInput.update()
        }
    }

    override fun initialize(am: GameAssetManager) {

    }

    companion object

}
