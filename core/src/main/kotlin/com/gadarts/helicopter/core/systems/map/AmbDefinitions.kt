package com.gadarts.helicopter.core.systems.map

enum class AmbDefinitions(
    val symbol: Char,
    val randomizeScale: Boolean = false,
    val randomizeRotation: Boolean = false
) {
    PALM_TREE('P', true, true),
    ROCK('R', true, true),
    BUILDING('B'),
    FENCE('F'),
    LIGHT_POLE('L'),
    BARRIER('-'),
}