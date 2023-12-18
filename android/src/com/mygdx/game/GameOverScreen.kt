package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3

class GameOverScreen(
    val onRestart: () -> Unit,
    val onMenu: () -> Unit
) : Screen, InputProcessor {
    private var batch: SpriteBatch = SpriteBatch()
    private var font: BitmapFont = BitmapFont()
    private var backgroundTexture: Texture = Texture(Gdx.files.internal("background_game_over.png"))
    private var music: Music = Gdx.audio.newMusic(Gdx.files.internal("game_over.mp3"))

    var buttonMenuTexture = Texture(Gdx.files.internal("button_menu.png"))
    val buttonMenuSprite = Sprite(buttonMenuTexture)

    var buttonRestartTexture = Texture(Gdx.files.internal("button_restart.png"))
    val buttonRestartSprite = Sprite(buttonRestartTexture)

    val centerY = Gdx.graphics.height / 2f  - buttonMenuSprite.height / 2f// Расположение по центру по оси Y
    val centerX = Gdx.graphics.width / 2f - buttonMenuSprite.width / 2f // Расположение по центру по оси X


    override fun show() {
        Gdx.input.inputProcessor = this
        music.play()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()

        // Отображение фона
        batch.draw(
            backgroundTexture,
            0f,
            0f,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )

        batch.draw(buttonRestartSprite, centerX, centerY)
        batch.draw(buttonMenuSprite, centerX, centerY - buttonRestartSprite.height)
        batch.end()

        /*if (Gdx.input.isTouched) {
            game.screen = GameScreen(Gdx.graphics.width)
            dispose()
        }*/
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
        music.stop()
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        backgroundTexture.dispose()
        buttonRestartTexture.dispose()
        buttonMenuTexture.dispose()
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val touchPoint = Vector3(screenX.toFloat(), screenY.toFloat(), 0f)
        val rect = buttonRestartSprite.boundingRectangle
        rect.x = centerX
        rect.y = centerY
        if (rect.contains(touchPoint.x, touchPoint.y)) {
            onRestart()
        }

        val rectMenu = buttonMenuSprite.boundingRectangle
        rectMenu.x = centerX
        rectMenu.y = centerY + buttonRestartSprite.height
        if (rectMenu.contains(touchPoint.x, touchPoint.y)) {
            onMenu()
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }
}
