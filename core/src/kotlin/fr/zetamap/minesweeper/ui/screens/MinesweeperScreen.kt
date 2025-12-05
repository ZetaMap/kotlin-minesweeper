package fr.zetamap.minesweeper.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.zetamap.minesweeper.game.GameState
import fr.zetamap.minesweeper.ui.components.*
import fr.zetamap.minesweeper.ui.theme.MinesweeperColors
import fr.zetamap.minesweeper.viewmodel.MinesweeperViewModel
import kotlinx.coroutines.delay

@Composable
fun MinesweeperScreen(
    viewModel: MinesweeperViewModel = remember { MinesweeperViewModel() }
) {
    // Timer
    LaunchedEffect(viewModel.gameState) {
        while (viewModel.gameState == GameState.PLAYING) {
            delay(1000)
            viewModel.tick()
        }
    }

    // Clear recently revealed cells after animation
    LaunchedEffect(viewModel.recentlyRevealedCells) {
        if (viewModel.recentlyRevealedCells.isNotEmpty()) {
            delay(300)
            viewModel.clearRecentlyRevealed()
        }
    }

    // Title animation
    val titleOffset = remember { Animatable(-50f) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        titleOffset.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    LaunchedEffect(Unit) {
        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(500)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinesweeperColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Row(
                modifier = Modifier
                    .graphicsLayer(
                        translationY = titleOffset.value,
                        alpha = titleAlpha.value
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💣",
                    fontSize = 32.sp
                )
                Text(
                    text = "Démineur",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MinesweeperColors.OnBackground
                )
                Text(
                    text = "💣",
                    fontSize = 32.sp
                )
            }

            // Difficulty selector
            DifficultySelector(
                currentDifficulty = viewModel.difficulty,
                onDifficultySelected = { difficulty ->
                    viewModel.newGame(difficulty)
                }
            )

            // Game header (mines counter, status, timer)
            GameHeader(
                remainingMines = viewModel.remainingMines,
                elapsedSeconds = viewModel.elapsedSeconds,
                gameState = viewModel.gameState
            )

            // New game button
            NewGameButton(
                onClick = { viewModel.newGame() },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Game grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GameGrid(
                    grid = viewModel.grid,
                    gameState = viewModel.gameState,
                    recentlyRevealedCells = viewModel.recentlyRevealedCells,
                    onRevealCell = { row, col -> viewModel.revealCell(row, col) },
                    onFlagCell = { row, col -> viewModel.toggleFlag(row, col) },
                    onChordCell = { row, col -> viewModel.chord(row, col) }
                )
            }

            // Instructions
            InstructionsPanel()
        }

        // Game over overlay
        GameOverOverlay(
            gameState = viewModel.gameState,
            elapsedSeconds = viewModel.elapsedSeconds,
            onNewGame = { viewModel.newGame() }
        )
    }
}

@Composable
private fun InstructionsPanel() {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MinesweeperColors.Surface.copy(alpha = 0.7f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📱 Clic = Révéler | Double-clic / Appui long = Drapeau",
            fontSize = 12.sp,
            color = MinesweeperColors.OnSurface.copy(alpha = 0.8f)
        )
    }
}

