package com.mygdx.gravity_game

/**
 * Класс ViewModel программы - содержит в себе параметры игры
 */
class GameGravityViewModel {
    var maxLevel: Int = 1
    var currentLevel: Int = 1
    lateinit var params: GameParams
    data class GameParams(
        val speed: Int,
        val obstacles: List<Int>
    )
    fun setParamsForLevel() {
        params = when(currentLevel) {
            1 -> { GameParams(15, listOf(0, 0, 0, 0, 0, 0, -1, 1, 2, 0, 0)) }
            2 -> { GameParams(15, listOf(0, 0, 0, 0, 0, 0, -1, 3, -2, -2, 3)) }
            3 -> { GameParams(15, listOf(0, 0, 0, 0, 0, 3, -1, 1, 2, -3, -1, 3, -2)) }
            4 -> { GameParams(15, listOf(0, 0, 0, 0, 0, 3, -1, 1, 2, -3, -1, 3, -2, -3, 3, -3)) }
            else -> GameParams(20, listOf(0, 0, 0, 1, 0, 2, -1, 1, 2, -2, -1, 2, -2, -3, 3, -3))

        }
    }
}