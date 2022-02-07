package com.gadarts.helicopter.core.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g3d.ModelInstance
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
    private var rotation = 0F
    private val currentDirection = Vector2(1F, 0F)
    private var desiredDirectionChanged: Boolean = false
    private lateinit var player: Entity
    private val desiredDirection = Vector2()

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
                }
            }
        })
    }

    private fun updateDesiredDirection(deltaX: Float, deltaY: Float) {
        desiredDirection.set(deltaX, deltaY)
        desiredDirectionChanged = true
        updateRotationStep()
    }

    private fun updateRotationStep() {
        val diff = desiredDirection.angle() - currentDirection.angle()
        val negativeRotation = auxVector2.set(1F, 0F).setAngle(diff).angle() > 180
        if (negativeRotation && rotation < 0) {
            rotation = max(rotation - ROTATION_INCREASE, -MAX_ROTATION_STEP)
        } else if (!negativeRotation && rotation > 0) {
            rotation = min(rotation + ROTATION_INCREASE, MAX_ROTATION_STEP)
        } else {
            rotation = INITIAL_ROTATION_STEP * (if (negativeRotation) -1F else 1F)
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        handleRotation(deltaTime)
    }

    private fun handleRotation(deltaTime: Float) {
        if (desiredDirectionChanged) {
            val rotBefore = rotation
            updateRotationStep()
            val didNotReachYet = (rotBefore < 0 && rotation < 0) || (rotBefore > 0 && rotation > 0)
            val diff = abs(currentDirection.angle() - desiredDirection.angle())
            if (didNotReachYet && diff > ROTATION_EPSILON
            ) {
                rotate(deltaTime)
            } else {
                desiredDirectionChanged = false
            }
        }
    }

    private fun rotate(deltaTime: Float) {
        val transform = ComponentsMapper.modelInstance.get(player).modelInstance.transform
        val position = transform.getTranslation(auxVector3)
        currentDirection.rotate(rotation * deltaTime)
        transform.setToRotation(Vector3.Y, currentDirection.angle())
        transform.setTranslation(position)
    }

    private fun addPlayer(engine: PooledEngine, assetsManager: GameAssetManager): Entity {
        EntityBuilder.initialize(engine)
        return EntityBuilder.begin()
            .addModelInstanceComponent(
                assetsManager.getModel(ModelsDefinitions.APACHE),
                auxVector3.set(0F, 2F, 2F)
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
        private const val INITIAL_FUEL = 100
        private const val MAX_ROTATION_STEP = 200F
        private const val ROTATION_INCREASE = 2F
        private const val INITIAL_ROTATION_STEP = 6F
        val auxVector3 = Vector3()
        val auxVector2 = Vector2()
        private const val ROTATION_EPSILON = 0.5F
    }
}
