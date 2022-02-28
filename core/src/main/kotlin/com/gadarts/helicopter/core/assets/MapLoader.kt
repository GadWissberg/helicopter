package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.gadarts.helicopter.core.GameMap
import com.gadarts.helicopter.core.systems.MapSystem.Companion.MAP_SIZE
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
        val tilesMapping = Array(MAP_SIZE) { CharArray(MAP_SIZE) }
        val mapJsonObject = gson.fromJson(file!!.reader(), JsonObject::class.java)
        val tilesMappingString =
            mapJsonObject.getAsJsonPrimitive(KEY_TILES_MAPPING).asString.replace("\n", "")
        for (row in 0 until MAP_SIZE) {
            for (col in 0 until MAP_SIZE) {
                tilesMapping[row][col] = tilesMappingString[row * MAP_SIZE + col]
            }
        }
        return GameMap(tilesMapping)
    }

    companion object {
        private val gson = Gson()
        private const val KEY_TILES_MAPPING = "tiles_mapping"
    }
}