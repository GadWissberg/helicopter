package com.gadarts.helicopter.core.components.child

import com.gadarts.helicopter.core.components.GameComponent

class ChildDecalComponent : GameComponent() {
    private var animateRotation: Boolean = false
    var decals = ArrayList<ChildDecal>()

    fun init(decals: List<ChildDecal>, animateRotation: Boolean) {
        this.decals.clear()
        this.decals.addAll(decals)
        this.animateRotation = animateRotation
    }

    override fun reset() {
    }

}
