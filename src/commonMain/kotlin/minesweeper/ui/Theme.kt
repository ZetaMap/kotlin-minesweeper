package minesweeper.ui

import androidx.compose.ui.graphics.Color

/**
 * Theme colors for the Minesweeper game.
 */
object MinesweeperTheme {
    // Cell colors
    val cellUnrevealed = Color(0xFF4A90D9)
    val cellUnrevealedHover = Color(0xFF5BA0E9)
    val cellRevealed = Color(0xFFD4D4D4)
    val cellMine = Color(0xFFFF4444)
    val cellMineExploded = Color(0xFFFF0000)
    val cellFlagged = Color(0xFF4A90D9)
    
    // Border colors
    val cellBorderLight = Color(0xFF6BB3F8)
    val cellBorderDark = Color(0xFF357ABD)
    val cellRevealedBorder = Color(0xFFB0B0B0)
    
    // Number colors for adjacent mine counts
    val numberColors = mapOf(
        1 to Color(0xFF0000FF),  // Blue
        2 to Color(0xFF008000),  // Green
        3 to Color(0xFFFF0000),  // Red
        4 to Color(0xFF000080),  // Dark Blue
        5 to Color(0xFF800000),  // Maroon
        6 to Color(0xFF008080),  // Teal
        7 to Color(0xFF000000),  // Black
        8 to Color(0xFF808080)   // Gray
    )
    
    // UI colors
    val background = Color(0xFFC0C0C0)
    val panelBackground = Color(0xFFE0E0E0)
    val displayBackground = Color(0xFF300000)
    val displayText = Color(0xFFFF0000)
    
    // Game state colors
    val wonColor = Color(0xFF00FF00)
    val lostColor = Color(0xFFFF0000)
}
