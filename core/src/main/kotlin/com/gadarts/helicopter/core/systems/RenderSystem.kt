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
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.components.ModelInstanceComponent
import com.gadarts.helicopter.core.systems.render.AxisModelHandler

class RenderSystem(private val data: SystemsData) : EntitySystem() {

    private lateinit var modelBatch: ModelBatch
    private lateinit var modelInstanceEntities: ImmutableArray<Entity>
    private var axisModelHandler = AxisModelHandler()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        val family = Family.all(ModelInstanceComponent::class.java).get()
        modelInstanceEntities = getEngine().getEntitiesFor(family)
        val mb = ModelBuilder()
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
        modelBatch.begin(data.camera)
        axisModelHandler.render(modelBatch)
        for (entity in modelInstanceEntities) {
            modelBatch.render(ComponentsMapper.modelInstance.get(entity).modelInstance)
        }
        modelBatch.end()
    }

    private fun createArrowEntity(
        modelBuilder: ModelBuilder,
        color: Color,
        direction: Vector3
    ): Entity? {
        val material = Material(ColorAttribute.createDiffuse(color))
        val attributes = VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal
        val model = modelBuilder.createArrow(Vector3.Zero, direction, material, attributes.toLong())
        val modelInstanceComponent = engine.createComponent(ModelInstanceComponent::class.java)
        modelInstanceComponent.init(ModelInstance(model))
        return engine.createEntity().add(modelInstanceComponent)
    }

    companion object {
        val auxVector3_1 = Vector3()
    }
}
