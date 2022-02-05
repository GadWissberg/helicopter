package com.gadarts.helicopter.core.components

import com.badlogic.ashley.core.Component
import com.gadarts.helicopter.core.components.child.ChildModel

class ChildModelInstanceComponent : Component {
    var animateRotation: Boolean = false
    var modelInstances = ArrayList<ChildModel>()

    fun init(models: List<ChildModel>, animateRotation: Boolean) {
        modelInstances.clear()
        modelInstances.addAll(models)
        this.animateRotation = animateRotation
    }

}
