package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.graphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.components.ModelInstanceComponent
import com.gadarts.helicopter.core.components.child.ChildModel
import com.gadarts.helicopter.core.systems.render.AxisModelHandler

class RenderSystem(private val data: SystemsData) : EntitySystem() {

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

    private fun renderModels(deltaTime: Float) {
        modelBatch.begin(data.camera)
        axisModelHandler.render(modelBatch)
        for (entity in modelInstanceEntities) {
            renderModel(entity, deltaTime)
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
        child.modelInstance.transform.setTranslation(
            modelInstance.transform.getTranslation(
                auxVector
            )
        )
        val node = child.modelInstance.nodes[0]
        node.isAnimated = true
        node.localTransform.rotate(child.rotationAxis, ROTATION_STEP * deltaTime)
        child.modelInstance.calculateTransforms()
        modelBatch.render(child.modelInstance)
    }

    companion object {
        val auxVector = Vector3()
        const val ROTATION_STEP = 896F
    }
}
