package fr.zetamap.minesweeper

import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import org.jetbrains.compose.ui.tooling.preview.Preview

import fr.zetamap.minesweeper.game.Manager
import fr.zetamap.minesweeper.ui.Pal
import fr.zetamap.minesweeper.ui.MainScreen


@Composable
@Preview
fun App(
  manager: Manager? = null,
  modifier: Modifier = Modifier,
  onGridSizeChanged: (() -> Unit)? = null
) {
  MaterialTheme(
    colorScheme = darkColorScheme(
      primary = Pal.Primary,
      secondary = Pal.Secondary,
      background = Pal.Background,
      surface = Pal.Surface,
      onPrimary = Pal.OnBackground,
      onSecondary = Pal.OnBackground,
      onBackground = Pal.OnBackground,
      onSurface = Pal.OnSurface
    )
  ) {
    Surface(
      modifier = modifier.safeContentPadding(),
      color = Pal.Background
    ) {
      if (manager != null) MainScreen(manager, onGridSizeChanged)
      else MainScreen(onGridSizeChanged = onGridSizeChanged)
    }
  }
}
