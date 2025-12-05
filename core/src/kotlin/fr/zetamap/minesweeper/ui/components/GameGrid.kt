package fr.zetamap.minesweeper.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import fr.zetamap.minesweeper.game.Cell
import fr.zetamap.minesweeper.game.GameState
import fr.zetamap.minesweeper.ui.theme.MinesweeperColors

@Composable
fun GameGrid(
    grid: List<List<Cell>>,
    gameState: GameState,
    recentlyRevealedCells: Set<Pair<Int, Int>>,
    onRevealCell: (Int, Int) -> Unit,
    onFlagCell: (Int, Int) -> Unit,
    onChordCell: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (grid.isEmpty()) return

    val rows = grid.size
    val cols = grid.firstOrNull()?.size ?: return

    val cellSize = when {
        cols <= 8 -> 40f
        cols <= 12 -> 35f
        cols <= 16 -> 30f
        else -> 25f
    }

    val gridScale = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        gridScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(gameState) {
        if (gameState == GameState.LOST) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 500
                    0f at 0
                    (-10f) at 50
                    10f at 100
                    (-8f) at 150
                    8f at 200
                    (-5f) at 250
                    5f at 300
                    (-2f) at 350
                    2f at 400
                    0f at 500
                }
            )
        }
    }

    Box(
        modifier = modifier
            .scale(gridScale.value)
            .offset(x = shakeOffset.value.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
                .background(MinesweeperColors.Surface.copy(alpha = 0.5f))
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                grid.forEachIndexed { rowIndex, row ->
                    Row {
                        row.forEachIndexed { colIndex, cell ->
                            key(rowIndex, colIndex) {
                                CellView(
                                    cell = cell,
                                    cellSize = cellSize,
                                    isRecentlyRevealed = (rowIndex to colIndex) in recentlyRevealedCells,
                                    onReveal = { onRevealCell(rowIndex, colIndex) },
                                    onFlag = { onFlagCell(rowIndex, colIndex) },
                                    onChord = { onChordCell(rowIndex, colIndex) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

