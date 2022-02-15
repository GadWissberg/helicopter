package com.gadarts.helicopter.core.systems

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
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.utils.Disposable
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.components.ModelInstanceComponent
import com.gadarts.helicopter.core.components.child.ChildModel
import com.gadarts.helicopter.core.systems.render.AxisModelHandler
import kotlin.math.max

class RenderSystem(private val data: SystemsData) : GameEntitySystem(), Disposable {

    private lateinit var modelBatch: ModelBatch
    private lateinit var modelInstanceEntities: ImmutableArray<Entity>
    private var axisModelHandler = AxisModelHandler()
    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        val family = Family.all(ModelInstanceComponent::class.java).get()
        modelInstanceEntities = getEngine().getEntitiesFor(family)
        modelBatch = ModelBatch()
    }

    private fun resetDisplay(color: Color) {
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
        val pos: Vector3 = modelInsComp.modelInstance.transform.getTranslation(auxVector_1)
        val center: Vector3 = pos.add(modelInsComp.getBoundingBox(auxBox).getCenter(auxVector_2))
        val dims: Vector3 = auxBox.getDimensions(auxVector_2)
        dims.x = max(dims.x, max(dims.y, dims.z))
        dims.y = max(dims.x, max(dims.y, dims.z))
        dims.z = max(dims.x, max(dims.y, dims.z))
        return data.camera.frustum.boundsInFrustum(center, dims)
    }

    private fun renderModels(deltaTime: Float) {
        modelBatch.begin(data.camera)
        axisModelHandler.render(modelBatch)
        for (entity in modelInstanceEntities) {
            if (isVisible(entity)) {
                renderModel(entity, deltaTime)
            }
        }
        modelBatch.end()
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
        transform.setTranslation(modelInstance.transform.getTranslation(auxVector_1))
        val node = child.modelInstance.nodes[0]
        node.isAnimated = true
        node.localTransform.rotate(child.rotationAxis, ROT_STEP * deltaTime)
        child.modelInstance.calculateTransforms()
    }

    override fun initialize(assetsManager: GameAssetManager) {

    }

    override fun dispose() {
        modelBatch.dispose()
    }

    companion object {
        val auxVector_1 = Vector3()
        val auxVector_2 = Vector3()
        val auxQuat = Quaternion()
        val auxBox = BoundingBox()
        const val ROT_STEP = 896F
    }
}
