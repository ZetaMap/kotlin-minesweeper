package fr.zetamap.minesweeper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import fr.zetamap.minesweeper.game.GameOptions

@Composable
fun GameFooter(
  currentOptions: GameOptions,
  onDifficultyClick: () -> Unit,
  onSettingsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(Pal.Surface)
      .padding(10.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Difficulty button (icon only)
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(Pal.Background)
        .clickable(onClick = onDifficultyClick)
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "📊", fontSize = 14.sp)
    }

    // Settings button
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(Pal.SurfaceLight)
        .clickable(onClick = onSettingsClick)
        .padding(8.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(text = "⚙️", fontSize = 14.sp)
    }
  }
}

