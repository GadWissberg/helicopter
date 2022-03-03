package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.graphics.Texture

enum class TexturesDefinitions(fileNames: Int = 1, ninepatch: Boolean = false) :
    AssetDefinition<Texture> {

    JOYSTICK,
    JOYSTICK_CENTER,
    BUTTON_UP,
    BUTTON_DOWN,
    ICON_BULLETS,
    ICON_MISSILES,
    PROPELLER_BLURRED,
    SPARK(3),
    SAND,
    SAND_DEC(4),
    VERTICAL,
    HORIZONTAL,
    CROSS,
    HORIZONTAL_BOTTOM,
    HORIZONTAL_TOP,
    VERTICAL_LEFT,
    VERTICAL_RIGHT,
    LEFT_TO_BOTTOM,
    RIGHT_TO_BOTTOM,
    LEFT_TO_TOP,
    RIGHT_TO_TOP,
    RIGHT_END,
    LEFT_END,
    TOP_END,
    BOTTOM_END;

    private val paths = ArrayList<String>()

    init {
        initializePaths("textures/${(if (ninepatch) "%s.9" else "%s")}.png", fileNames)
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