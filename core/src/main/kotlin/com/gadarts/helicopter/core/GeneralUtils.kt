package com.gadarts.helicopter.core

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.MathUtils
import com.gadarts.helicopter.core.components.ComponentsMapper
import com.gadarts.helicopter.core.systems.CharacterSystem

object GeneralUtils {
    fun calculateVolumeAccordingToPosition(entity: Entity, camera: PerspectiveCamera): Float {
        val transform = ComponentsMapper.modelInstance.get(entity).modelInstance.transform
        val position = transform.getTranslation(CharacterSystem.auxVector1)
        var distance = MathUtils.clamp(camera.position.dst2(position), 15F, 64F)
        distance = 1 - MathUtils.norm(15F, 64F, distance)
        return distance
    }

}