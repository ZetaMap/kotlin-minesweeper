package minesweeper.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import minesweeper.model.Cell

/**
 * A single cell in the Minesweeper grid with animations.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun CellView(
    cell: Cell,
    isRevealing: Boolean,
    isFlagAnimating: Boolean,
    isExploding: Boolean,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onDoubleClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    
    // Reveal animation
    val revealScale by animateFloatAsState(
        targetValue = if (isRevealing) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "revealScale"
    )
    
    val revealRotation by animateFloatAsState(
        targetValue = if (cell.isRevealed && isRevealing) 0f else if (!cell.isRevealed) 180f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "revealRotation"
    )
    
    // Flag animation
    val flagScale by animateFloatAsState(
        targetValue = if (isFlagAnimating) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "flagScale"
    )
    
    // Explosion animation
    val explosionScale by animateFloatAsState(
        targetValue = if (isExploding) 1.5f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "explosionScale"
    )
    
    val explosionColor by animateColorAsState(
        targetValue = if (isExploding) MinesweeperTheme.cellMineExploded else MinesweeperTheme.cellMine,
        animationSpec = tween(500),
        label = "explosionColor"
    )
    
    // Background color
    val backgroundColor by animateColorAsState(
        targetValue = when {
            cell.isRevealed && cell.isMine && isExploding -> explosionColor
            cell.isRevealed && cell.isMine -> MinesweeperTheme.cellMine
            cell.isRevealed -> MinesweeperTheme.cellRevealed
            isPressed -> MinesweeperTheme.cellUnrevealedHover.copy(alpha = 0.7f)
            isHovered -> MinesweeperTheme.cellUnrevealedHover
            else -> MinesweeperTheme.cellUnrevealed
        },
        animationSpec = tween(150),
        label = "backgroundColor"
    )
    
    Box(
        modifier = Modifier
            .size(32.dp)
            .scale(
                when {
                    isExploding -> explosionScale
                    isRevealing -> revealScale
                    isFlagAnimating -> flagScale
                    else -> 1f
                }
            )
            .graphicsLayer {
                rotationY = revealRotation
            }
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = if (cell.isRevealed) MinesweeperTheme.cellRevealedBorder else MinesweeperTheme.cellBorderDark,
                shape = RoundedCornerShape(4.dp)
            )
            .hoverable(
                interactionSource = remember { MutableInteractionSource() }
            )
            .pointerInput(cell.isRevealed, cell.isFlagged) {
                detectTapGestures(
                    onTap = { 
                        if (!cell.isRevealed && !cell.isFlagged) {
                            onLeftClick()
                        } else if (cell.isRevealed) {
                            onDoubleClick()
                        }
                    },
                    onDoubleTap = { onDoubleClick() }
                )
            }
            .onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
                if (!cell.isRevealed) {
                    onRightClick()
                }
            }
            .onHover { isHovered = it },
        contentAlignment = Alignment.Center
    ) {
        when {
            cell.isFlagged -> {
                Text(
                    text = "🚩",
                    fontSize = 16.sp,
                    modifier = Modifier.scale(flagScale)
                )
            }
            cell.isRevealed && cell.isMine -> {
                Text(
                    text = "💣",
                    fontSize = 16.sp,
                    modifier = Modifier.scale(if (isExploding) explosionScale else 1f)
                )
            }
            cell.isRevealed && cell.adjacentMines > 0 -> {
                Text(
                    text = cell.adjacentMines.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinesweeperTheme.numberColors[cell.adjacentMines] ?: Color.Black
                )
            }
        }
    }
}

@Composable
private fun Modifier.onHover(onHover: (Boolean) -> Unit): Modifier {
    return this.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                onHover(event.changes.any { it.pressed.not() && it.previousPressed.not() })
            }
        }
    }
}
