package fr.zetamap.minesweeper.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import fr.zetamap.minesweeper.game.GameOptions
import fr.zetamap.minesweeper.ui.Pal
import fr.zetamap.minesweeper.ui.components.DialogButton

@Composable
fun SettingsDialog(
  currentOptions: GameOptions,
  onApply: (GameOptions) -> Unit,
  onDismiss: () -> Unit
) {
  var rows by remember { mutableIntStateOf(currentOptions.rows) }
  var cols by remember { mutableIntStateOf(currentOptions.cols) }
  var density by remember { mutableFloatStateOf(currentOptions.mineDensity) }

  val mines = (rows * cols * density).toInt().coerceIn(1, rows * cols - 9)
  val hasChanges = rows != currentOptions.rows ||
                   cols != currentOptions.cols ||
                   density != currentOptions.mineDensity

  BaseDialog(
    onDismiss = onDismiss,
    modifier = Modifier.widthIn(min = 280.dp, max = 350.dp)
  ) {
    val dismissDialog = ::dismiss

    // Title
    Text(
      text = "⚙️ Paramètres",
      fontSize = 22.sp,
      fontWeight = FontWeight.Bold,
      color = Pal.OnSurface
    )

    // Custom grid section
    SectionTitle("Grille personnalisée")

    // Rows slider
    SliderSetting(
      label = "Lignes",
      value = rows,
      range = 5..50,
      onValueChange = { rows = it }
    )

    // Cols slider
    SliderSetting(
      label = "Colonnes",
      value = cols,
      range = 5..50,
      onValueChange = { cols = it }
    )

    // Mine percentage slider
    SliderSettingFloat(
      label = "Mines",
      value = density,
      range = 0.1f..0.60f,
      displayValue = "${(density * 100).toInt()}% ($mines mines)",
      onValueChange = { density = it }
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Controls section
    SectionTitle("Commandes")

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Pal.Background)
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      ControlRow("Clic gauche", "Révéler une case")
      ControlRow("Clic droit / Double-clic", "Poser un drapeau")
      Spacer(Modifier.size(10.dp))
      ControlRow("Appuis court", "Révéler une case (mobile)")
      ControlRow("Appuis long / \nDouble appuis", "Poser le drapeau (mobile)")
    }

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
        text = if (hasChanges) "Appliquer" else "OK",
        onClick = { dismissDialog { onApply(GameOptions(rows, cols, density)) } },
        backgroundColor = if (hasChanges) Pal.Primary else Pal.SurfaceLight,
        textColor = Color.White,
        bold = true
      )
    }
  }
}

@Composable
private fun SectionTitle(text: String) {
  Text(
    text = text,
    fontSize = 14.sp,
    fontWeight = FontWeight.SemiBold,
    color = Pal.Primary,
    modifier = Modifier.fillMaxWidth()
  )
}

@Composable
private fun SliderSetting(
  label: String,
  value: Int,
  range: IntRange,
  onValueChange: (Int) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = label,
      fontSize = 13.sp,
      color = Pal.OnSurface,
      modifier = Modifier.width(70.dp)
    )
    Slider(
      value = value.toFloat(),
      onValueChange = { onValueChange(it.toInt()) },
      valueRange = range.first.toFloat()..range.last.toFloat(),
      steps = range.last - range.first - 1,
      modifier = Modifier.weight(1f).height(20.dp),
      colors = SliderDefaults.colors(
        thumbColor = Pal.Primary,
        activeTrackColor = Pal.Primary
      )
    )
    Text(
      text = value.toString(),
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = Pal.OnSurface,
      modifier = Modifier.width(25.dp),
      textAlign = TextAlign.End
    )
  }
}

@Composable
private fun SliderSettingFloat(
  label: String,
  value: Float,
  range: ClosedFloatingPointRange<Float>,
  displayValue: String,
  onValueChange: (Float) -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = label,
        fontSize = 13.sp,
        color = Pal.OnSurface
      )
      Text(
        text = displayValue,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Pal.OnSurface
      )
    }
    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = range,
      modifier = Modifier.fillMaxWidth().height(20.dp),
      colors = SliderDefaults.colors(
        thumbColor = Pal.Primary,
        activeTrackColor = Pal.Primary
      )
    )
  }
}

@Composable
private fun ControlRow(action: String, description: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = action,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      color = Pal.Secondary
    )
    Text(
      text = description,
      fontSize = 12.sp,
      color = Pal.OnSurface.copy(alpha = 0.7f)
    )
  }
}

