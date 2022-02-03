package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import java.util.*

enum class ShaderDefinitions : AssetDefinition<String> {
    ;

    private var path: String = "shaders/${name.toLowerCase(Locale.ROOT)}.shader"

    override fun getPath(): String {
        return path
    }

    override fun getParameters(): AssetLoaderParameters<String>? {
        return null
    }

    override fun getClazz(): Class<String> {
        return String::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }
}