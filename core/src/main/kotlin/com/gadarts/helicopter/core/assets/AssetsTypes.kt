package com.gadarts.helicopter.core.assets

enum class AssetsTypes(
    val assets: Array<out AssetDefinition<*>>,
    private val loadedUsingLoader: Boolean
) {
    TEXTURES(TexturesDefinitions.values(), true),
    SHADERS(ShaderDefinitions.values(), false),
    FONTS(FontsDefinitions.values(), true);

    fun isLoadedUsingLoader(): Boolean {
        return loadedUsingLoader
    }

}