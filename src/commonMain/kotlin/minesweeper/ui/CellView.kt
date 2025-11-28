package minesweeper.ui

import androidx.compose.runtime.Composable
import minesweeper.model.Cell

/**
 * A single cell in the Minesweeper grid with animations.
 * Platform-specific implementations handle mouse (desktop) vs touch (mobile) interactions.
 */
@Composable
expect fun CellView(
    cell: Cell,
    isRevealing: Boolean,
    isFlagAnimating: Boolean,
    isExploding: Boolean,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onDoubleClick: () -> Unit
)
