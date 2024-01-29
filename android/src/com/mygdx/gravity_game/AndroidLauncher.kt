package com.mygdx.gravity_game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

/**
 * Стартовый класс риложения. Запуск активности приложения, считывание
 * настроек (уровня) и создание объекта класса игры с последующей инициализацией
 */
class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val config = AndroidApplicationConfiguration()


        val level = sharedPreferences.getInt("level", 1)

        val game = MyGame(level) {
            sharedPreferences.edit().putInt("level", it).apply()
        }

        initialize(game, config)
    }
}