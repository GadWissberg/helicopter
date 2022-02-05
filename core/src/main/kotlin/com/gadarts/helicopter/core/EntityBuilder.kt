package com.gadarts.helicopter.core

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.components.AmbSoundComponent
import com.gadarts.helicopter.core.components.CharacterComponent
import com.gadarts.helicopter.core.components.child.ChildModelInstanceComponent
import com.gadarts.helicopter.core.components.ModelInstanceComponent
import com.gadarts.helicopter.core.components.PlayerComponent
import com.gadarts.helicopter.core.components.child.ChildModel

class EntityBuilder private constructor() {
    fun addModelInstanceComponent(model: Model, position: Vector3): EntityBuilder {
        val modelInstanceComponent = engine.createComponent(ModelInstanceComponent::class.java)
        modelInstanceComponent.init(model, position)
        entity!!.add(modelInstanceComponent)
        return instance
    }

    fun finishAndAddToEngine() {
        engine.addEntity(entity)
        entity = null
    }

    fun addChildModelInstanceComponent(
        models: List<ChildModel>,
        animateRotation: Boolean
    ): EntityBuilder {
        val component = engine.createComponent(ChildModelInstanceComponent::class.java)
        component.init(models, animateRotation)
        entity!!.add(component)
        return instance

    }

    fun addAmbSoundComponent(sound: Sound): EntityBuilder {
        val ambSoundComponent = engine.createComponent(AmbSoundComponent::class.java)
        ambSoundComponent.init(sound)
        entity!!.add(ambSoundComponent)
        return instance
    }

    fun addCharacterComponent(hp: Int): EntityBuilder {
        val characterComponent = engine.createComponent(CharacterComponent::class.java)
        characterComponent.init(hp)
        entity!!.add(characterComponent)
        return instance
    }

    fun addPlayerComponent(): EntityBuilder {
        val characterComponent = engine.createComponent(PlayerComponent::class.java)
        characterComponent.init()
        entity!!.add(characterComponent)
        return instance
    }

    companion object {
        private lateinit var instance: EntityBuilder
        var entity: Entity? = null
        lateinit var engine: PooledEngine
        fun begin(): EntityBuilder {
            entity = engine.createEntity()
            return instance
        }

        fun initialize(engine: PooledEngine) {
            this.engine = engine
            this.instance = EntityBuilder()
        }

    }
}
