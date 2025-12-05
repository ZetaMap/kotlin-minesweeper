package fr.zetamap.minesweeper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.zetamap.minesweeper.game.Cell
import fr.zetamap.minesweeper.game.Difficulty
import fr.zetamap.minesweeper.game.GameState
import fr.zetamap.minesweeper.game.MinesweeperGame

class MinesweeperViewModel {
    var difficulty by mutableStateOf(Difficulty.EASY)
        private set
    var game by mutableStateOf(MinesweeperGame.create(Difficulty.EASY))
        private set
    var grid by mutableStateOf(game.grid)
        private set
    var gameState by mutableStateOf(GameState.NOT_STARTED)
        private set
    var elapsedSeconds by mutableStateOf(0)
        private set
    var recentlyRevealedCells by mutableStateOf<Set<Pair<Int, Int>>>(emptySet())
        private set
    var showingExplosion by mutableStateOf(false)
        private set
    var showingVictory by mutableStateOf(false)
        private set
    val remainingMines: Int get() = game.remainingMines

    fun newGame(newDifficulty: Difficulty = difficulty) {
        difficulty = newDifficulty
        game = MinesweeperGame.create(newDifficulty)
        grid = game.grid
        gameState = GameState.NOT_STARTED
        elapsedSeconds = 0
        recentlyRevealedCells = emptySet()
        showingExplosion = false
        showingVictory = false
    }

    fun revealCell(row: Int, col: Int) {
        if (gameState == GameState.WON || gameState == GameState.LOST) return
        val revealed = game.revealCell(row, col)
        grid = game.grid
        gameState = game.gameState
        recentlyRevealedCells = revealed.map { it.row to it.col }.toSet()
        when (gameState) { GameState.LOST -> showingExplosion = true; GameState.WON -> showingVictory = true; else -> {} }
    }

    fun toggleFlag(row: Int, col: Int) {
        if (gameState == GameState.WON || gameState == GameState.LOST) return
        game.toggleFlag(row, col)
        grid = game.grid
        gameState = game.gameState
    }

    fun chord(row: Int, col: Int) {
        if (gameState == GameState.WON || gameState == GameState.LOST) return
        val revealed = game.chord(row, col)
        grid = game.grid
        gameState = game.gameState
        recentlyRevealedCells = revealed.map { it.row to it.col }.toSet()
        when (gameState) { GameState.LOST -> showingExplosion = true; GameState.WON -> showingVictory = true; else -> {} }
    }

    fun tick() { if (gameState == GameState.PLAYING) elapsedSeconds++ }
    fun clearRecentlyRevealed() { recentlyRevealedCells = emptySet() }
    fun getCell(row: Int, col: Int): Cell? = game.getCell(row, col)
}
