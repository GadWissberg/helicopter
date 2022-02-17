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
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.TimeUtils
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.components.ArmComponent
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.components.ModelInstanceComponent
import com.gadarts.helicopter.core.components.child.ChildModel
import com.gadarts.helicopter.core.systems.GameEntitySystem
import com.gadarts.helicopter.core.systems.player.PlayerSystemEventsSubscriber
import kotlin.math.max

class RenderSystem : GameEntitySystem(), Disposable, PlayerSystemEventsSubscriber {


    private lateinit var armEntities: ImmutableArray<Entity>
    private lateinit var modelBatch: ModelBatch
    private lateinit var modelInstanceEntities: ImmutableArray<Entity>
    private var axisModelHandler = AxisModelHandler()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        val modelInstanceFamily = Family.all(ModelInstanceComponent::class.java).get()
        modelInstanceEntities = getEngine().getEntitiesFor(modelInstanceFamily)
        val armFamily = Family.all(ArmComponent::class.java).get()
        armEntities = getEngine().getEntitiesFor(armFamily)
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
        renderModels(deltaTime)
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

    private fun renderModels(deltaTime: Float) {
        modelBatch.begin(commonData.camera)
        axisModelHandler.render(modelBatch)
        for (entity in modelInstanceEntities) {
            if (isVisible(entity)) {
                renderModel(entity, deltaTime)
                renderSpark(entity)
            }
        }
        modelBatch.end()
    }

    private fun renderSpark(entity: Entity) {
        if (ComponentsMapper.arm.has(entity)) {
            val armComponent = ComponentsMapper.arm.get(entity)
            if (TimeUtils.timeSinceMillis(armComponent.displaySpark) <= SPARK_DURATION) {
                val modelInstance = ComponentsMapper.modelInstance.get(entity).modelInstance
                modelInstance.transform.getRotation(auxQuat)
                armComponent.modelInstance.transform.getScale(auxVector3_2)
                armComponent.modelInstance.transform
                    .setToTranslation(modelInstance.transform.getTranslation(auxVector3_1))
                    .scale(auxVector3_2.x, auxVector3_2.y, auxVector3_2.z)
                    .rotate(auxQuat)
                    .translate(2F, 0F, 0F)
                modelBatch.render(armComponent.modelInstance)
            }
        }
    }

    private fun renderModel(entity: Entity?, deltaTime: Float) {
        val modelInstance = ComponentsMapper.modelInstance.get(entity).modelInstance
        modelBatch.render(modelInstance)
        renderChildren(entity, modelInstance, deltaTime)
    }

    private fun renderChildren(
        entity: Entity?,
        modelInstance: ModelInstance,
        deltaTime: Float
    ) {
        if (ComponentsMapper.childModelInstance.has(entity)) {
            val childComponent = ComponentsMapper.childModelInstance.get(entity)
            val children = childComponent.modelInstances
            for (child in children) {
                renderChild(child, deltaTime, modelInstance)
            }
        }
    }

    private fun renderChild(
        child: ChildModel,
        deltaTime: Float,
        modelInstance: ModelInstance
    ) {
        updateChildTransformation(modelInstance, child, deltaTime)
        modelBatch.render(child.modelInstance)
    }

    private fun updateChildTransformation(
        modelInstance: ModelInstance,
        child: ChildModel,
        deltaTime: Float
    ) {
        val parentRot = modelInstance.transform.getRotation(auxQuat)
        val transform = child.modelInstance.transform
        transform.setFromEulerAnglesRad(parentRot.yawRad, parentRot.pitchRad, parentRot.rollRad)
        transform.setTranslation(modelInstance.transform.getTranslation(auxVector3_1))
        val node = child.modelInstance.nodes[0]
        node.isAnimated = true
        node.localTransform.rotate(child.rotationAxis, ROT_STEP * deltaTime)
        child.modelInstance.calculateTransforms()
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
        val auxVector2_1 = Vector2()
        val auxQuat = Quaternion()
        val auxBox = BoundingBox()
        const val ROT_STEP = 1600F
        const val SPARK_DURATION = 75L
    }
}
