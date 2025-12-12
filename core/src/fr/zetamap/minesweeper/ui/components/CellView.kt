package fr.zetamap.minesweeper.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import fr.zetamap.minesweeper.game.Cell
import fr.zetamap.minesweeper.game.GameState
import fr.zetamap.minesweeper.ui.Pal
import fr.zetamap.minesweeper.ui.Pal.palColor


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CellView(
  cell: Cell,
  state: GameState,
  cellSize: Float,
  isRecentlyRevealed: Boolean,
  isExplodedMine: Boolean = false,
  onReveal: (Cell) -> Unit,
  onFlag: (Cell) -> Unit,
  onChord: (Cell) -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(4.dp)

  val scaleAnimation by animateFloatAsState(
    targetValue = if (isRecentlyRevealed && cell.isRevealed) 1.1f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMedium
    ),
    label = "scale"
  )

  val explosionScale = remember { Animatable(1f) }
  LaunchedEffect(cell.isExploded, isExplodedMine) {
    if (cell.isExploded && isExplodedMine) {
      explosionScale.animateTo(
        targetValue = 1.3f,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioHighBouncy,
          stiffness = Spring.StiffnessHigh
        )
      )
      explosionScale.animateTo(
        targetValue = 1f,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessMedium
        )
      )
    }
  }

  val flagWave = remember { Animatable(0f) }
  LaunchedEffect(cell.isFlagged) {
    flagWave.animateTo(
      if (cell.isFlagged) 1f else 0f,
      keyframes {
        durationMillis = 500;
        0f at 0;
        15f at 100;
        (-10f) at 200;
        5f at 300;
        0f at 500
      }
    )
  }

  // Hover state
  val interactionSource = remember { MutableInteractionSource() }
  val isHovered by interactionSource.collectIsHoveredAsState()

  // Hover rotation animation
  val hoverRotation = remember { Animatable(0f) }
  LaunchedEffect(isHovered, cell.isRevealed) {
    if (isHovered && !cell.isRevealed) {
      while (true) {
        hoverRotation.animateTo(
          targetValue = 2f,
          animationSpec = tween(300, easing = LinearEasing)
        )
        hoverRotation.animateTo(
          targetValue = -2f,
          animationSpec = tween(600, easing = LinearEasing)
        )
        hoverRotation.animateTo(
          targetValue = 0f,
          animationSpec = tween(300, easing = LinearEasing)
        )
      }
    } else {
      hoverRotation.snapTo(0f)
    }
  }

  val backgroundColor by animateColorAsState(
    targetValue = when {
      cell.isFlagged && cell.isMine && state == GameState.LOST -> Pal.CellFlaggedMine
      cell.isExploded && isExplodedMine -> Pal.CellExploded
      cell.isExploded -> Pal.CellMine
      cell.isRevealed && cell.isMine -> Pal.CellMine
      cell.isRevealed -> Pal.CellRevealed
      isHovered && !cell.isFlagged -> Pal.CellUnrevealedHover
      cell.isFlagged -> Pal.CellFlagged.copy(alpha = 0.3f)
      else -> Pal.CellUnrevealed
    },
    animationSpec = tween(200),
    label = "backgroundColor"
  )

  Box(
    modifier = modifier
      .size(cellSize.dp)
      .padding(1.dp)
      .scale(scaleAnimation * explosionScale.value)
      .graphicsLayer(rotationZ = hoverRotation.value)
      .hoverable(interactionSource = interactionSource)
      .shadow(elevation = if (cell.isRevealed) 1.dp else 3.dp, shape = shape)
      .clip(shape)
      .background(
        if (!cell.isRevealed && !cell.isFlagged && !isHovered) {
          Brush.Companion.verticalGradient(listOf(Pal.CellUnrevealed.copy(alpha = 0.9f), Pal.CellUnrevealed))
        } else {
          Brush.verticalGradient(listOf(backgroundColor, backgroundColor))
        }
      )
      .border(1.dp, if (cell.isRevealed) Color.Gray.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.3f), shape)
      .pointerInput(cell.isRevealed, cell.isFlagged) {
        // Right-click to flag (desktop)
        awaitPointerEventScope {
          while (true) {
            val event = awaitPointerEvent()
            if (event.type == PointerEventType.Press &&
                event.button == PointerButton.Secondary &&
                !cell.isRevealed) {
              onFlag(cell)
            }
          }
        }
      }
      .pointerInput(cell.isRevealed, cell.isFlagged) {
        detectTapGestures(
          onTap = { if (cell.isRevealed) onChord(cell) else if (!cell.isFlagged) onReveal(cell) },
          onLongPress = { if (!cell.isRevealed) onFlag(cell) },
          onDoubleTap = { if (!cell.isRevealed) onFlag(cell) else onChord(cell) }
        )
      },
    contentAlignment = Alignment.Center
  ) {
    when {
      cell.isFlagged -> Text(
        "🚩",
        fontSize = (cellSize * 0.5f).sp,
        modifier = Modifier.graphicsLayer(rotationZ = flagWave.value)
      )
      cell.isExploded && isExplodedMine -> Text(
        "💥",
        fontSize = (cellSize * 0.5f).sp
      )
      cell.isRevealed && cell.isMine -> Text(
        "💣",
        fontSize = (cellSize * 0.5f).sp
      )
      cell.isRevealed && cell.hasAdjacentMines -> Text(
        cell.adjacentMines.toString(),
        fontSize = (cellSize * 0.5f).sp,
        fontWeight = FontWeight.Bold,
        color = cell.adjacentMines.palColor()
      )
    }
  }
}
