package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.gadarts.helicopter.core.DefaultGameSettings
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.assets.TexturesDefinitions
import com.gadarts.helicopter.core.assets.TexturesDefinitions.*

class HudSystem(private val data: SystemsData) : GameEntitySystem<HudSystemEventsSubscriber>() {

    private val priWeaponButtonClickListener = object : ClickListener() {
        override fun touchDown(
            event: InputEvent,
            x: Float,
            y: Float,
            pointer: Int,
            button: Int
        ): Boolean {
            subscribers.forEach { it.onPrimaryWeaponButtonPressed() }
            return super.touchDown(event, x, y, pointer, button)
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            subscribers.forEach { it.onPrimaryWeaponButtonReleased() }
            super.touchUp(event, x, y, pointer, button)
        }
    }

    private val secWeaponButtonClickListener = object : ClickListener() {
        override fun touchDown(
            event: InputEvent,
            x: Float,
            y: Float,
            pointer: Int,
            button: Int
        ): Boolean {
            subscribers.forEach { it.onSecondaryWeaponButtonPressed() }
            return super.touchDown(event, x, y, pointer, button)
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            subscribers.forEach { it.onSecondaryWeaponButtonReleased() }
            super.touchUp(event, x, y, pointer, button)
        }
    }

    override val subscribers = HashSet<HudSystemEventsSubscriber>()

    override fun initialize(am: GameAssetManager) {
        val ui = (data.stage.actors.first {
            val name = it.name
            name != null && name.equals(SystemsData.UI_TABLE_NAME)
        }) as Table
        addJoystick(ui, am)
        addWeaponButton(am, ui, ICON_BULLETS, clickListener = priWeaponButtonClickListener)
        addWeaponButton(am, ui, ICON_MISSILES, JOYSTICK_PADDING_LEFT, secWeaponButtonClickListener)
    }

    private fun addJoystick(ui: Table, assetsManager: GameAssetManager) {
        val joystickTexture = assetsManager.getTexture(JOYSTICK)
        ui.add(data.touchpad)
            .size(joystickTexture.width.toFloat(), joystickTexture.height.toFloat())
            .pad(0F, JOYSTICK_PADDING_LEFT, JOYSTICK_PADDING_BOTTOM, 0F)
            .growX()
            .left()
    }

    private fun addWeaponButton(
        assetsManager: GameAssetManager,
        ui: Table,
        iconDefinition: TexturesDefinitions,
        rightPadding: Float = 0F,
        clickListener: ClickListener
    ) {
        val up = TextureRegionDrawable(assetsManager.getTexture(BUTTON_UP))
        val down = TextureRegionDrawable(assetsManager.getTexture(BUTTON_DOWN))
        val icon = TextureRegionDrawable(assetsManager.getTexture(iconDefinition))
        val button = ImageButton(ImageButton.ImageButtonStyle(up, down, null, icon, null, null))
        ui.add(button)
        if (rightPadding != 0F) {
            ui.pad(0F, 0F, 0F, rightPadding)
        }
        button.addListener(clickListener)
    }

    override fun dispose() {

    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        val uiTable = Table()
        uiTable.debug(if (DefaultGameSettings.UI_DEBUG) Table.Debug.all else Table.Debug.none)
        uiTable.name = SystemsData.UI_TABLE_NAME
        uiTable.setFillParent(true)
        uiTable.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        uiTable.align(Align.bottom)
        data.stage.addActor(uiTable)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        data.stage.act(deltaTime)
        data.stage.draw()
    }

    companion object {
        const val JOYSTICK_PADDING_LEFT = 64F
        const val JOYSTICK_PADDING_BOTTOM = 64F
    }
}
