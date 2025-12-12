package fr.zetamap.minesweeper.ui

import androidx.compose.ui.graphics.Color


object Pal {
  // Cell colors
  val CellUnrevealed = Color(0xFF4A90D9)
  val CellUnrevealedHover = Color(0xFF5BA3E8)
  val CellRevealed = Color(0xFFE8E8E8)
  val CellMine = Color(0xFFFF4444)
  val CellExploded = Color(0xFF2D2D44)
  val CellFlagged = Color(0xFFFFAA00)
  val CellFlaggedMine = Color(0xFF4CAF50)

  // Number colors (classic minesweeper colors)
  val Number1 = Color(0xFF0000FF)
  val Number2 = Color(0xFF008000)
  val Number3 = Color(0xFFFF0000)
  val Number4 = Color(0xFF000080)
  val Number5 = Color(0xFF800000)
  val Number6 = Color(0xFF008080)
  val Number7 = Color(0xFF000000)
  val Number8 = Color(0xFF808080)

  // UI colors
  val Background = Color(0xFF1E1E2E)
  val Surface = Color(0xFF2D2D44)
  val SurfaceLight = Color(0xFF3D3D5C)
  val Primary = Color(0xFF6C63FF)
  val PrimaryVariant = Color(0xFF5A52E0)
  val Secondary = Color(0xFF03DAC6)
  val OnBackground = Color(0xFFFFFFFF)
  val OnSurface = Color(0xFFE0E0E0)

  // Game state colors
  val Victory = Color(0xFF4CAF50)
  val Defeat = Color(0xFFF44336)
  val Warning = Color(0xFFFF9800)

  // Button colors
  val ButtonUndo = Color(0xFF8E44AD)
  val ButtonRedo = Color(0xFF8E44AD)
  val ButtonNewGame = Color(0xFF3498DB)
  val ButtonNewGameLost = Color(0xFFF44336)


  fun Int.palColor(): Color = when (this) {
    1 -> Number1
    2 -> Number2
    3 -> Number3
    4 -> Number4
    5 -> Number5
    6 -> Number6
    7 -> Number7
    8 -> Number8
    else -> Color.Transparent
  }
}
