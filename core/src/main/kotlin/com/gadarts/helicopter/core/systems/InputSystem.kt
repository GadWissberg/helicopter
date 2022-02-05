package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController

class InputSystem(private val data: SystemsData) : GameEntitySystem() {
    private lateinit var debugInput: CameraInputController
    override fun dispose() {

    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        debugInput = CameraInputController(data.camera)
        debugInput.autoUpdate = true
        Gdx.input.inputProcessor = debugInput
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        debugInput.update()
    }
}
