package com.mygdx.game

import kotlinx.coroutines.flow.MutableStateFlow

class GameViewModel {
    var maxLevel: Int = 1
    var currentLevel: Int = 1
    lateinit var params: GameParams
    data class GameParams(
        val countCoins: Int,
        val countRedCoin: Int,
        val countGreenCoin: Int,
        val speed: Float
    )
    fun setParamsForLevel() {
        params = when(currentLevel) {
            1 -> { GameParams(4, 0, 0, 9f,) }
            2 -> { GameParams(4, 2, 0, 9f) }
            3 -> { GameParams(4, 3, 0, 9f) }
            4 -> { GameParams(5, 4, 0, 9f) }
            else -> { GameParams(4, 5, 2, 9f) }
        }
    }
}