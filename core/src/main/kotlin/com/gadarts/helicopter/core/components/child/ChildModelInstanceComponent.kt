package com.gadarts.helicopter.core.components.child

import com.gadarts.helicopter.core.components.GameComponent

class ChildModelInstanceComponent : GameComponent() {
    private var animateRotation: Boolean = false
    var modelInstances = ArrayList<ChildModel>()

    fun init(models: List<ChildModel>, animateRotation: Boolean) {
        modelInstances.clear()
        modelInstances.addAll(models)
        this.animateRotation = animateRotation
    }

    override fun reset() {
    }

}
