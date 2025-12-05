package fr.zetamap.minesweeper.ui.components

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.zetamap.minesweeper.game.GameState
import fr.zetamap.minesweeper.ui.theme.MinesweeperColors

@Composable
fun GameOverOverlay(
    gameState: GameState,
    elapsedSeconds: Int,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (gameState != GameState.WON && gameState != GameState.LOST) return

    val isVictory = gameState == GameState.WON

    // Entrance animation
    val overlayAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.5f) }

    LaunchedEffect(gameState) {
        overlayAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(300)
        )
        contentScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Confetti/explosion animation for the emoji
    val emojiRotation = remember { Animatable(0f) }
    val emojiScale = remember { Animatable(1f) }

    LaunchedEffect(isVictory) {
        if (isVictory) {
            // Victory celebration
            emojiScale.animateTo(
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 500
                        1f at 0
                        1.5f at 250
                        1f at 500
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            // Defeat shake
            emojiRotation.animateTo(
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 200
                        (-10f) at 50
                        10f at 150
                        0f at 200
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(alpha = overlayAlpha.value)
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .scale(contentScale.value)
                .clip(RoundedCornerShape(24.dp))
                .background(MinesweeperColors.Surface)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Emoji
            Text(
                text = if (isVictory) "🎉" else "💥",
                fontSize = 64.sp,
                modifier = Modifier
                    .scale(emojiScale.value)
                    .graphicsLayer(rotationZ = emojiRotation.value)
            )

            // Title
            Text(
                text = if (isVictory) "VICTOIRE !" else "GAME OVER",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (isVictory) MinesweeperColors.Victory else MinesweeperColors.Defeat
            )

            // Message
            Text(
                text = if (isVictory) {
                    "Félicitations !\nTemps: ${formatTime(elapsedSeconds)}"
                } else {
                    "Vous avez touché une mine !\nTemps: ${formatTime(elapsedSeconds)}"
                },
                fontSize = 16.sp,
                color = MinesweeperColors.OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // New game button
            NewGameButton(onClick = onNewGame)
        }
    }
}

@Composable
fun NewGameButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(MinesweeperColors.Primary)
            .clickable {
                isPressed = true
                onClick()
            }
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🔄",
                fontSize = 20.sp
            )
            Text(
                text = "Nouvelle Partie",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins > 0) {
        "${mins}m ${secs}s"
    } else {
        "${secs}s"
    }
}

