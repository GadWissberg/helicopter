package com.gadarts.helicopter.core.components

import com.badlogic.gdx.graphics.g3d.decals.Decal

class IndependentDecalComponent : GameComponent() {
    lateinit var decal: Decal

    override fun reset() {

    }

    fun init(decal: Decal) {
        this.decal = decal
    }

}
