package com.mygdx.gravity_game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import kotlin.math.abs
import kotlin.properties.Delegates


/**
 * Класс экрана игры.
 * На вход получает [GameGravityViewModel] и [onGameOver] и [onWin], которые вызываются при завершении игры: выигрыше и проигрыше
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
class GameGravityScreen(
    val viewModel: GameGravityViewModel,
    val onGameOver: () -> Unit,
    val onWin: () -> Unit
) : Screen,
    InputProcessor {
    private val width = Gdx.graphics.width
    private val height = Gdx.graphics.height

    // Инициализация текстур игрока
    private var mansTextures = listOf(
        Texture(Gdx.files.internal("Biker_run 1.png")),
        Texture(Gdx.files.internal("Biker_run 2.png")),
        Texture(Gdx.files.internal("Biker_run 3.png")),
        Texture(Gdx.files.internal("Biker_run 4.png")),
        Texture(Gdx.files.internal("Biker_run 5.png")),
        Texture(Gdx.files.internal("Biker_run 6.png")),
    )

    // Инициализация текстур препятствий
    val block = Texture(Gdx.files.internal("block.png"))
    val block2 = Texture(Gdx.files.internal("block2.png"))
    val block3 = Texture(Gdx.files.internal("block3.png"))

    // Список спрайтов препятсвий (заполняется в методе show)
    private val blockSprites = mutableListOf<Sprite>()

    // Номер текущей текстуры игрока (меняется при каждой итерации метода render) - для бега
    private var indexManTexture = -1

    // Инициализация текстуры фона
    private var backgroundTexture: Texture = Texture(Gdx.files.internal("background.png"))

    // Создание срайта игрока
    private var manSprite: Sprite = Sprite(mansTextures[0])

    // Создание кисти
    private var batch: SpriteBatch = SpriteBatch()

    // Создание переменной скорости - инициализация ленивая (поздняя), заполняется в методе show
    private var speed by Delegates.notNull<Int>()

    // Создание переменных для текста счета
    val font = BitmapFont().apply {
        data.setScale(10f)
    }
    var text = "Gravity"
    val textX = 10f // Координата X для текста
    val textY =  height.toFloat() // Координата Y для текста (от верхнего края экрана)

    // Звук
    private var music: Music = Gdx.audio.newMusic(Gdx.files.internal("game_sound.mp3"))

    // Переменная, определяющая, сколько уже пробежал игрок
    var currentDistance = 0f

    // Переменная для определения гравитации - 0f если пол, height если потолок
    var gravityDistance = 0f

    // Функция по id возвращает спрайт препятсвия и устанавливает его y координату
    private fun getBlockSpriteById(id: Int): Sprite {
        val sprite = when (abs(id)) {
            1 -> Sprite(block)
            2 -> Sprite(block2)
            else -> Sprite(block3)
        }

        sprite.y = if (id > 0) 0f else height - sprite.height
        return sprite
    }

    // метод запускается один раз, когда экран отображается
    override fun show() {
        // музыка играет
        music.play()

        // получение параметров из viewModel
        val params = viewModel.params

        // Шаг через который устанавливать препятсвие
        val step = width * 3 / params.obstacles.size

        // Установка изначального расположения игрока (пол или потолок)
        gravityDistance = 0f

        // Очищаем поле от предыдущих препятсвий
        blockSprites.clear()

        // создание спрайтов препятствий
        params.obstacles.forEachIndexed { index, value ->
            if (value != 0) {
                val sprite = getBlockSpriteById(value)
                sprite.x = (step * abs(index)).toFloat()
                blockSprites.add(sprite)
            }
        }

        // установка позиции игрока
        manSprite.setPosition(200f, 0f)

        // Инициализация скорости
        speed = viewModel.params.speed

        // Включение отслеживания активности юзера (клики, перемещения и тд)
        Gdx.input.inputProcessor = this

    }

    override fun render(delta: Float) {
        // Чистка экрана - пустое полотно
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Изменение текстуры в спайте игрока (делается это для видимости бега юзера)
        if (currentDistance.toInt() % (speed + 5) == 0) {
            indexManTexture = (indexManTexture + 1) % mansTextures.size
            manSprite.texture = mansTextures[indexManTexture]
        }

        // Перемещение препятсвий влево по оси x
        blockSprites.forEach {
            it.x -= speed
        }

        // увеличение расстояния, который пробежал игрок
        currentDistance += speed

        // увеличиваем координаты игрока по оси y если гравитация поменялась
        if (manSprite.y < gravityDistance) {
            manSprite.y += speed * 4

            if (manSprite.y > height - manSprite.height) manSprite.y = height - manSprite.height
        }
        if (manSprite.y > gravityDistance) {
            manSprite.y -= speed * 4

            if (manSprite.y < 0) manSprite.y = 0f
        }

        // Отображение фона
        batch.begin()

        //Рисование 1 фона
        batch.draw(
            backgroundTexture,
            0f - (currentDistance % width),
            0f,
            width.toFloat(),
            height.toFloat()
        )

        // Рисование 2 фона
        batch.draw(
            backgroundTexture,
            width - (currentDistance % width),
            0f,
            width.toFloat(),
            height.toFloat()
        )

        // Рисование игрока
        manSprite.draw(batch)

        // рисование препятсвий
        blockSprites.forEach {
            it.draw(batch)
        }

        // Рисование текста
        font.draw(batch, text, textX, textY)

        batch.end()

        // Преобразовываем спрайт игрока в объект Rectangle
        val rect = manSprite.boundingRectangle

        // Пробегаемся по каждому спрайту препятсвия - если обнаружен контакт игрока с препятсвием, то вызываем функцию onGameOver()
        blockSprites.forEach {
            if (rect.contains(it.x, it.y)) {
                onGameOver()
            }
        }

        // Если последний блок ушел левее 0 по оси x, то вызываем функцию onWin()
        if (blockSprites.last().x < 0) {
            onWin()
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
        gravityDistance = 0f
        if (manSprite.isFlipY) {
            manSprite.flip(false, true)
        }
    }

    // Завершение игры, освобождаем память, заолненная текстурами и музыкой
    override fun dispose() {
        mansTextures.forEach {
            it.dispose()
        }
        block.dispose()
        block2.dispose()
        block3.dispose()
        block.dispose()
        backgroundTexture.dispose()
        music.dispose()

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

    // Обработка нажатиия
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (manSprite.y == 0f || manSprite.y == height - manSprite.height) {
            gravityDistance = if (gravityDistance == 0f) {
                manSprite.flip(false, true)
                height - manSprite.height
            } else {
                manSprite.flip(false, true)
                0f
            }
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    // Включается, если мы провели пальцем по экрану, а не росто нажали
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