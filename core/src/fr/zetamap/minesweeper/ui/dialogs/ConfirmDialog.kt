package fr.zetamap.minesweeper.ui.dialogs

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import fr.zetamap.minesweeper.ui.Pal
import fr.zetamap.minesweeper.ui.components.DialogButton


@Composable
fun ConfirmDialog(
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  // Emoji animation
  val emojiScale = remember { Animatable(1f) }
  LaunchedEffect(Unit) {
    emojiScale.animateTo(
      targetValue = 1.2f,
      animationSpec = infiniteRepeatable(
        animation = keyframes {
          durationMillis = 800
          1f at 0
          1.2f at 400
          1f at 800
        },
        repeatMode = RepeatMode.Restart
      )
    )
  }

  BaseDialog(onDismiss = onDismiss) {
    val dismissDialog = ::dismiss

    // Emoji
    Text(
      text = "🔄",
      fontSize = 64.sp,
      modifier = Modifier.scale(emojiScale.value)
    )

    // Title
    Text(
      text = "Recommencer ?",
      fontSize = 32.sp,
      fontWeight = FontWeight.Bold,
      color = Pal.Warning
    )

    // Message
    Text(
      text = "La partie en cours sera perdue.",
      fontSize = 16.sp,
      color = Pal.OnSurface,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Buttons
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      DialogButton(
        text = "Annuler",
        onClick = { dismissDialog(null) },
        backgroundColor = Pal.SurfaceLight,
        textColor = Pal.OnSurface
      )
      DialogButton(
        text = "Oui",
        onClick = { dismissDialog(onConfirm) },
        backgroundColor = Pal.ButtonNewGame,
        textColor = Color.White,
        bold = true
      )
    }
  }
}

