package com.gadarts.helicopter.core.systems.player

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes.Usage.*
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute.createDiffuse
import com.badlogic.gdx.graphics.g3d.decals.Decal.*
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.helicopter.core.DefaultGameSettings
import com.gadarts.helicopter.core.EntityBuilder
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.ModelsDefinitions
import com.gadarts.helicopter.core.assets.SfxDefinitions
import com.gadarts.helicopter.core.assets.TexturesDefinitions
import com.gadarts.helicopter.core.assets.TexturesDefinitions.SPARK_0
import com.gadarts.helicopter.core.assets.TexturesDefinitions.PROPELLER_BLURRED
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.components.child.ChildDecal
import com.gadarts.helicopter.core.systems.GameEntitySystem
import com.gadarts.helicopter.core.systems.HudSystemEventsSubscriber
import com.gadarts.helicopter.core.systems.Notifier
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


/**
 * Responsible for player logic, including reacting to input.
 */
class PlayerSystem : GameEntitySystem(), HudSystemEventsSubscriber,
    Notifier<PlayerSystemEventsSubscriber> {


    private var primaryShooting: Boolean = false
    private lateinit var sparkModel: Model
    private lateinit var propellerBlurredModel: Model
    private var lastTouchDown: Long = 0
    private var tiltAnimationHandler = TiltAnimationHandler()
    private var rotToAdd = 0F
    private var desiredDirectionChanged: Boolean = false
    private lateinit var player: Entity
    private val desiredVelocity = Vector2()
    override val subscribers = HashSet<PlayerSystemEventsSubscriber>()
    override fun initialize(am: GameAssetManager) {
    }

    private fun createPropellerBlurredModel(assetsManager: GameAssetManager) {
        val builder = ModelBuilder()
        builder.begin()
        val material = createFlatMaterial(assetsManager, PROPELLER_BLURRED)
        createFlatMesh(builder, material, "propeller_blurred")
        propellerBlurredModel = builder.end()
    }

    private fun createFlatMesh(
        builder: ModelBuilder,
        material: Material,
        meshName: String
    ) {
        val mbp = builder.part(
            meshName,
            GL20.GL_TRIANGLES,
            (Position or Normal or TextureCoordinates).toLong(),
            material
        )
        mbp.setUVRange(0F, 0F, 1F, 1F)
        mbp.rect(
            -1F, 0F, 1F,
            1F, 0F, 1F,
            1F, 0F, -1F,
            -1F, 0F, -1F,
            0F, 1F, 0F,
        )
    }

    private fun createFlatMaterial(
        assetsManager: GameAssetManager,
        textureDefinition: TexturesDefinitions
    ): Material {
        val material = Material(createDiffuse(assetsManager.getTexture(textureDefinition)))
        material.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
        return material
    }

    override fun dispose() {
        propellerBlurredModel.dispose()
        sparkModel.dispose()
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        createSparkModel(assetsManager)
        createPropellerBlurredModel(assetsManager)
        player = addPlayer(engine as PooledEngine, assetsManager)
        commonData.touchpad.addListener(object : ClickListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                if (TimeUtils.timeSinceMillis(lastTouchDown) <= STRAFE_PRESS_INTERVAL) {
                    activateStrafing()
                } else {
                    deactivateStrafing()
                }
                touchPadTouched(event!!.target)
                lastTouchDown = TimeUtils.millis()
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                touchPadTouched(event!!.target)
                super.touchDragged(event, x, y, pointer)
            }

            override fun touchUp(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ) {
                desiredVelocity.setZero()
                super.touchUp(event, x, y, pointer, button)
            }
        })
    }

    private fun createSparkModel(assetsManager: GameAssetManager) {
        val builder = ModelBuilder()
        builder.begin()
        val material = createFlatMaterial(assetsManager, SPARK_0)
        createFlatMesh(builder, material, "spark")
        sparkModel = builder.end()
    }

    private fun deactivateStrafing() {
        val playerComponent = ComponentsMapper.player.get(player)
        if (playerComponent.strafing != null) {
            val currentVelocity = playerComponent.getCurrentVelocity(auxVector2)
            val newVelocity = currentVelocity.setAngle(playerComponent.strafing!!)
            playerComponent.setCurrentVelocity(newVelocity)
        }
        playerComponent.strafing = null
    }

    private fun activateStrafing() {
        val playerComponent = ComponentsMapper.player.get(player)
        playerComponent.strafing =
            ComponentsMapper.modelInstance.get(player).modelInstance.transform.getRotation(
                auxQuat
            ).getAngleAround(
                Vector3.Y
            )
        tiltAnimationHandler.onStrafeActivated()
    }

    private fun touchPadTouched(actor: Actor) {
        val deltaX = (actor as Touchpad).knobPercentX
        val deltaY = actor.knobPercentY
        if (deltaX != 0F || deltaY != 0F) {
            updateDesiredDirection(deltaX, deltaY)
        } else {
            desiredVelocity.setZero()
        }
    }

    private fun updateDesiredDirection(deltaX: Float, deltaY: Float) {
        desiredVelocity.set(deltaX, deltaY)
        desiredDirectionChanged = true
        updateRotationStep()
    }

    private fun updateRotationStep() {
        if (desiredVelocity.isZero) return
        val playerComponent = ComponentsMapper.player.get(player)
        val diff = desiredVelocity.angle() - playerComponent.getCurrentVelocity(auxVector2).angle()
        val negativeRotation = auxVector2.set(1F, 0F).setAngle(diff).angle() > 180
        rotToAdd = if (negativeRotation && rotToAdd < 0) {
            max(rotToAdd - ROTATION_INCREASE, -MAX_ROTATION_STEP)
        } else if (!negativeRotation && rotToAdd > 0) {
            min(rotToAdd + ROTATION_INCREASE, MAX_ROTATION_STEP)
        } else {
            INITIAL_ROTATION_STEP * (if (negativeRotation) -1F else 1F)
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        handleRotation(deltaTime)
        handleAcceleration()
        takeStep(deltaTime)
        tiltAnimationHandler.update(player)
        handleShooting()
    }

    private fun handleShooting() {
        if (!primaryShooting) return
        val armComponent = ComponentsMapper.arm.get(player)
        val now = TimeUtils.millis()
        if (armComponent.loaded <= now) {
            armComponent.displaySpark = now
            armComponent.loaded = now + PRIMARY_RELOAD_DURATION
            subscribers.forEach { it.onPlayerPrimaryWeaponShot() }
        }
    }

    private fun handleRotation(deltaTime: Float) {
        if (desiredDirectionChanged) {
            if (!desiredVelocity.isZero) {
                calculateRotation(deltaTime)
            }
        } else {
            tiltAnimationHandler.lowerRotationTilt()
        }
        applyRotation()
    }

    private fun takeStep(deltaTime: Float) {
        val playerComponent = ComponentsMapper.player.get(player)
        if (playerComponent.getCurrentVelocity(auxVector2).len2() > 1F) {
            val currentVelocity = playerComponent.getCurrentVelocity(auxVector2)
            val step = auxVector3_1.set(currentVelocity.x, 0F, -currentVelocity.y)
            step.setLength2(step.len2() - 1F).scl(deltaTime)
            ComponentsMapper.modelInstance.get(player).modelInstance.transform.trn(step)
        }
    }

    private fun handleAcceleration() {
        val currentVelocity = ComponentsMapper.player.get(player).getCurrentVelocity(auxVector2)
        if (desiredVelocity.len2() > 0.5F) {
            currentVelocity.setLength2(min(currentVelocity.len2() + (ACCELERATION), MAX_SPEED))
            tiltAnimationHandler.onAcceleration()
        } else {
            currentVelocity.setLength2(max(currentVelocity.len2() - (DECELERATION), 1F))
            tiltAnimationHandler.onDeceleration()
        }
        ComponentsMapper.player.get(player).setCurrentVelocity(currentVelocity)
    }

    private fun calculateRotation(deltaTime: Float) {
        val rotBefore = rotToAdd
        updateRotationStep()
        val currentVelocity = ComponentsMapper.player.get(player).getCurrentVelocity(auxVector2)
        val diff = abs(currentVelocity.angle() - desiredVelocity.angle())
        if ((rotBefore < 0 && rotToAdd < 0) || (rotBefore > 0 && rotToAdd > 0) && diff > ROT_EPSILON) {
            rotate(currentVelocity, deltaTime)
        } else {
            desiredDirectionChanged = false
        }
    }

    private fun rotate(currentVelocity: Vector2, deltaTime: Float) {
        currentVelocity.rotate(rotToAdd * deltaTime)
        ComponentsMapper.player.get(player).setCurrentVelocity(currentVelocity)
        tiltAnimationHandler.onRotation(rotToAdd)
    }

    private fun applyRotation() {
        val transform = ComponentsMapper.modelInstance.get(player).modelInstance.transform
        val position = transform.getTranslation(auxVector3_1)
        val playerComponent = ComponentsMapper.player.get(player)
        val currentVelocity = playerComponent.getCurrentVelocity(auxVector2)
        transform.setToRotation(
            Vector3.Y,
            (if (playerComponent.strafing != null) playerComponent.strafing else currentVelocity.angle())!!
        )
        transform.rotate(Vector3.Z, -IDLE_Z_TILT_DEGREES)
        transform.setTranslation(position)
    }

    private fun addPlayer(engine: PooledEngine, am: GameAssetManager): Entity {
        EntityBuilder.initialize(engine)
        val apacheModel = am.getModel(ModelsDefinitions.APACHE)
        val startPos = auxVector3_1.set(0F, 2F, 2F)
        val entityBuilder = EntityBuilder.begin().addModelInstanceComponent(apacheModel, startPos)
        if (DefaultGameSettings.DISPLAY_PROPELLER) {
            addPropeller(am, entityBuilder)
        }
        val sparkTextureRegion = TextureRegion(am.getTexture(SPARK_0))
        return entityBuilder.addAmbSoundComponent(am.getSound(SfxDefinitions.PROPELLER))
            .addCharacterComponent(INITIAL_HP)
            .addPlayerComponent()
            .addArmComponent(newDecal(SPARK_SIZE, SPARK_SIZE, sparkTextureRegion, true))
            .finishAndAddToEngine()
    }

    private fun addPropeller(
        am: GameAssetManager,
        entityBuilder: EntityBuilder
    ) {
        val propTextureRegion = TextureRegion(am.getTexture(PROPELLER_BLURRED))
        val propDec = newDecal(PROP_SIZE, PROP_SIZE, propTextureRegion, true)
        propDec.rotateX(90F)
        val decals = listOf(ChildDecal(propDec, Vector3.Zero))
        entityBuilder.addChildDecalComponent(decals, true)
    }

    override fun onPrimaryWeaponButtonPressed() {
        primaryShooting = true
    }

    override fun onPrimaryWeaponButtonReleased() {
        primaryShooting = false
    }

    override fun onSecondaryWeaponButtonPressed() {
    }

    override fun onSecondaryWeaponButtonReleased() {
    }

    companion object {
        private const val INITIAL_HP = 100
        private const val MAX_ROTATION_STEP = 200F
        private const val ROTATION_INCREASE = 2F
        private const val INITIAL_ROTATION_STEP = 6F
        private val auxVector3_1 = Vector3()
        private val auxVector2 = Vector2()
        private val auxQuat = Quaternion()
        private const val ROT_EPSILON = 0.5F
        private const val MAX_SPEED = 2F
        private const val ACCELERATION = 0.02F
        private const val DECELERATION = 0.01F
        private const val IDLE_Z_TILT_DEGREES = 12F
        private const val STRAFE_PRESS_INTERVAL = 500
        private const val PRIMARY_RELOAD_DURATION = 125L
        private const val PROP_SIZE = 2F
        private const val SPARK_SIZE = 0.3F
    }
}
