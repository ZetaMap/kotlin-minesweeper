package fr.zetamap.minesweeper.game


interface Position {
  val x: Int
  val y: Int

  fun equals(other: Position): Boolean =
    this.x == other.x && this.y == other.y
}
