package fr.zetamap.minesweeper.game


/** Game options. */
open class GameOptions(
  val rows: Int,
  val cols: Int,
  val mines: Int
) {
  val cells: Int = rows * cols
  val mineDensity: Float = mines.toFloat() / cells.toFloat()

  constructor(
    rows: Int,
    cols: Int,
    mineDensity: Float
  ) : this(rows, cols, (rows * cols * mineDensity).toInt())

  init {
    require(rows > 0) { "'rows' must be greater than 1, got $rows" }
    require(cols > 0) { "'cols' must be greater than 1, got $cols" }
    require(mines in 1 until cells) { "'mines' must be between 1 and ${cells - 1}, got $mines" }
    require(mineDensity in 0.0..1.0) { "'mineDensity' must be between 0.0 and 1.0, got $mineDensity" }
  }
}
