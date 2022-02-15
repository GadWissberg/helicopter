package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.components.PlayerComponent

class CameraSystem(private val data: SystemsData) : GameEntitySystem() {
    private var cameraStrafeMode = Vector3().setZero()
    private var cameraTarget = Vector3()
    private var player: Entity? = null

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        data.camera.update()
        followPlayer()
    }

    private fun followPlayer() {
        if (player != null) {
            val transform = ComponentsMapper.modelInstance.get(player).modelInstance.transform
            val playerPosition = transform.getTranslation(auxVector3_1)
            val playerComp = ComponentsMapper.player.get(player)
            if (playerComp.strafing == null) {
                followPlayerRegularMovement(playerComp, playerPosition)
            } else {
                followPlayerWhenStrafing(playerPosition)
            }
        }
    }

    private fun followPlayerWhenStrafing(playerPosition: Vector3) {
        if (cameraStrafeMode.isZero) {
            cameraStrafeMode.set(data.camera.position).sub(playerPosition)
        } else {
            data.camera.position.set(
                playerPosition.x,
                data.camera.position.y,
                playerPosition.z
            ).add(cameraStrafeMode.x, 0F, cameraStrafeMode.z)
        }
    }

    private fun followPlayerRegularMovement(
        playerComp: PlayerComponent,
        playerPosition: Vector3
    ) {
        val velocityDir = playerComp.getCurrentVelocity(auxVector2).nor().setLength2(5F)
        cameraTarget =
            playerPosition.add(velocityDir.x, 0F, -velocityDir.y + 4F)
        cameraTarget.y = data.camera.position.y
        data.camera.position.interpolate(cameraTarget, 0.2F, Interpolation.exp5)
        if (!cameraStrafeMode.isZero) {
            cameraStrafeMode.setZero()
        }
    }

    override fun initialize(assetsManager: GameAssetManager) {
        player = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get()).first()
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
        val auxVector2 = Vector2()
        val auxVector3_1 = Vector3()
    }

}