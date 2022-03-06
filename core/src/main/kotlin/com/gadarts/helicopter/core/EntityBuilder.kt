package com.gadarts.helicopter.core

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.decals.Decal
import com.badlogic.gdx.math.Vector3
import com.gadarts.helicopter.core.components.*
import com.gadarts.helicopter.core.components.child.ChildDecalComponent
import com.gadarts.helicopter.core.components.child.ChildDecal

class EntityBuilder private constructor() {
    fun addModelInstanceComponent(
        model: Model,
        position: Vector3,
    ): EntityBuilder {
        val modelInstanceComponent = engine.createComponent(ModelInstanceComponent::class.java)
        modelInstanceComponent.init(model, position)
        entity!!.add(modelInstanceComponent)
        return instance
    }

    fun addModelInstanceComponent(model: ModelInstance, position: Vector3): EntityBuilder {
        val modelInstanceComponent = engine.createComponent(ModelInstanceComponent::class.java)
        modelInstanceComponent.init(model, position)
        entity!!.add(modelInstanceComponent)
        return instance
    }

    fun finishAndAddToEngine(): Entity {
        engine.addEntity(entity)
        val result = entity
        entity = null
        return result!!
    }

    fun addChildDecalComponent(
        decals: List<ChildDecal>,
        animateRotation: Boolean
    ): EntityBuilder {
        val component = engine.createComponent(ChildDecalComponent::class.java)
        component.init(decals, animateRotation)
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

    fun addPrimaryArmComponent(
        decal: Decal,
        armProperties: ArmProperties,
        priCalculateRelativePosition: ArmComponent.CalculateRelativePosition
    ): EntityBuilder {
        return addArmComponent(
            PrimaryArmComponent::class.java,
            decal,
            armProperties,
            priCalculateRelativePosition
        )
    }

    fun addSecondaryArmComponent(
        decal: Decal,
        armProperties: ArmProperties,
        secCalculateRelativePosition: ArmComponent.CalculateRelativePosition
    ): EntityBuilder {
        return addArmComponent(
            SecondaryArmComponent::class.java,
            decal,
            armProperties,
            secCalculateRelativePosition
        )
    }

    private fun addArmComponent(
        armComponentType: Class<out ArmComponent>,
        decal: Decal,
        armProperties: ArmProperties,
        calculateRelativePosition: ArmComponent.CalculateRelativePosition,
    ): EntityBuilder {
        val armComponent = engine.createComponent(armComponentType)
        armComponent.init(decal, armProperties, calculateRelativePosition)
        entity!!.add(armComponent)
        return instance
    }

    fun addBulletComponent(position: Vector3, speed: Float): EntityBuilder {
        val bulletComponent = engine.createComponent(BulletComponent::class.java)
        bulletComponent.init(position, speed)
        entity!!.add(bulletComponent)
        return instance
    }

    fun addAmbComponent(scale: Vector3, rotation: Float): EntityBuilder {
        val ambComponent = engine.createComponent(AmbComponent::class.java)
        ambComponent.init(scale, rotation)
        entity!!.add(ambComponent)
        return instance
    }

    fun addGroundComponent(): EntityBuilder {
        val groundComponent = engine.createComponent(GroundComponent::class.java)
        entity!!.add(groundComponent)
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
