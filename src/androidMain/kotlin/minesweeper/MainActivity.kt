package minesweeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import minesweeper.game.GameManager
import minesweeper.ui.GameScreen

class MainActivity : ComponentActivity() {
    private val gameManager = GameManager()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GameScreen(gameManager = gameManager)
            }
        }
    }
}
