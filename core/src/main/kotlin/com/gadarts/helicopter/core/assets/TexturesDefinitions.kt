package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.graphics.Texture
import java.util.Locale.ROOT

enum class TexturesDefinitions(ninepatch: Boolean = false) : AssetDefinition<Texture> {
    JOYSTICK,
    JOYSTICK_CENTER,
    BUTTON_UP,
    BUTTON_DOWN,
    ICON_BULLETS,
    ICON_MISSILES;

    private var path: String =
        "textures/${(if (ninepatch) "$name.9" else name).toLowerCase(ROOT)}.png"

    override fun getPath(): String {
        return path
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