package com.gadarts.helicopter.core.systems.render

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.components.*
import com.gadarts.helicopter.core.components.child.ChildDecal
import com.gadarts.helicopter.core.components.child.ChildDecalComponent
import com.gadarts.helicopter.core.systems.GameEntitySystem
import com.gadarts.helicopter.core.systems.player.PlayerSystemEventsSubscriber
import kotlin.math.max

class RenderSystem : GameEntitySystem(), Disposable, PlayerSystemEventsSubscriber {

    private lateinit var decalEntities: ImmutableArray<Entity>
    private lateinit var childEntities: ImmutableArray<Entity>
    private lateinit var decalBatch: DecalBatch
    private lateinit var armEntities: ImmutableArray<Entity>
    private lateinit var modelBatch: ModelBatch
    private lateinit var modelInstanceEntities: ImmutableArray<Entity>
    private var axisModelHandler = AxisModelHandler()
    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        modelInstanceEntities = engine!!.getEntitiesFor(
            Family.all(ModelInstanceComponent::class.java)
                .exclude(GroundComponent::class.java)
                .get()
        )
        armEntities = engine.getEntitiesFor(Family.all(PrimaryArmComponent::class.java).get())
        childEntities = engine.getEntitiesFor(Family.all(ChildDecalComponent::class.java).get())
        decalEntities =
            engine.getEntitiesFor(Family.all(IndependentDecalComponent::class.java).get())
        createBatches()
    }

    private fun createBatches() {
        decalBatch = DecalBatch(DECALS_POOL_SIZE, CameraGroupStrategy(commonData.camera))
        modelBatch = ModelBatch()
    }

    private fun resetDisplay(@Suppress("SameParameterValue") color: Color) {
        Gdx.gl.glViewport(0, 0, graphics.width, graphics.height)
        val s = if (graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or s)
        Gdx.gl.glClearColor(color.r, color.g, color.b, 1f)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        resetDisplay(Color.BLACK)
        renderModels()
        renderDecals(deltaTime)
    }

    private fun renderDecals(deltaTime: Float) {
        Gdx.gl.glDepthMask(false)
        renderSparks()
        for (entity in childEntities) {
            renderChildren(entity, deltaTime)
        }
        renderIndependentDecals()
        decalBatch.flush()
        Gdx.gl.glDepthMask(true)
    }

    private fun renderIndependentDecals() {
        for (entity in decalEntities) {
            val decal = ComponentsMapper.independentDecal.get(entity).decal
            faceDecalToCamera(decal)
            decalBatch.add(decal)
        }
    }

    private fun renderSparks() {
        for (entity in armEntities) {
            renderSpark(ComponentsMapper.primaryArm.get(entity))
            renderSpark(ComponentsMapper.secondaryArm.get(entity))
        }
    }

    private fun isVisible(entity: Entity): Boolean {
        val modelInsComp = ComponentsMapper.modelInstance[entity]
        val pos: Vector3 = modelInsComp.modelInstance.transform.getTranslation(auxVector3_1)
        val center: Vector3 = pos.add(modelInsComp.getBoundingBox(auxBox).getCenter(auxVector3_2))
        val dims: Vector3 = auxBox.getDimensions(auxVector3_2)
        dims.x = max(dims.x, max(dims.y, dims.z))
        dims.y = max(dims.x, max(dims.y, dims.z))
        dims.z = max(dims.x, max(dims.y, dims.z))
        return commonData.camera.frustum.boundsInFrustum(center, dims)
    }

    private fun renderModels() {
        modelBatch.begin(commonData.camera)
        axisModelHandler.render(modelBatch)
        for (entity in modelInstanceEntities) {
            renderModel(entity)
        }
        modelBatch.render(commonData.modelCache)
        modelBatch.end()
    }

    private fun renderSpark(armComp: ArmComponent) {
        if (TimeUtils.timeSinceMillis(armComp.displaySpark) <= SPARK_DURATION) {
            val sparkFrames = armComp.armProperties.sparkFrames
            val frame = sparkFrames[MathUtils.random(sparkFrames.size - 1)]
            if (armComp.sparkDecal.textureRegion != frame) {
                armComp.sparkDecal.textureRegion = frame
            }
            faceDecalToCamera(armComp.sparkDecal)
            decalBatch.add(armComp.sparkDecal)
        }
    }

    private fun faceDecalToCamera(decal: Decal) {
        val camera = commonData.camera
        decal.lookAt(auxVector3_1.set(decal.position).sub(camera.direction), camera.up)
    }

    private fun renderModel(entity: Entity) {
        if (isVisible(entity)) {
            val modelInstance = ComponentsMapper.modelInstance.get(entity).modelInstance
            modelBatch.render(modelInstance)
        }
    }

    private fun renderChildren(
        entity: Entity,
        deltaTime: Float,
    ) {
        val childComponent = ComponentsMapper.childDecal.get(entity)
        val children = childComponent.decals
        val modelInstance = ComponentsMapper.modelInstance.get(entity).modelInstance
        val parentPosition = modelInstance.transform.getTranslation(auxVector3_1)
        val parentRotation = modelInstance.transform.getRotation(auxQuat)
        for (child in children) {
            renderChild(child, parentRotation, deltaTime, parentPosition)
        }
    }

    private fun renderChild(
        child: ChildDecal,
        parentRotation: Quaternion?,
        deltaTime: Float,
        parentPosition: Vector3?
    ) {
        child.decal.rotation = parentRotation
        child.decal.rotateX(90F)
        child.decal.rotateZ(child.rotationStep.angle())
        child.rotationStep.setAngle(child.rotationStep.angle() + ROT_STEP * deltaTime)
        child.decal.position = parentPosition
        decalBatch.add(child.decal)
    }

    override fun initialize(am: GameAssetManager) {
    }

    override fun resume(delta: Long) {

    }

    override fun dispose() {
        modelBatch.dispose()
    }

    override fun onPlayerWeaponShot(
        player: Entity,
        bulletModelInstance: ModelInstance,
        arm: ArmComponent
    ) {

    }

    override fun onPlayerEnteredNewRegion(player: Entity) {
    }

    companion object {
        val auxVector3_1 = Vector3()
        val auxVector3_2 = Vector3()
        val auxQuat = Quaternion()
        val auxBox = BoundingBox()
        const val ROT_STEP = 1600F
        const val SPARK_DURATION = 40L
        const val DECALS_POOL_SIZE = 200
    }

}
