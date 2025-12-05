package fr.zetamap.minesweeper.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.zetamap.minesweeper.game.Cell
import fr.zetamap.minesweeper.ui.theme.MinesweeperColors

@Composable
fun CellView(
    cell: Cell,
    cellSize: Float,
    isRecentlyRevealed: Boolean,
    onReveal: () -> Unit,
    onFlag: () -> Unit,
    onChord: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)
    var wasRevealed by remember { mutableStateOf(cell.isRevealed) }
    val revealAnimation = remember { Animatable(if (cell.isRevealed) 1f else 0f) }

    LaunchedEffect(cell.isRevealed) {
        if (cell.isRevealed && !wasRevealed) {
            wasRevealed = true
            revealAnimation.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
    }

    val scaleAnimation by animateFloatAsState(
        targetValue = if (isRecentlyRevealed && cell.isRevealed) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "scale"
    )

    val explosionScale by animateFloatAsState(
        targetValue = if (cell.isExploded) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessLow),
        label = "explosion"
    )

    val flagWave = remember { Animatable(0f) }
    LaunchedEffect(cell.isFlagged) {
        if (cell.isFlagged) {
            flagWave.animateTo(1f, keyframes { durationMillis = 500; 0f at 0; 15f at 100; (-10f) at 200; 5f at 300; 0f at 500 })
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            cell.isExploded -> MinesweeperColors.CellExploded
            cell.isRevealed && cell.isMine -> MinesweeperColors.CellMine
            cell.isRevealed -> MinesweeperColors.CellRevealed
            cell.isFlagged -> MinesweeperColors.CellFlagged.copy(alpha = 0.3f)
            else -> MinesweeperColors.CellUnrevealed
        },
        animationSpec = tween(200),
        label = "backgroundColor"
    )

    Box(
        modifier = modifier
            .size(cellSize.dp)
            .padding(1.dp)
            .scale(scaleAnimation * explosionScale)
            .shadow(elevation = if (cell.isRevealed) 1.dp else 3.dp, shape = shape)
            .clip(shape)
            .background(
                if (!cell.isRevealed && !cell.isFlagged) {
                    Brush.verticalGradient(listOf(MinesweeperColors.CellUnrevealed.copy(alpha = 0.9f), MinesweeperColors.CellUnrevealed))
                } else {
                    Brush.verticalGradient(listOf(backgroundColor, backgroundColor))
                }
            )
            .border(1.dp, if (cell.isRevealed) Color.Gray.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.3f), shape)
            .pointerInput(cell.isRevealed, cell.isFlagged) {
                detectTapGestures(
                    onTap = { if (cell.isRevealed) onChord() else if (!cell.isFlagged) onReveal() },
                    onLongPress = { if (!cell.isRevealed) onFlag() },
                    onDoubleTap = { if (!cell.isRevealed) onFlag() else onChord() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            cell.isFlagged -> Text("🚩", fontSize = (cellSize * 0.5f).sp, modifier = Modifier.graphicsLayer(rotationZ = flagWave.value))
            cell.isRevealed && cell.isMine -> Text(if (cell.isExploded) "💥" else "💣", fontSize = (cellSize * 0.5f).sp)
            cell.isRevealed && cell.adjacentMines > 0 -> Text(
                cell.adjacentMines.toString(),
                fontSize = (cellSize * 0.5f).sp,
                fontWeight = FontWeight.Bold,
                color = MinesweeperColors.getNumberColor(cell.adjacentMines),
                modifier = Modifier.graphicsLayer(scaleX = revealAnimation.value, scaleY = revealAnimation.value)
            )
        }
    }
}
