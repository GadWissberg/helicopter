package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.audio.Sound
import java.util.Locale.ROOT

enum class SfxDefinitions : AssetDefinition<Sound> {
    PROPELLER,
    MACHINE_GUN;

    private var path: String = "sfx/${name.toLowerCase(ROOT)}.wav"

    override fun getPath(): String {
        return path
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