package com.gadarts.helicopter.core

class GameMap(val tilesMapping: Array<CharArray>) {
    companion object {
        const val TILE_TYPE_EMPTY = '0'
        const val TILE_TYPE_ROAD = '#'
    }
}
