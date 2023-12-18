package com.mygdx.game

import android.util.Log
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import kotlin.concurrent.schedule
import kotlin.properties.Delegates


/**
 * Класс экрана игры.
 * На вход получает [GameViewModel] и [onGameOver] и [onWin], которые вызываются при завершении игры: выигрыше и проигрыше
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
class GameScreen(
    val viewModel: GameViewModel,
    val onGameOver: () -> Unit,
    val onWin: () -> Unit
) : Screen,
    InputProcessor {
    val font = BitmapFont().apply {
        data.setScale(10f)
    }

    // Инициализация текстур
    private var platformTexture: Texture = Texture(Gdx.files.internal("platform.png"))
    private var platformLongTexture: Texture = Texture(Gdx.files.internal("long_platform.png"))
    private var ballTexture: Texture = Texture(Gdx.files.internal("ball.png"))
    private var backgroundTexture: Texture = Texture(Gdx.files.internal("background.jpg"))
    // Монеты
    private var coins: MutableList<Vector2> = mutableListOf()
    private var coinTexture: Texture = Texture(Gdx.files.internal("coin.png"))

    // Красные монеты
    private var coinsRed: MutableList<Vector2> = mutableListOf()
    private var coinRedTexture: Texture = Texture(Gdx.files.internal("red_coin.png"))

    // Зеленые монеты
    private var coinsGreen: MutableList<Vector2> = mutableListOf()
    private var coinGreenTexture: Texture = Texture(Gdx.files.internal("coin_green.png"))
    private var countToEndEffectsOfGreen: Int = 0

    // Создание срайтов
    private var ballSprite: Sprite = Sprite(ballTexture)
    private var batch: SpriteBatch = SpriteBatch()
    private var platformSprite: Sprite = Sprite(platformTexture)

    // Создание необходимых переменных, инициализация ленивая (поздняя)
    private var ballXSpeed by Delegates.notNull<Float>()
    private var ballYSpeed by Delegates.notNull<Float>()
    private var currentTouchX: Int? = null
    private var countCoins by Delegates.notNull<Int>()
    private var countRedCoin by Delegates.notNull<Int>()
    private var countGreenCoin by Delegates.notNull<Int>()
    private var speed by Delegates.notNull<Float>()

    // Создание переменных для текста счета
    var text = ""
    val textX = 10f // Координата X для текста
    val textY = Gdx.graphics.height - 10f // Координата Y для текста (от верхнего края экрана)

    // Звук
    private var coinSound: Sound = Gdx.audio.newSound(Gdx.files.internal("coin_sound.mp3"))
    private var music: Music = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"))

    override fun show() {
        // Инициализация всех переменных
        countCoins = viewModel.params.countCoins
        countRedCoin = viewModel.params.countRedCoin
        countGreenCoin = viewModel.params.countGreenCoin
        speed = viewModel.params.speed
        ballXSpeed = speed
        ballYSpeed = speed
        text = "0 / ${countCoins + countRedCoin}"

        // Включение отслеживания активности юзера (клики, перемещения и тд)
        Gdx.input.inputProcessor = this

        // Устанока начального значения платформы и шаров
        platformSprite.x = Gdx.graphics.width / 2f
        ballSprite.setPosition(200f, 300f)

        // Создание монет в случайных позициях
        for (i in 0 until countCoins) {
            coins.add(
                Vector2(
                    MathUtils.random(
                        0f + ballSprite.width,
                        Gdx.graphics.width.toFloat() - ballSprite.width
                    ),
                    MathUtils.random(
                        0f + platformSprite.height,
                        Gdx.graphics.height.toFloat() - ballSprite.height
                    )
                )
            )
        }

        // Создание красных монет в случайных позициях
        for (i in 0 until countRedCoin) {
            coinsRed.add(
                Vector2(
                    MathUtils.random(
                        0f + ballSprite.width,
                        Gdx.graphics.width.toFloat() - ballSprite.width
                    ),
                    MathUtils.random(
                        0f + platformSprite.height,
                        Gdx.graphics.height.toFloat() - ballSprite.height
                    )
                )
            )
        }

        // Создание зеленых монет в случайных позициях
        for (i in 0 until countGreenCoin) {
            coinsGreen.add(
                Vector2(
                    MathUtils.random(
                        0f + ballSprite.width,
                        Gdx.graphics.width.toFloat() - ballSprite.width
                    ),
                    MathUtils.random(
                        0f + platformSprite.height,
                        Gdx.graphics.height.toFloat() - ballSprite.height
                    )
                )
            )
        }

        // Настройка звука (ставим - повторение после окончания и включаем музыку)
        music.isLooping = true
        music.play()
    }

    override fun render(delta: Float) {
        // Чистка экрана - пустое полотно
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Обработка сбора монет
        coins.forEach { coin ->
            // Если монета внутри координат шара, то...
            if (
                ballSprite.y + ballSprite.height / 2 > coin.y &&
                ballSprite.y - ballSprite.height / 2 < coin.y &&
                ballSprite.x + ballSprite.width / 2 > coin.x &&
                ballSprite.x - ballSprite.width / 2 < coin.x
            ) {
                // Включаем звук монеты
                coinSound.play()

                // Удаляем данную монету из списка
                coins = coins.filter { it.x != coin.x && it.y != coin.y }.toMutableList()

                // Обновляем текст счета
                text =
                    "${countCoins + countRedCoin - (coins.size + coinsRed.size)} / ${countCoins + countRedCoin}"

                // Уменьшаем скорость, если она выше заданной
                if (ballXSpeed > speed) {
                    ballXSpeed -= 10f
                }
                if (ballXSpeed < -speed) {
                    ballXSpeed += 10f
                }
                if (ballYSpeed > speed) {
                    ballYSpeed -= 10f
                }
                if (ballYSpeed < -speed) {
                    ballYSpeed += 10f
                }

                // Уменьшаем счет монет (countToEndEffectsOfGreen считает 3 монеты после попадания
                // зеленой монеты и убирает эффект большой платформы)
                countToEndEffectsOfGreen--

                // Если данный счет равен нулю, то мы уменьшаем платформу (буст закончился)
                if (countToEndEffectsOfGreen == 0) {
                    platformSprite = Sprite(platformTexture)
                    platformSprite.x = coin.x
                }
            }
        }

        // Аналогично для красных монет, только тут скорость увеличиваем
        coinsRed.forEach { coin ->
            if (
                ballSprite.y + ballSprite.height / 2 > coin.y &&
                ballSprite.y - ballSprite.height / 2 < coin.y &&
                ballSprite.x + ballSprite.width / 2 > coin.x &&
                ballSprite.x - ballSprite.width / 2 < coin.x
            ) {
                coinSound.play()
                coinsRed = coinsRed.filter { it.x != coin.x && it.y != coin.y }.toMutableList()
                text =
                    "${countCoins + countRedCoin - (coins.size + coinsRed.size)} / ${countCoins + countRedCoin}"
                if (ballXSpeed > 0) {
                    ballXSpeed += 10f
                }
                if (ballXSpeed < 0) {
                    ballXSpeed -= 10f
                }

                if (ballYSpeed > 0) {
                    ballYSpeed += 10f
                }

                if (ballYSpeed < 0) {
                    ballYSpeed -= 10f
                }

                countToEndEffectsOfGreen--
                if (countToEndEffectsOfGreen == 0) {
                    platformSprite = Sprite(platformTexture)
                    platformSprite.x = coin.x
                }
            }
        }

        // Аналогично для зеленых монет, только тут увеличиваем платформу, а
        // [countToEndEffectsOfGreen] ставим равной 3
        coinsGreen.forEach { coin ->
            if (
                ballSprite.y + ballSprite.height / 2 > coin.y &&
                ballSprite.y - ballSprite.height / 2 < coin.y &&
                ballSprite.x + ballSprite.width / 2 > coin.x &&
                ballSprite.x - ballSprite.width / 2 < coin.x
            ) {
                coinSound.play()
                coinsGreen = coinsGreen.filter { it.x != coin.x && it.y != coin.y }.toMutableList()
                platformSprite = Sprite(platformLongTexture,)
                platformSprite.x = coin.x
                countToEndEffectsOfGreen = 3
            }
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
        if (ballSprite.y < platformSprite.height && ballSprite.x > platformSprite.x && ballSprite.x < platformSprite.x + platformSprite.width) {
            ballYSpeed = -ballYSpeed
        }

        // Отображение фона
        batch.begin()

        batch.draw(
            backgroundTexture,
            0f,
            0f,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )

        // Рисование текста (счет)
        font.draw(batch, text, textX, textY)

        // Отображение монет
        for (coin in coins) {
            batch.draw(coinTexture, coin.x, coin.y)
        }

        // Рисуем красные монеты
        for (coin in coinsRed) {
            batch.draw(coinRedTexture, coin.x, coin.y)
        }

        // Рисуем зеленые монеты
        for (coin in coinsGreen) {
            batch.draw(coinGreenTexture, coin.x, coin.y)
        }

        // Отображение платформы и шарика
        ballSprite.draw(batch)
        platformSprite.draw(batch)
        batch.end()

        // Если сумма обычных и красных монет равна 0, мы победили в раунде
        if (coins.size + coinsRed.size == 0) {
            onWin()
        }

        // Проверка завершения игры при достижении нижней границы
        if (ballSprite.y < 0) {
            Gdx.input.setInputProcessor(null)
            onGameOver()
        }


    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    // Сброс игры
    override fun hide() {
        music.stop()
        ballXSpeed = 5f
        ballYSpeed = 5f
        countToEndEffectsOfGreen = 0
        coins.clear()
        coinsRed.clear()
        coinsGreen.clear()
    }

    // Завершение игры, освобождаем память, заолненная текстурами и музыкой
    override fun dispose() {
        ballTexture.dispose()
        platformTexture.dispose()
        coinTexture.dispose()
        coinRedTexture.dispose()
        music.dispose()
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

    // Обработка нажатий
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        currentTouchX = screenX
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        currentTouchX = null  // Обнуляем текущую координату X при отпускании экрана
        return true
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    // Включается, если мы провели пальцем по экрану, а не росто нажали
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (currentTouchX != null) {
            val deltaX = screenX - currentTouchX!! // Вычисляем изменение координаты X
            if ((platformSprite.x + deltaX + platformSprite.width < Gdx.graphics.width && platformSprite.x + deltaX >= 0) || countToEndEffectsOfGreen != 0) {
                platformSprite.x += deltaX  // Изменяем позицию платформы на основе изменения координаты X
            }
            currentTouchX = screenX  // Обновляем текущую координату X
        }
        return true
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }
}