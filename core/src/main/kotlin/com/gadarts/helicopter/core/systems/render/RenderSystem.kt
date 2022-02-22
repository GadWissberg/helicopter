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
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.components.ArmComponent
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.components.ModelInstanceComponent
import com.gadarts.helicopter.core.components.child.ChildDecal
import com.gadarts.helicopter.core.components.child.ChildDecalComponent
import com.gadarts.helicopter.core.systems.GameEntitySystem
import com.gadarts.helicopter.core.systems.player.PlayerSystemEventsSubscriber
import kotlin.math.max

class RenderSystem : GameEntitySystem(), Disposable, PlayerSystemEventsSubscriber {


    private lateinit var childrenEntities: ImmutableArray<Entity>
    private lateinit var decalBatch: DecalBatch
    private lateinit var armEntities: ImmutableArray<Entity>
    private lateinit var modelBatch: ModelBatch
    private lateinit var modelInstanceEntities: ImmutableArray<Entity>
    private var axisModelHandler = AxisModelHandler()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        decalBatch = DecalBatch(DECALS_POOL_SIZE, CameraGroupStrategy(commonData.camera))
        val modelInstanceFamily = Family.all(ModelInstanceComponent::class.java).get()
        modelInstanceEntities = getEngine().getEntitiesFor(modelInstanceFamily)
        val armFamily = Family.all(ArmComponent::class.java).get()
        armEntities = getEngine().getEntitiesFor(armFamily)
        val childrenFamily = Family.all(ChildDecalComponent::class.java).get()
        childrenEntities = getEngine().getEntitiesFor(childrenFamily)
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
        for (entity in armEntities) {
            renderSpark(entity)
        }
        for (entity in childrenEntities) {
            renderChildren(entity, deltaTime)
        }
        decalBatch.flush()
        Gdx.gl.glDepthMask(true)
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
            if (isVisible(entity)) {
                renderModel(entity)
            }
        }
        modelBatch.end()
    }

    private fun renderSpark(entity: Entity) {
        val armComp = ComponentsMapper.arm.get(entity)
        if (TimeUtils.timeSinceMillis(armComp.displaySpark) <= SPARK_DURATION) {
            val modelInstance = ComponentsMapper.modelInstance.get(entity).modelInstance
            val sparkDecal = positionSpark(armComp, modelInstance)
            decalBatch.add(sparkDecal)
        }
    }

    private fun positionSpark(
        armComp: ArmComponent,
        modelInstance: ModelInstance
    ): Decal {
        val decal = armComp.sparkDecal
        decal.position = modelInstance.transform.getTranslation(auxVector3_1)
        decal.position.add(
            auxVector3_1.set(1F, 0F, 0F)
                .rot(modelInstance.transform)
                .scl(SPARK_FORWARD_BIAS)
        )
        decal.position.y -= SPARK_HEIGHT_BIAS
        return decal
    }

    private fun renderModel(entity: Entity?) {
        val modelInstance = ComponentsMapper.modelInstance.get(entity).modelInstance
        modelBatch.render(modelInstance)
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

    override fun dispose() {
        modelBatch.dispose()
    }

    override fun onPlayerPrimaryWeaponShot() {
    }

    companion object {
        val auxVector3_1 = Vector3()
        val auxVector3_2 = Vector3()
        val auxQuat = Quaternion()
        val auxBox = BoundingBox()
        const val ROT_STEP = 1600F
        const val SPARK_DURATION = 40L
        const val DECALS_POOL_SIZE = 200
        const val SPARK_HEIGHT_BIAS = 0.37F
        const val SPARK_FORWARD_BIAS = 0.55F
    }
}
