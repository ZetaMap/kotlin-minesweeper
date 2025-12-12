package fr.zetamap.minesweeper.game


/** Represents a cell in the grid. */
data class Cell(
  override val x: Int,
  override val y: Int,
  val isMine: Boolean = false,
  val isRevealed: Boolean = false,
  val isFlagged: Boolean = false,
  val adjacentMines: Int = 0
): Position {
  val isExploded = isMine && isRevealed
  val hasAdjacentMines = adjacentMines > 0

  init {
    require(x >= 0) { "'x' must be >= 0, got $x" }
    require(y >= 0) { "'y' must be >= 0, got $y" }
    require(adjacentMines in 0..8 ) { "'adjacentMines' must be between 0 and 8, got $adjacentMines" }
  }

  /** Just compares {@link #x} and {@link #y}. */
  override fun equals(other: Any?): Boolean =
    other is Position && x == other.x && y == other.y

  /** Just hash {@link #x} and {@link #y}. */
  override fun hashCode(): Int =  31 * x + y
}

