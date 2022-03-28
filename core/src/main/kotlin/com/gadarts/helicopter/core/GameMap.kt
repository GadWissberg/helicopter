package com.gadarts.helicopter.core

import com.gadarts.helicopter.core.systems.map.PlacedElement

class GameMap(val tilesMapping: Array<CharArray>, val placedElements: List<PlacedElement>) {
    companion object {
        const val TILE_TYPE_EMPTY = '0'
    }
}
