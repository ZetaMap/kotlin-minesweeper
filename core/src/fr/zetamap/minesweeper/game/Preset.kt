package fr.zetamap.minesweeper.game


/** Game difficulty presets. */
class Preset(
  val name: String,
  rows: Int,
  cols: Int,
  mines: Int
): GameOptions(rows, cols, mines) {

  companion object {
    val Easy = Preset("Facile", 8, 8, 10)
    val Medium = Preset("Moyen", 12, 12, 30)
    val Hard = Preset("Difficile", 16, 16, 60)
    val Expert = Preset("Expert", 16, 30, 99)

    val All = setOf(Easy, Medium, Hard, Expert)
  }
}

