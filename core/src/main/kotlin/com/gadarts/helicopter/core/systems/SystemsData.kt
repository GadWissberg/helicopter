package com.gadarts.helicopter.core.systems

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.TexturesDefinitions

class SystemsData(assetsManager: GameAssetManager) {
    var touchpad: Touchpad
    val camera: PerspectiveCamera = PerspectiveCamera(
        FOV,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat()
    )
    val stage: Stage = Stage()

    init {
        val joystickTexture = assetsManager.getTexture(TexturesDefinitions.JOYSTICK)
        val joystickDrawableTex = TextureRegionDrawable(joystickTexture)
        val joystickCenterTex =
            TextureRegionDrawable(assetsManager.getTexture(TexturesDefinitions.JOYSTICK_CENTER))
        touchpad = Touchpad(
            DEAD_ZONE,
            Touchpad.TouchpadStyle(joystickDrawableTex, joystickCenterTex)
        )
    }


    companion object {
        const val DEAD_ZONE = 15F
        const val FOV = 67F
        const val UI_TABLE_NAME = "ui_table"

    }
}
