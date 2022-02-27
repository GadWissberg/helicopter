package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.TexturesDefinitions.JOYSTICK
import com.gadarts.helicopter.core.assets.TexturesDefinitions.JOYSTICK_CENTER

class CommonData(assetsManager: GameAssetManager) {
    lateinit var player: Entity
    var touchpad: Touchpad
    val camera: PerspectiveCamera = PerspectiveCamera(
        FOV,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat()
    )
    val stage: Stage = Stage()

    init {
        val joystickTexture = assetsManager.getAssetByDefinition(JOYSTICK)
        val joystickDrawableTex = TextureRegionDrawable(joystickTexture)
        val joystickCenterTex =
            TextureRegionDrawable(assetsManager.getAssetByDefinition(JOYSTICK_CENTER))
        touchpad = Touchpad(
            DEAD_ZONE,
            Touchpad.TouchpadStyle(joystickDrawableTex, joystickCenterTex)
        )
    }


    companion object {
        const val DEAD_ZONE = 15F
        const val FOV = 67F
        const val UI_TABLE_NAME = "ui_table"
        const val SPARK_FORWARD_BIAS = 0.55F
        const val SPARK_HEIGHT_BIAS = 0.37F
    }
}
