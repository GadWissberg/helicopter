package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.audio.Sound

enum class SfxDefinitions : AssetDefinition<Sound> {

    PROPELLER,
    MACHINE_GUN,
    MISSILE;

    private val paths = ArrayList<String>()

    init {
        initializePaths("sfx/%s.wav")
    }

    override fun getPaths(): ArrayList<String> {
        return paths
    }

    override fun getParameters(): AssetLoaderParameters<Sound>? {
        return null
    }

    override fun getClazz(): Class<Sound> {
        return Sound::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }
}