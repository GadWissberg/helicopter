package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.graphics.Texture
import java.util.ArrayList
import java.util.Locale.ROOT

enum class TexturesDefinitions(ninepatch: Boolean = false) : AssetDefinition<Texture> {

    JOYSTICK,
    JOYSTICK_CENTER,
    BUTTON_UP,
    BUTTON_DOWN,
    ICON_BULLETS,
    ICON_MISSILES,
    PROPELLER_BLURRED,
    SPARK_0,
    SPARK_1,
    SPARK_2;

    private val paths = ArrayList<String>()

    init {
        initializePaths(
            "textures/${(if (ninepatch) "%s.9" else "%s")}.png"
        )
    }

    override fun getPaths(): ArrayList<String> {
        return paths
    }

    override fun getParameters(): AssetLoaderParameters<Texture>? {
        return null
    }

    override fun getClazz(): Class<Texture> {
        return Texture::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }
}