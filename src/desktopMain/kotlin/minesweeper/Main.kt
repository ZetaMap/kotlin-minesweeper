package minesweeper

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import minesweeper.game.GameManager
import minesweeper.ui.GameScreen

fun main() = application {
    val gameManager = GameManager()
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "Minesweeper",
        state = rememberWindowState(
            size = DpSize(800.dp, 700.dp)
        ),
        resizable = true
    ) {
        MaterialTheme {
            GameScreen(gameManager = gameManager)
        }
    }
}
