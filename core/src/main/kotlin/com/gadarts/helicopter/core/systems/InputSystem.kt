package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.gadarts.helicopter.core.DefaultGameSettings
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.TexturesDefinitions
import com.gadarts.helicopter.core.assets.TexturesDefinitions.*

class InputSystem(private val data: SystemsData, private val assetsManager: GameAssetManager) :
    GameEntitySystem() {
    private lateinit var debugInput: CameraInputController
    override fun dispose() {

    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        initializeInput()
    }

    private fun initializeInput() {
        if (DefaultGameSettings.DEBUG_INPUT) {
            debugInput = CameraInputController(data.camera)
            debugInput.autoUpdate = true
            Gdx.input.inputProcessor = debugInput
        } else {
            Gdx.input.inputProcessor = data.stage
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (DefaultGameSettings.DEBUG_INPUT) {
            debugInput.update()
        }
    }

    override fun initialize() {
        val ui = (data.stage.actors.first {
            val name = it.name
            name != null && name.equals(SystemsData.UI_TABLE_NAME)
        }) as Table
        addJoystick(ui)
        addWeaponButton(ui, ICON_BULLETS)
        addWeaponButton(ui, ICON_MISSILES)
    }

    private fun addJoystick(ui: Table) {
        val joystickTexture = assetsManager.getTexture(JOYSTICK)
        ui.add(data.touchpad)
            .size(joystickTexture.width.toFloat(), joystickTexture.height.toFloat())
            .pad(0F, JOYSTICK_PADDING_LEFT, JOYSTICK_PADDING_BOTTOM, 0F)
            .growX()
            .left()
    }

    private fun addWeaponButton(ui: Table, iconDefinition: TexturesDefinitions) {
        val up = TextureRegionDrawable(assetsManager.getTexture(BUTTON_UP))
        val down = TextureRegionDrawable(assetsManager.getTexture(BUTTON_DOWN))
        val icon = TextureRegionDrawable(assetsManager.getTexture(iconDefinition))
        val button = ImageButton(ImageButtonStyle(up, down, null, icon, null, null))
        ui.add(button)
    }

    companion object {
        const val JOYSTICK_PADDING_LEFT = 64F
        const val JOYSTICK_PADDING_BOTTOM = 64F
    }
}
