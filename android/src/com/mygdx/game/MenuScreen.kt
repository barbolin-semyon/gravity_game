package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3

/**
 * Класс экрана меню.
 * На вход получает функции [onStart] и [onLevel] которые вызываются при нажатии на кнопки
 *
 * Наследуется от [Screen] и [InputProcessor], переоределяет методы [show], [render], [resize], [dispose]
 *  * а также переопределяет методы [keyDown], [keyUp], [keyTyped], [touchDown], [touchUp], [touchDragged], [mouseMoved], [scrolled]
 *  *
 *  * Мы используем в основном методы:
 *  * [show] - вызывается при открытии экрана
 *  * [render] - вызывается каждый кадр
 *  * [dispose]  - вызывается при закрытии экрана
 *  * [hide] - вызывается при закрытии экрана
 *  * [onTouchDown] - вызывается при нажатии на экран
 *  */
class MenuScreen(
    val onStart: () -> Unit,
    val onLevel: () -> Unit,
) : Screen, InputProcessor {
    private var batch: SpriteBatch = SpriteBatch()

    private var backgroundTexture: Texture = Texture(Gdx.files.internal("background_menu.png"))
    val buttonTexture = Texture(Gdx.files.internal("button_start.png"))
    val buttonSprite = Sprite(buttonTexture)

    val buttonLevelsTexture = Texture(Gdx.files.internal("button_levels.png"))
    val buttonLevelsSprite = Sprite(buttonLevelsTexture)

    val centerY = Gdx.graphics.height / 2f  - buttonSprite.height / 2f// Расположение по центру по оси Y
    val centerX = Gdx.graphics.width / 2f - buttonSprite.width / 2f // Расположение по центру по оси X

    init {
    }

    override fun show() {
        Gdx.input.inputProcessor = this
    }

    override fun render(delta: Float) {
        batch.begin()
        batch.draw(
            backgroundTexture,
            0f,
            0f,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )
        batch.draw(buttonSprite, centerX, centerY + buttonSprite.height / 2f)
        batch.draw(buttonLevelsSprite, centerX, centerY - buttonSprite.height / 2f)
        batch.end()
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
        backgroundTexture.dispose()
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
        val rect = buttonSprite.boundingRectangle
        rect.x = centerX
        rect.y = centerY + buttonSprite.height / 2f
        if (rect.contains(touchPoint.x, touchPoint.y)) {
            onLevel()
        }

        val rectLevels = buttonSprite.boundingRectangle
        rectLevels.x = centerX
        rectLevels.y = centerY - buttonSprite.height / 2f
        if (rectLevels.contains(touchPoint.x, touchPoint.y)) {
            onStart()
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
