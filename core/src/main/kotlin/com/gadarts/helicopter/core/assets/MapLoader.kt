package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.gadarts.helicopter.core.GameMap
import com.google.gson.Gson
import com.google.gson.JsonObject

class MapLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<GameMap, MapLoaderParameter>(resolver) {
    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: MapLoaderParameter?
    ): Array<AssetDescriptor<Any>> {
        return Array()
    }

    override fun loadAsync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: MapLoaderParameter?
    ) {
    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: MapLoaderParameter?
    ): GameMap {
        val jsonObj = gson.fromJson(file!!.reader(), JsonObject::class.java)
        val asJsonPrimitive = jsonObj.getAsJsonPrimitive(KEY_TILES_MAPPING).asString
        val width = asJsonPrimitive.indexOf('\n')
        val depth = asJsonPrimitive.count { it == '\n' }
        val tilesString = asJsonPrimitive.replace("\n", "")
        val tilesMapping = Array(depth) { CharArray(width) }
        for (row in 0 until depth) {
            for (col in 0 until width) {
                tilesMapping[row][col] = tilesString[row * width + col]
            }
        }
        return GameMap(tilesMapping)
    }

    companion object {
        private val gson = Gson()
        private const val KEY_TILES_MAPPING = "tiles_mapping"
    }
}