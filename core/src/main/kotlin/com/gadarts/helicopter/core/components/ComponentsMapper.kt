package com.gadarts.helicopter.core.components

import com.badlogic.ashley.core.ComponentMapper

object ComponentsMapper {
    val modelInstance: ComponentMapper<ModelInstanceComponent> =
        ComponentMapper.getFor(ModelInstanceComponent::class.java)

}
