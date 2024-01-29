package com.mygdx.gravity_game

import com.badlogic.gdx.Game

/**
 * Мозги игроого процесса
 * @param initLevel начальный уровень
 * @param onSaveLevel callback функция, вызываемая при сохранения уровня игры
 */
class MyGame(
    val initLevel: Int,
    val onSaveLevel: (Int) -> Unit
) : Game() {

    /**
     * Создание переменных с экранами
     */
    private lateinit var gameOverGravityScreen: GameOverGravityScreen
    private lateinit var gameGravityScreen: GameGravityScreen
    private lateinit var menuScreen: MenuScreen
    private lateinit var gameGravityWinRoundScreen: GameGravityWinRoundScreen
    private lateinit var gameWinGravityScreen: GameWinGravityScreen
    private lateinit var levelsScreen: LevelsScreen
    val viewModel = GameGravityViewModel()
    override fun create() {

        /**
         * Инициализация каждого экрана. Все экраны получают в качестве аргумента
         * callback функцию (функция обратного вызова), позволяющая вернутся назад в коде
         * и выполнить определенные действия - открыть другой экран.
         * При исользовании setScreen экраны переключаются между собой, предыдущий экран
         * вызывает метод onHide
         */
        viewModel.currentLevel = initLevel
        viewModel.maxLevel = initLevel

        viewModel.setParamsForLevel()

        gameOverGravityScreen = GameOverGravityScreen(
            onRestart =  {
                setScreen(gameGravityScreen)
            },
            onMenu = {
                getScreen().pause()
                setScreen(menuScreen)
            }
        )
        gameGravityScreen = GameGravityScreen(
            viewModel = viewModel,
            onGameOver = { setScreen(gameOverGravityScreen) },
            onWin = {
                if (viewModel.currentLevel == 5) {
                    setScreen(gameWinGravityScreen)
                } else {
                    viewModel.currentLevel++
                    if (viewModel.currentLevel > viewModel.maxLevel) {
                        viewModel.maxLevel++
                        onSaveLevel(viewModel.maxLevel)
                    }
                    viewModel.setParamsForLevel()
                    setScreen(gameGravityWinRoundScreen)
                }
            }
        )
        menuScreen = MenuScreen(
            onStart = { setScreen((gameGravityScreen)) },
            onLevel = { setScreen(levelsScreen) }
        )

        gameGravityWinRoundScreen = GameGravityWinRoundScreen(
            onNext = {
                setScreen(gameGravityScreen)
                     },
            onMenu = {  setScreen(menuScreen)}
        )

        gameWinGravityScreen = GameWinGravityScreen(
            onRestart = {
                viewModel.currentLevel = 1
                viewModel.maxLevel = 1
                viewModel.setParamsForLevel()
                setScreen(menuScreen)
            },
            onMenu = {
                setScreen(menuScreen)
            }
        )

        levelsScreen = LevelsScreen(viewModel) {
            viewModel.currentLevel = it
            viewModel.setParamsForLevel()
            setScreen(gameGravityScreen)
        }

        setScreen(menuScreen)
    }
}

