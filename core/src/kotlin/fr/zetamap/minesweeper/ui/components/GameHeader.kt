package fr.zetamap.minesweeper.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.zetamap.minesweeper.game.GameState
import fr.zetamap.minesweeper.ui.theme.MinesweeperColors

@Composable
fun GameHeader(remainingMines: Int, elapsedSeconds: Int, gameState: GameState, modifier: Modifier = Modifier) {
    val statusEmoji = when (gameState) {
        GameState.NOT_STARTED -> "😊"
        GameState.PLAYING -> "🙂"
        GameState.WON -> "😎"
        GameState.LOST -> "😵"
    }

    val timerPulse = remember { Animatable(1f) }
    LaunchedEffect(gameState) {
        if (gameState == GameState.PLAYING) {
            timerPulse.animateTo(1f, infiniteRepeatable(keyframes { durationMillis = 1000; 1f at 0; 1.05f at 500; 1f at 1000 }, RepeatMode.Restart))
        }
    }

    var previousState by remember { mutableStateOf(gameState) }
    val emojiScale = remember { Animatable(1f) }

    LaunchedEffect(gameState) {
        if (gameState != previousState) {
            previousState = gameState
            emojiScale.animateTo(1.3f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessHigh))
            emojiScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        }
    }

    val mineCounterColor by animateColorAsState(
        when { remainingMines < 0 -> MinesweeperColors.Defeat; remainingMines == 0 -> MinesweeperColors.Victory; else -> MinesweeperColors.OnSurface },
        tween(300), "mineCounterColor"
    )

    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MinesweeperColors.Surface).padding(16.dp),
        Arrangement.SpaceBetween, Alignment.CenterVertically
    ) {
        CounterDisplay(remainingMines, "💣", mineCounterColor)
        Text(statusEmoji, fontSize = 32.sp, modifier = Modifier.scale(emojiScale.value))
        CounterDisplay(elapsedSeconds, "⏱️", MinesweeperColors.OnSurface, Modifier.scale(if (gameState == GameState.PLAYING) timerPulse.value else 1f))
    }
}

@Composable
private fun CounterDisplay(value: Int, emoji: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(8.dp)).background(MinesweeperColors.Background).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(value.toString().padStart(3, '0'), fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = color)
    }
}
