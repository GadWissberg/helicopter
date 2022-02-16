package com.gadarts.helicopter.core.components

import com.badlogic.ashley.core.ComponentMapper
import com.gadarts.helicopter.core.components.child.ChildModelInstanceComponent

object ComponentsMapper {
    val modelInstance: ComponentMapper<ModelInstanceComponent> =
        ComponentMapper.getFor(ModelInstanceComponent::class.java)
    val childModelInstance: ComponentMapper<ChildModelInstanceComponent> =
        ComponentMapper.getFor(ChildModelInstanceComponent::class.java)
    val ambSound: ComponentMapper<AmbSoundComponent> =
        ComponentMapper.getFor(AmbSoundComponent::class.java)
    val player: ComponentMapper<PlayerComponent> =
        ComponentMapper.getFor(PlayerComponent::class.java)
    val arm: ComponentMapper<ArmComponent> =
        ComponentMapper.getFor(ArmComponent::class.java)

}
