package fr.zetamap.minesweeper.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import fr.zetamap.minesweeper.game.GameState
import fr.zetamap.minesweeper.ui.components.*


@Composable
fun GameHeader(
  remainingMines: Int,
  elapsedSeconds: Int,
  state: GameState,
  showConfirmDialog: Boolean,
  onShowConfirmDialog: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  onNewGame: () -> Unit,
  onUndo: () -> Unit,
  onRedo: () -> Unit,
  onShopClick: () -> Unit,
  canUndo: Boolean,
  canRedo: Boolean
) {
  val statusEmoji = when (state) {
    GameState.NOT_STARTED -> "😊"
    GameState.PLAYING -> "🙂"
    GameState.WON -> "😎"
    GameState.LOST -> "😵"
  }

  val timerPulse = remember { Animatable(1f) }
  LaunchedEffect(state, showConfirmDialog) {
    if (state == GameState.PLAYING && !showConfirmDialog) {
      timerPulse.animateTo(
        1f,
        infiniteRepeatable(
          keyframes {
            durationMillis = 1000
            1f at 0
            1.05f at 500
            1f at 1000
          },
          RepeatMode.Restart
        )
      )
    } else {
      timerPulse.snapTo(1f)
    }
  }

  var previousState by remember { mutableStateOf(state) }
  val emojiScale = remember { Animatable(1f) }

  LaunchedEffect(state) {
    if (state != previousState) {
      previousState = state
      emojiScale.animateTo(1.3f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessHigh))
      emojiScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "blink")
  val blinkAlpha by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 0.4f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "blinkAlpha"
  )

  val mineCounterColor by animateColorAsState(
    when {
      remainingMines < 0 -> Pal.Defeat
      remainingMines == 0 -> Pal.Victory
      else -> Pal.OnSurface
    },
    tween(300), "mineCounterColor"
  )

  Row(
    modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Pal.Surface).padding(10.dp),
    Arrangement.SpaceBetween, Alignment.CenterVertically
  ) {
    // Left side: Mine counter + New game button
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      CounterDisplay(remainingMines, "💣", mineCounterColor)
      // New game button
      ActionButton(
        onClick = onNewGame,
        backgroundColor =
          if (state == GameState.LOST) Pal.ButtonNewGameLost.copy(alpha = blinkAlpha)
          else Pal.ButtonNewGame,
        content = { Text("🔄", fontSize = 14.sp) }
      )
    }

    // Center: Undo + Emoji + Redo
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      // Undo button
      ActionButton(
        onClick = onUndo,
        enabled = canUndo,
        backgroundColor = Pal.ButtonUndo,
        content = { Text("↩️", fontSize = 14.sp) }
      )
      Text(statusEmoji, fontSize = 24.sp, modifier = Modifier.scale(emojiScale.value))
      // Redo button
      ActionButton(
        onClick = onRedo,
        enabled = canRedo,
        backgroundColor = Pal.ButtonRedo,
        content = { Text("↪️", fontSize = 14.sp) }
      )
    }

    // Right side: Shop + Timer
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      // Shop button
      ActionButton(
        onClick = onShopClick,
        backgroundColor = Pal.SurfaceLight,
        content = { Text("🛒", fontSize = 14.sp) }
      )
      CounterDisplay(elapsedSeconds, "⏱️", Pal.OnSurface, Modifier.scale(if (state == GameState.PLAYING) timerPulse.value else 1f))
    }
  }
}
