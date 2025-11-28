package minesweeper.model

/**
 * Represents the state of a single cell in the Minesweeper grid.
 */
data class Cell(
    val row: Int,
    val col: Int,
    val isMine: Boolean = false,
    val isRevealed: Boolean = false,
    val isFlagged: Boolean = false,
    val adjacentMines: Int = 0
)

/**
 * Represents the difficulty level of the game.
 */
enum class Difficulty(val rows: Int, val cols: Int, val mines: Int) {
    BEGINNER(9, 9, 10),
    INTERMEDIATE(16, 16, 40),
    EXPERT(16, 30, 99)
}

/**
 * Represents the current state of the game.
 */
enum class GameState {
    READY,      // Game is ready to start (first click not made)
    PLAYING,    // Game is in progress
    WON,        // Player won the game
    LOST        // Player lost the game (hit a mine)
}
