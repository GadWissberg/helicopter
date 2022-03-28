package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.gadarts.helicopter.core.GameMap
import com.gadarts.helicopter.core.systems.map.AmbModelDefinitions
import com.gadarts.helicopter.core.systems.map.CharactersDefinitions
import com.gadarts.helicopter.core.systems.map.ElementsDefinitions
import com.gadarts.helicopter.core.systems.map.PlacedElement
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.lang.IllegalArgumentException
import java.util.*

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
        val tilesString = jsonObj.getAsJsonPrimitive(KEY_TILES_MAPPING).asString
        val size = jsonObj.getAsJsonPrimitive(KEY_SIZE).asInt
        val tilesMapping = Array(size) { CharArray(size) }
        for (row in 0 until size) {
            for (col in 0 until size) {
                tilesMapping[row][col] = tilesString[row * size + col]
            }
        }
        return GameMap(tilesMapping, inflateElements(jsonObj))
    }

    private fun inflateElements(jsonObj: JsonObject): List<PlacedElement> {
        val elementsJsonArray = jsonObj.getAsJsonArray(KEY_ELEMENTS)
        return elementsJsonArray.map {
            val asJsonObject = it.asJsonObject
            val definitionName = asJsonObject.get(KEY_DEFINITION).asString
            var definition: ElementsDefinitions? = null
            try {
                definition = CharactersDefinitions.valueOf(definitionName)
            } catch (e: IllegalArgumentException) {
                try {
                    definition = AmbModelDefinitions.valueOf(definitionName.toUpperCase(Locale.ROOT))
                } catch (ignored: IllegalArgumentException) {
                }
            }
            val direction = asJsonObject.get(KEY_DIRECTION).asInt
            val row = asJsonObject.get(KEY_ROW).asInt
            val col = asJsonObject.get(KEY_COL).asInt
            PlacedElement(definition!!, direction, row, col)
        }
    }

    companion object {
        private val gson = Gson()
        private const val KEY_TILES_MAPPING = "tiles"
        private const val KEY_ELEMENTS = "elements"
        private const val KEY_DEFINITION = "definition"
        private const val KEY_DIRECTION = "direction"
        private const val KEY_ROW = "row"
        private const val KEY_COL = "col"
        private const val KEY_SIZE = "size"
    }
}