package com.mygdx.game

import android.os.Bundle
import android.preference.PreferenceManager
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration


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