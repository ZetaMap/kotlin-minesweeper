package minesweeper.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import minesweeper.game.GameManager
import minesweeper.model.GameState

/**
 * The game board view containing the grid of cells.
 */
@Composable
fun BoardView(
    gameManager: GameManager,
    modifier: Modifier = Modifier
) {
    val board by gameManager.board
    val revealingCells = gameManager.revealingCells
    val animatingFlags = gameManager.animatingFlags
    val explodingCell by gameManager.explodingCell
    
    // Shake animation for when the game is lost
    val shakeOffset by animateFloatAsState(
        targetValue = if (board.gameState == GameState.LOST) 0f else 0f,
        animationSpec = if (board.gameState == GameState.LOST) {
            keyframes {
                durationMillis = 500
                0f at 0
                10f at 50
                -10f at 100
                8f at 150
                -8f at 200
                5f at 250
                -5f at 300
                3f at 350
                -3f at 400
                0f at 500
            }
        } else {
            snap()
        },
        label = "shakeOffset"
    )
    
    Box(
        modifier = modifier
            .offset(x = shakeOffset.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MinesweeperTheme.panelBackground)
            .border(
                width = 3.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (row in 0 until board.rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    for (col in 0 until board.cols) {
                        val cell = board.cells[row][col]
                        val isRevealing = cell in revealingCells
                        val isFlagAnimating = animatingFlags[Pair(row, col)] != null
                        val isExploding = explodingCell?.let { it.row == row && it.col == col } ?: false
                        
                        CellView(
                            cell = cell,
                            isRevealing = isRevealing,
                            isFlagAnimating = isFlagAnimating,
                            isExploding = isExploding,
                            onLeftClick = { gameManager.onCellClick(row, col) },
                            onRightClick = { gameManager.onCellRightClick(row, col) },
                            onDoubleClick = { gameManager.onCellDoubleClick(row, col) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * LED-style digital display for numbers (timer and mine counter).
 */
@Composable
fun DigitalDisplay(
    value: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MinesweeperTheme.displayBackground)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = value.coerceIn(-99, 999).toString().padStart(3, '0'),
            color = MinesweeperTheme.displayText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Status panel showing the face button, timer, and mine count.
 */
@Composable
fun StatusPanel(
    gameManager: GameManager,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val board by gameManager.board
    val elapsedSeconds by gameManager.elapsedSeconds
    
    // Face emoji based on game state
    val faceEmoji = when (board.gameState) {
        GameState.WON -> "😎"
        GameState.LOST -> "😵"
        else -> "🙂"
    }
    
    // Pulse animation for the face
    val infiniteTransition = rememberInfiniteTransition(label = "facePulse")
    val faceScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "faceScale"
    )
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MinesweeperTheme.panelBackground)
            .border(
                width = 3.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mine counter
        DigitalDisplay(value = board.remainingMines)
        
        // Face button
        FaceButton(
            emoji = faceEmoji,
            scale = if (board.gameState == GameState.WON) faceScale else 1f,
            onClick = onNewGame
        )
        
        // Timer
        DigitalDisplay(value = elapsedSeconds)
    }
}

@Composable
private fun FaceButton(
    emoji: String,
    scale: Float,
    onClick: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        )
    }
}
