package com.gadarts.helicopter.core

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.gadarts.helicopter.core.assets.GameAssetManager
import com.gadarts.helicopter.core.screens.GamePlayScreen


class HelicopterGame(private val androidInterface: AndroidInterface) :
    Game() {

    private lateinit var assetsManager: GameAssetManager

    override fun create() {
        loadAssets()
        Gdx.input.inputProcessor = InputMultiplexer()
        setScreen(GamePlayScreen(assetsManager))
    }

    private fun loadAssets() {
        assetsManager = GameAssetManager()
        assetsManager.loadAssets()
        assetsManager.finishLoading()
    }

}