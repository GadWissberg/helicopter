package com.gadarts.helicopter.core.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
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
        val diff = desiredDirection.angle() - currentDirection.angle()
        rotation = if (auxVector2.set(1F, 0F).setAngle(diff).angle() > 180) {
            -1F
        } else {
            1F
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        handleRotation()
    }

    private fun handleRotation() {
        if (desiredDirectionChanged) {
            Gdx.app.log("current", "${currentDirection.angle()}")
            Gdx.app.log("desired", "${desiredDirection.angle()}")
            if (abs(currentDirection.angle() - desiredDirection.angle()) > 0.5F) {
                rotate()
            } else {
                desiredDirectionChanged = false
            }
        }
    }

    private fun rotate() {
        val transform = ComponentsMapper.modelInstance.get(player).modelInstance.transform
        val position = transform.getTranslation(auxVector3)
        currentDirection.rotate(rotation)
        transform.setToRotation(Vector3.Y, currentDirection.angle())
        transform.setTranslation(position)
    }

    private fun addPlayer(engine: PooledEngine, assetsManager: GameAssetManager): Entity {
        EntityBuilder.initialize(engine)
        return EntityBuilder.begin()
            .addModelInstanceComponent(
                assetsManager.getModel(ModelsDefinitions.APACHE),
                auxVector3.set(2F, 2F, 2F)
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
            .addCharacterComponent(PLAYER_INITIAL_HP)
            .addPlayerComponent()
            .finishAndAddToEngine()
    }

    companion object {
        private const val PLAYER_INITIAL_HP = 100
        private const val PLAYER_INITIAL_FUEL = 100
        val auxVector3 = Vector3()
        val auxVector2 = Vector2()

    }
}
