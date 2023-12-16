package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch


class GameScreen : Screen, InputProcessor { // Здесь будет логика для отображения игры и обработки ввода
    private var platformTexture: Texture = Texture(Gdx.files.internal("c_64_tex.png"))
    private var ballTexture: Texture = Texture(Gdx.files.internal("c_64_tex.png"))
    private var platformX: Float = 0.toFloat()
    private var ballSprite: Sprite = Sprite(ballTexture)
    private var ballXSpeed: Float = 5f
    private var ballYSpeed: Float = 5f
    private var font: BitmapFont = BitmapFont()
    private var batch: SpriteBatch = SpriteBatch()

    init {
        ballSprite.setPosition(200f, 300f)
    }


    override fun show() {
        Gdx.input.inputProcessor = this
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Обработка движения платформы
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            platformX -= 10
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            platformX += 10
        }

        // Обновление позиции шарика
        ballSprite.setPosition(ballSprite.x + ballXSpeed, ballSprite.y + ballYSpeed)

        // Обработка отскоков шарика от стенок
        if (ballSprite.x < 0 || ballSprite.x + ballSprite.width > Gdx.graphics.width) {
            ballXSpeed = -ballXSpeed
        }
        if (ballSprite.y < 0 || ballSprite.y + ballSprite.height > Gdx.graphics.height) {
            ballYSpeed = -ballYSpeed
        }

        // Обработка отскока шарика от платформы
        if (ballSprite.y < platformTexture.height && ballSprite.x > platformX && ballSprite.x < platformX + platformTexture.width) {
            ballYSpeed = -ballYSpeed
        }

        // Отображение платформы и шарика
        batch.begin()
        batch.draw(platformTexture, platformX, 0f)
        ballSprite.draw(batch)
        batch.end()

        // Проверка завершения игры при достижении нижней границы
        if (ballSprite.y < 0) {
            Gdx.input.inputProcessor = null
            batch.begin()
            font.draw(batch, "Game Over", 100f, 150f)
            batch.end()
        }
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> platformX -= 10 // Примерное значение - нужно настроить под размеры экрана
            Input.Keys.RIGHT -> platformX += 10 // Примерное значение - нужно настроить под размеры экрана
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }
}