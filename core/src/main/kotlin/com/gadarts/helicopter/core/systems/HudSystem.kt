package com.gadarts.helicopter.core.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.gadarts.helicopter.core.DefaultGameSettings

class HudSystem(private val data: SystemsData) : GameEntitySystem() {
    override fun initialize() {

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
}
