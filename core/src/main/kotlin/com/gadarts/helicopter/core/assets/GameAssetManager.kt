package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.gadarts.helicopter.core.GameMap
import java.io.File.*
import java.util.*

/**
 * Responsible to load the assets.
 */
open class GameAssetManager : AssetManager() {

    /**
     * Loads all assets sync.
     */
    fun loadAssets() {
        initializeCustomLoaders()
        AssetsTypes.values().forEach { type ->
            if (type.isLoadedUsingLoader()) {
                if (type.assets.isNotEmpty()) {
                    type.assets.forEach { asset ->
                        if (asset.getParameters() != null) {
                            load(
                                asset.getPaths().first(),
                                BitmapFont::class.java,
                                (asset.getParameters() as FreetypeFontLoader.FreeTypeFontLoaderParameter)
                            )
                        } else {
                            asset.getPaths().forEach { load(it, asset.getClazz()) }
                        }
                    }
                } else {
                    val toLowerCase = type.name.toLowerCase(Locale.ROOT)
                    val dir = Gdx.files.internal(toLowerCase)
                    dir.list().forEach {
                        load(
                            "$toLowerCase$separatorChar${it.name()}",
                            GameMap::class.java
                        )
                    }
                }
            } else {
                type.assets.forEach { asset ->
                    asset.getPaths().forEach {
                        addAsset(
                            it,
                            String::class.java,
                            Gdx.files.internal(it).readString()
                        )
                    }
                }
            }
        }
    }

    private fun initializeCustomLoaders() {
        val resolver: FileHandleResolver = InternalFileHandleResolver()
        setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        val loader = FreetypeFontLoader(resolver)
        setLoader(BitmapFont::class.java, "ttf", loader)
        val mapLoader = MapLoader(resolver)
        setLoader(GameMap::class.java, "json", mapLoader)
    }

    inline fun <reified T> getAssetByDefinition(definition: AssetDefinition<T>): T {
        return get(definition.getPaths().random(), T::class.java)
    }

    inline fun <reified T> getAssetByDefinitionAndIndex(definition: AssetDefinition<T>, i: Int): T {
        return get(definition.getPaths()[i], T::class.java)
    }
}