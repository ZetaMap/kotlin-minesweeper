package fr.zetamap.minesweeper.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import fr.zetamap.minesweeper.ui.Pal
import fr.zetamap.minesweeper.ui.components.DialogButton

@Composable
fun ShopDialog(onDismiss: () -> Unit) {
  BaseDialog(onDismiss = onDismiss) {
    val dismissDialog = ::dismiss

    // Title
    Text(
      text = "🛒 Shop",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = Pal.OnSurface
    )

    // Construction message
    Text(
      text = "🚧",
      fontSize = 48.sp
    )

    Text(
      text = "En construction",
      fontSize = 16.sp,
      color = Pal.OnSurface.copy(alpha = 0.7f),
      textAlign = TextAlign.Center
    )

    Text(
      text = "Cette fonctionnalité sera\ndisponible prochainement !",
      fontSize = 14.sp,
      color = Pal.OnSurface.copy(alpha = 0.5f),
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Close button
    DialogButton(
      text = "OK",
      onClick = { dismissDialog(null) },
      backgroundColor = Pal.Primary,
      textColor = Color.White,
      bold = true
    )
  }
}

