package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3

/**
 * Класс экрана выбора уроня.
 * На вход получает [GameViewModel] и [onSetLevel] - функцию для установки уровня
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
class LevelsScreen(val viewModel: GameViewModel, val onSetLevel: (Int) -> Unit) : Screen,
    InputProcessor {
    private var batch: SpriteBatch = SpriteBatch()

    private var backgroundTexture: Texture = Texture(Gdx.files.internal("background_menu.png"))
    val button1Texture = Texture(Gdx.files.internal("1.png"))
    val button1Sprite = Sprite(button1Texture)

    val button2Texture = Texture(Gdx.files.internal("2.png"))
    val button2Sprite = Sprite(button2Texture)


    val button3Texture = Texture(Gdx.files.internal("3.png"))
    val button3Sprite = Sprite(button3Texture)


    val button4Texture = Texture(Gdx.files.internal("4.png"))
    val button4Sprite = Sprite(button4Texture)


    val button5Texture = Texture(Gdx.files.internal("5.png"))
    val button5Sprite = Sprite(button5Texture)


    val centerY =
        Gdx.graphics.height / 2f - button1Sprite.height / 2f// Расположение по центру по оси Y
    val centerX =
        Gdx.graphics.width / 2f - button1Sprite.width / 2f // Расположение по центру по оси X

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
        batch.draw(button1Texture, centerX - (button1Sprite.width + 10f) * 2, centerY)

        if (viewModel.maxLevel >= 2) {
            batch.draw(button2Texture, centerX - (button1Sprite.width + 10f), centerY)
        }

        if (viewModel.maxLevel >= 3) {
            batch.draw(button3Texture, centerX, centerY)
        }

        if (viewModel.maxLevel >= 4) {
            batch.draw(button4Texture, centerX + (button1Sprite.width + 10f), centerY)
        }

        if (viewModel.maxLevel >= 5) {
            batch.draw(button5Texture, centerX + (button1Sprite.width + 10f) * 2, centerY)
        }
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
        val rect1 = button1Sprite.boundingRectangle
        rect1.x = centerX - (button1Sprite.width + 10f) * 2
        rect1.y = centerY

        if (rect1.contains(touchPoint.x, touchPoint.y)) {
            onSetLevel(1)
        }

        val rect2 = button2Sprite.boundingRectangle
        rect2.x = centerX - (button1Sprite.width + 10f)
        rect2.y = centerY
        if (rect2.contains(touchPoint.x, touchPoint.y)) {
            onSetLevel(2)
        }

        val rect3 = button3Sprite.boundingRectangle
        rect3.x = centerX
        rect3.y = centerY
        if (rect3.contains(touchPoint.x, touchPoint.y)) {
            onSetLevel(3)
        }

        val rect4 = button4Sprite.boundingRectangle
        rect4.x = centerX + (button1Sprite.width + 10f)
        rect4.y = centerY
        if (rect4.contains(touchPoint.x, touchPoint.y)) {
            onSetLevel(4)
        }

        val rect5 = button5Sprite.boundingRectangle
        rect5.x = centerX + (button1Sprite.width + 10f) * 2
        rect5.y = centerY
        if (rect5.contains(touchPoint.x, touchPoint.y)) {
            onSetLevel(5)
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
