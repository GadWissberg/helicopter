package com.gadarts.helicopter.core.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.gadarts.helicopter.core.EntityBuilder
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.ModelsDefinitions
import com.gadarts.helicopter.core.assets.SfxDefinitions
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.components.child.ChildModel
import com.gadarts.helicopter.core.systems.GameEntitySystem
import com.gadarts.helicopter.core.systems.SystemsData
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class PlayerSystem(private val data: SystemsData, private val assetsManager: GameAssetManager) :
    GameEntitySystem() {
    private var accelerationTiltDegrees: Float = 0.0f
    private var rotTiltDegrees: Float = 0.0f
    private var rotation = 0F
    private val currentVelocity = Vector2(1F, 0F)
    private var desiredDirectionChanged: Boolean = false
    private lateinit var player: Entity
    private val desiredVelocity = Vector2()

    override fun dispose() {
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        player = addPlayer(engine as PooledEngine, assetsManager)
        data.touchpad.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val deltaX = (actor as Touchpad).knobPercentX
                val deltaY = actor.knobPercentY
                if (deltaX != 0F || deltaY != 0F) {
                    updateDesiredDirection(deltaX, deltaY)
                } else {
                    desiredVelocity.setZero()
                }
            }
        })
    }

    private fun updateDesiredDirection(deltaX: Float, deltaY: Float) {
        desiredVelocity.set(deltaX, deltaY)
        desiredDirectionChanged = true
        updateRotationStep()
    }

    private fun updateRotationStep() {
        val diff = desiredVelocity.angle() - currentVelocity.angle()
        val negativeRotation = auxVector2.set(1F, 0F).setAngle(diff).angle() > 180
        rotation = if (negativeRotation && rotation < 0) {
            max(rotation - ROTATION_INCREASE, -MAX_ROTATION_STEP)
        } else if (!negativeRotation && rotation > 0) {
            min(rotation + ROTATION_INCREASE, MAX_ROTATION_STEP)
        } else {
            INITIAL_ROTATION_STEP * (if (negativeRotation) -1F else 1F)
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        handleRotation(deltaTime)
        handleAcceleration()
        takeStep(deltaTime)
        handleMovementTilt()
    }

    private fun handleRotation(deltaTime: Float) {
        if (desiredDirectionChanged) {
            calculateRotation(deltaTime)
        } else {
            lowerRotationTilt()
        }
        applyRotation()
    }

    private fun lowerRotationTilt() {
        if (rotTiltDegrees > 0) {
            rotTiltDegrees = max(rotTiltDegrees - ROT_TILT_DEC_STEP_SIZE, 0F)
        } else if (rotTiltDegrees < 0) {
            rotTiltDegrees = min(rotTiltDegrees + ROT_TILT_DEC_STEP_SIZE, 0F)
        }
    }

    private fun handleMovementTilt() {
        val transform = ComponentsMapper.modelInstance.get(player).modelInstance.transform
        if (accelerationTiltDegrees > 0) {
            transform.rotate(
                Vector3.Z,
                -accelerationTiltDegrees
            )
        }
        transform.rotate(
            Vector3.X,
            rotTiltDegrees
        )
    }

    private fun takeStep(deltaTime: Float) {
        if (currentVelocity.len2() > 1F) {
            val step = auxVector3_1.set(currentVelocity.x, 0F, -currentVelocity.y)
            step.setLength2(step.len2() - 1F).scl(deltaTime)
            ComponentsMapper.modelInstance.get(player).modelInstance.transform.trn(step)
        }
    }

    private fun handleAcceleration() {
        accelerationTiltDegrees = if (desiredVelocity.len2() > 0.5F) {
            currentVelocity.setLength2(min(currentVelocity.len2() + (ACCELERATION), MAX_SPEED))
            min(accelerationTiltDegrees + ACC_TILT_STEP_SIZE, ACC_TILT_RELATIVE_MAX_DEGREES)
        } else {
            currentVelocity.setLength2(max(currentVelocity.len2() - (DECELERATION), 1F))
            max(accelerationTiltDegrees - ACC_TILT_STEP_SIZE, 0F)
        }
    }

    private fun calculateRotation(deltaTime: Float) {
        val rotBefore = rotation
        updateRotationStep()
        val diff = abs(currentVelocity.angle() - desiredVelocity.angle())
        if ((rotBefore < 0 && rotation < 0) || (rotBefore > 0 && rotation > 0) && diff > ROT_EPSILON) {
            currentVelocity.rotate(rotation * deltaTime)
            increaseRotationTilt()
        } else {
            desiredDirectionChanged = false
        }
    }

    private fun increaseRotationTilt() {
        rotTiltDegrees += if (rotation > 0) -ROT_TILT_STEP_SIZE else ROT_TILT_STEP_SIZE
        rotTiltDegrees = MathUtils.clamp(rotTiltDegrees, -ROT_TILT_MAX_DEG, ROT_TILT_MAX_DEG)
    }

    private fun applyRotation() {
        val transform = ComponentsMapper.modelInstance.get(player).modelInstance.transform
        val position = transform.getTranslation(auxVector3_1)
        transform.setToRotation(Vector3.Y, currentVelocity.angle())
        transform.rotate(Vector3.Z, -IDLE_Z_TILT_DEGREES)
        transform.setTranslation(position)
    }

    private fun addPlayer(engine: PooledEngine, assetsManager: GameAssetManager): Entity {
        EntityBuilder.initialize(engine)
        return EntityBuilder.begin()
            .addModelInstanceComponent(
                assetsManager.getModel(ModelsDefinitions.APACHE),
                auxVector3_1.set(0F, 2F, 2F)
            ).addChildModelInstanceComponent(
                listOf(
                    ChildModel(
                        ModelInstance(assetsManager.getModel(ModelsDefinitions.PROPELLER)),
                        Vector3.Z,
                        Vector3.Zero
                    ),
                ),
                true
            ).addAmbSoundComponent(assetsManager.getSound(SfxDefinitions.PROPELLER))
            .addCharacterComponent(INITIAL_HP)
            .addPlayerComponent()
            .finishAndAddToEngine()
    }

    companion object {
        private const val INITIAL_HP = 100
        private const val MAX_ROTATION_STEP = 200F
        private const val ROTATION_INCREASE = 2F
        private const val INITIAL_ROTATION_STEP = 6F
        val auxVector3_1 = Vector3()
        val auxVector2 = Vector2()
        private const val ROT_EPSILON = 0.5F
        private const val MAX_SPEED = 2F
        private const val ACCELERATION = 0.02F
        private const val DECELERATION = 0.01F
        private const val ACC_TILT_RELATIVE_MAX_DEGREES = 6F
        private const val ACC_TILT_STEP_SIZE = 0.5F
        private const val ROT_TILT_MAX_DEG = 20F
        private const val ROT_TILT_STEP_SIZE = 1F
        private const val ROT_TILT_DEC_STEP_SIZE = 0.5F
        private const val IDLE_Z_TILT_DEGREES = 10F
    }
}
