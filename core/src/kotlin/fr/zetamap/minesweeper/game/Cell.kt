package fr.zetamap.minesweeper.game

/**
 * Represents a cell in the Minesweeper grid
 */
data class Cell(
    val row: Int,
    val col: Int,
    val isMine: Boolean = false,
    val isRevealed: Boolean = false,
    val isFlagged: Boolean = false,
    val adjacentMines: Int = 0
) {
    val isExploded: Boolean
        get() = isMine && isRevealed
}

