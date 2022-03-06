package com.gadarts.helicopter.core.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.*
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.helicopter.core.GameMap
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.systems.CommonData.Companion.REGION_SIZE
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class PlayerMovementHandler {

    private var tiltAnimationHandler = TiltAnimationHandler()
    private var rotToAdd = 0F
    private var desiredDirectionChanged: Boolean = false
    private val desiredVelocity = Vector2()

    fun onTouchUp() {
        desiredVelocity.setZero()
    }

    private fun handleRotation(deltaTime: Float, player: Entity) {
        if (desiredDirectionChanged && !desiredVelocity.isZero) {
            calculateRotation(deltaTime, player)
        } else {
            tiltAnimationHandler.lowerRotationTilt()
        }
        applyRotation(player)
    }

    private fun handleAcceleration(player: Entity) {
        val currentVelocity =
            ComponentsMapper.player.get(player).getCurrentVelocity(auxVector2_1)
        if (desiredVelocity.len2() > 0.5F) {
            currentVelocity.setLength2(
                min(
                    currentVelocity.len2() + (ACCELERATION),
                    MAX_SPEED
                )
            )
            tiltAnimationHandler.onAcceleration()
        } else {
            currentVelocity.setLength2(
                max(
                    currentVelocity.len2() - (DECELERATION),
                    1F
                )
            )
            tiltAnimationHandler.onDeceleration()
        }
        ComponentsMapper.player.get(player).setCurrentVelocity(currentVelocity)
    }

    private fun calculateRotation(deltaTime: Float, player: Entity) {
        val rotBefore = rotToAdd
        updateRotationStep(player)
        val currentVelocity =
            ComponentsMapper.player.get(player).getCurrentVelocity(auxVector2_1)
        val diff = abs(currentVelocity.angle() - desiredVelocity.angle())
        if ((rotBefore < 0 && rotToAdd < 0) || (rotBefore > 0 && rotToAdd > 0) && diff > ROT_EPSILON) {
            rotate(currentVelocity, deltaTime, player)
        } else {
            desiredDirectionChanged = false
        }
    }

    private fun rotate(currentVelocity: Vector2, deltaTime: Float, player: Entity) {
        currentVelocity.rotate(rotToAdd * deltaTime)
        ComponentsMapper.player.get(player).setCurrentVelocity(currentVelocity)
        tiltAnimationHandler.onRotation(rotToAdd)
    }

    private fun updateRotationStep(player: Entity) {
        if (desiredVelocity.isZero) return
        val playerComponent = ComponentsMapper.player.get(player)
        val diff =
            desiredVelocity.angle() - playerComponent.getCurrentVelocity(auxVector2_1)
                .angle()
        val negativeRotation = auxVector2_1.set(1F, 0F).setAngle(diff).angle() > 180
        rotToAdd = if (negativeRotation && rotToAdd < 0) {
            max(rotToAdd - ROTATION_INCREASE, -MAX_ROTATION_STEP)
        } else if (!negativeRotation && rotToAdd > 0) {
            min(rotToAdd + ROTATION_INCREASE, MAX_ROTATION_STEP)
        } else {
            INITIAL_ROTATION_STEP * (if (negativeRotation) -1F else 1F)
        }
    }

    private fun activateStrafing(player: Entity) {
        val playerComponent = ComponentsMapper.player.get(player)
        playerComponent.strafing =
            ComponentsMapper.modelInstance.get(player).modelInstance.transform.getRotation(
                auxQuat
            ).getAngleAround(
                Vector3.Y
            )
        tiltAnimationHandler.onStrafeActivated()
    }

    fun onTouchPadTouched(deltaX: Float, deltaY: Float, player: Entity) {
        if (deltaX != 0F || deltaY != 0F) {
            updateDesiredDirection(deltaX, deltaY, player)
        } else {
            desiredVelocity.setZero()
        }
    }

    private fun updateDesiredDirection(deltaX: Float, deltaY: Float, player: Entity) {
        desiredVelocity.set(deltaX, deltaY)
        desiredDirectionChanged = true
        updateRotationStep(player)
    }

    fun update(
        player: Entity,
        deltaTime: Float,
        subscribers: HashSet<PlayerSystemEventsSubscriber>,
        currentMap: GameMap
    ) {
        handleRotation(deltaTime, player)
        handleAcceleration(player)
        takeStepWithRegionCheck(player, deltaTime, subscribers, currentMap)
        tiltAnimationHandler.update(player)
    }

    private fun takeStepWithRegionCheck(
        player: Entity,
        deltaTime: Float,
        subscribers: HashSet<PlayerSystemEventsSubscriber>,
        currentMap: GameMap
    ) {
        val transform = ComponentsMapper.modelInstance.get(player).modelInstance.transform
        val currentPosition = transform.getTranslation(auxVector3_2)
        val prevHorizontalIndex = floor(currentPosition.x / REGION_SIZE)
        val prevVerticalIndex = floor(currentPosition.z / REGION_SIZE)
        takeStep(deltaTime, player, currentMap)
        val newPosition = transform.getTranslation(auxVector3_2)
        val newHorizontalIndex = floor(newPosition.x / REGION_SIZE)
        val newVerticalIndex = floor(newPosition.z / REGION_SIZE)
        if (prevHorizontalIndex != newHorizontalIndex || prevVerticalIndex != newVerticalIndex) {
            subscribers.forEach { it.onPlayerEnteredNewRegion(player) }
        }
    }

    fun onTouchDown(lastTouchDown: Long, player: Entity) {
        if (TimeUtils.timeSinceMillis(lastTouchDown) <= STRAFE_PRESS_INTERVAL) {
            activateStrafing(player)
        } else {
            deactivateStrafing(player)
        }
    }

    private fun deactivateStrafing(player: Entity) {
        val playerComponent = ComponentsMapper.player.get(player)
        if (playerComponent.strafing != null) {
            val currentVelocity = playerComponent.getCurrentVelocity(auxVector2_1)
            val newVelocity = currentVelocity.setAngle(playerComponent.strafing!!)
            playerComponent.setCurrentVelocity(newVelocity)
        }
        playerComponent.strafing = null
    }

    private fun takeStep(deltaTime: Float, player: Entity, currentMap: GameMap) {
        val playerComponent = ComponentsMapper.player.get(player)
        if (playerComponent.getCurrentVelocity(auxVector2_1).len2() > 1F) {
            val currentVelocity = playerComponent.getCurrentVelocity(auxVector2_1)
            val step = auxVector3_1.set(currentVelocity.x, 0F, -currentVelocity.y)
            step.setLength2(step.len2() - 1F).scl(deltaTime)
            val transform = ComponentsMapper.modelInstance.get(player).modelInstance.transform
            transform.trn(step)
            clampPosition(transform, currentMap)
        }
    }

    private fun clampPosition(
        transform: Matrix4,
        currentMap: GameMap
    ) {
        val newPos = transform.getTranslation(auxVector3_1)
        newPos.x = MathUtils.clamp(newPos.x, 0F, currentMap.tilesMapping.size.toFloat())
        newPos.z = MathUtils.clamp(newPos.z, 0F, currentMap.tilesMapping[0].size.toFloat())
        transform.setTranslation(newPos)
    }

    private fun applyRotation(player: Entity) {
        val transform = ComponentsMapper.modelInstance.get(player).modelInstance.transform
        val position = transform.getTranslation(auxVector3_1)
        val playerComponent = ComponentsMapper.player.get(player)
        val currentVelocity = playerComponent.getCurrentVelocity(auxVector2_1)
        transform.setToRotation(
            Vector3.Y,
            (if (playerComponent.strafing != null) playerComponent.strafing else currentVelocity.angle())!!
        )
        transform.rotate(Vector3.Z, -IDLE_Z_TILT_DEGREES)
        transform.setTranslation(position)
    }

    companion object {
        private const val MAX_ROTATION_STEP = 200F
        private const val ROTATION_INCREASE = 2F
        private const val INITIAL_ROTATION_STEP = 6F
        private val auxVector2_1 = Vector2()
        private val auxVector3_1 = Vector3()
        private val auxVector3_2 = Vector3()
        private val auxQuat = Quaternion()
        private const val ROT_EPSILON = 0.5F
        private const val MAX_SPEED = 14F
        private const val ACCELERATION = 0.04F
        private const val DECELERATION = 0.06F
        private const val IDLE_Z_TILT_DEGREES = 12F
        private const val STRAFE_PRESS_INTERVAL = 500
    }
}
