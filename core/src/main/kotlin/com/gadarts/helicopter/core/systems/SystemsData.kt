package com.gadarts.helicopter.core.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera

class SystemsData {
    val camera: PerspectiveCamera = PerspectiveCamera(
        FOV,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat()
    )

    companion object {
        const val FOV = 67F
    }
}
