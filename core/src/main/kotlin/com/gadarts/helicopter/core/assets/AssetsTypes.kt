package com.gadarts.helicopter.core.assets

enum class AssetsTypes(
    val assets: Array<out AssetDefinition<*>> = arrayOf(),
    private val loadedUsingLoader: Boolean = true
) {
    TEXTURES(TexturesDefinitions.values()),
    SHADERS(ShaderDefinitions.values(), loadedUsingLoader = false),
    FONTS(FontsDefinitions.values()),
    MODELS(ModelsDefinitions.values()),
    SFX(SfxDefinitions.values()),
    MAPS;

    fun isLoadedUsingLoader(): Boolean {
        return loadedUsingLoader
    }

}