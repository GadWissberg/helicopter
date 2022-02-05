package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.math.Vector3

class CameraSystem(private val data: SystemsData) : GameEntitySystem() {
    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        data.camera.update()
    }

    override fun dispose() {
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        initializeCamera()
    }

    private fun initializeCamera() {
        data.camera.near = NEAR
        data.camera.far = FAR
        data.camera.update()
        data.camera.position.set(0F, INITIAL_Y, INITIAL_Z)
        data.camera.rotate(Vector3.X, -45F)
    }

    companion object {
        const val NEAR = 0.1F
        const val FAR = 300F
        const val INITIAL_Y = 5F
        const val INITIAL_Z = 5F
    }

}