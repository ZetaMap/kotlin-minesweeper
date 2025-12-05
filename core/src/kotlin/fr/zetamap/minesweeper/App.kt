package fr.zetamap.minesweeper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.zetamap.minesweeper.ui.screens.MinesweeperScreen
import fr.zetamap.minesweeper.ui.theme.MinesweeperColors
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = MinesweeperColors.Primary,
            secondary = MinesweeperColors.Secondary,
            background = MinesweeperColors.Background,
            surface = MinesweeperColors.Surface,
            onPrimary = MinesweeperColors.OnBackground,
            onSecondary = MinesweeperColors.OnBackground,
            onBackground = MinesweeperColors.OnBackground,
            onSurface = MinesweeperColors.OnSurface
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            color = MinesweeperColors.Background
        ) {
            MinesweeperScreen()
        }
    }
}
