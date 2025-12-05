package fr.zetamap.minesweeper.game

/**
 * Game difficulty presets
 */
enum class Difficulty(
    val displayName: String,
    val rows: Int,
    val cols: Int,
    val mines: Int
) {
    EASY("Facile", 8, 8, 10),
    MEDIUM("Moyen", 12, 12, 30),
    HARD("Difficile", 16, 16, 60),
    EXPERT("Expert", 16, 30, 99);

    val totalCells: Int get() = rows * cols
}

