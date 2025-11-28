package minesweeper.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import minesweeper.game.GameManager
import minesweeper.model.Difficulty
import minesweeper.model.GameState
import kotlin.random.Random

/**
 * Main game screen containing all game elements.
 */
@Composable
fun GameScreen(
    gameManager: GameManager
) {
    val board by gameManager.board
    val difficulty by gameManager.difficulty
    val isTimerRunning by gameManager.isTimerRunning
    
    // Timer effect
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000)
            gameManager.incrementTimer()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinesweeperTheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.animateContentSize()
        ) {
            // Title
            Text(
                text = "💣 Minesweeper",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            
            // Difficulty selector
            DifficultySelector(
                currentDifficulty = difficulty,
                onDifficultyChange = { gameManager.newGame(it) }
            )
            
            // Status panel
            StatusPanel(
                gameManager = gameManager,
                onNewGame = { gameManager.newGame() }
            )
            
            // Game board
            BoardView(gameManager = gameManager)
            
            // Game state message
            GameStateMessage(gameState = board.gameState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelector(
    currentDifficulty: Difficulty,
    onDifficultyChange: (Difficulty) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MinesweeperTheme.panelBackground)
            .border(
                width = 2.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Difficulty.entries.forEach { diff ->
            FilterChip(
                selected = currentDifficulty == diff,
                onClick = { onDifficultyChange(diff) },
                label = {
                    Text(
                        text = when (diff) {
                            Difficulty.BEGINNER -> "Beginner"
                            Difficulty.INTERMEDIATE -> "Intermediate"
                            Difficulty.EXPERT -> "Expert"
                        }
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MinesweeperTheme.cellUnrevealed,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun GameStateMessage(gameState: GameState) {
    when (gameState) {
        GameState.WON -> {
            WinMessage()
        }
        GameState.LOST -> {
            LoseMessage()
        }
        else -> {
            // Show instructions
            Text(
                text = "Left click to reveal • Right click to flag • Double click to chord",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun WinMessage() {
    var visible by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            visible = !visible
        }
    }
    
    if (visible) {
        Text(
            text = "🎉 YOU WON! 🎉",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MinesweeperTheme.wonColor
        )
    } else {
        Text(
            text = "🎉 YOU WON! 🎉",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Yellow
        )
    }
}

@Composable
private fun LoseMessage() {
    var offset by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            offset = Random.nextDouble(-2.0, 2.0).toFloat()
        }
    }
    
    Text(
        text = "💥 GAME OVER 💥",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MinesweeperTheme.lostColor,
        modifier = Modifier.offset(x = offset.dp)
    )
}
