package com.mygdx.game

import com.badlogic.gdx.Game

class MyGame : Game() {
    override fun create() {
        val gameScreen = GameScreen()
        setScreen(gameScreen)
    }
}