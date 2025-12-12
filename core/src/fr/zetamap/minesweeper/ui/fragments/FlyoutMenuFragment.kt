package fr.zetamap.minesweeper.ui.fragments

import kotlinx.coroutines.launch

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

import fr.zetamap.minesweeper.ui.Pal


enum class FlyoutDirection {
  UP, DOWN, LEFT, RIGHT;

  fun toAlignment(): Alignment =
    when (this) {
      UP -> Alignment.BottomStart
      DOWN -> Alignment.TopStart
      LEFT -> Alignment.CenterEnd
      RIGHT -> Alignment.CenterStart
    }

  fun toTransformOrigin(): TransformOrigin =
    when (this) {
      UP -> TransformOrigin(0.5f, 1f)
      DOWN -> TransformOrigin(0.5f, 0f)
      LEFT -> TransformOrigin(1f, 0.5f)
      RIGHT -> TransformOrigin(0f, 0.5f)
    }
}

data class FlyoutMenuItem(
  val label: String,
  val detail: String? = null,
  val selected: Boolean = false
)

@Composable
fun FlyoutMenuFragment(
  items: List<FlyoutMenuItem>,
  onItemClick: (Int) -> Unit,
  onDismiss: () -> Unit,
  direction: FlyoutDirection = FlyoutDirection.UP,
  offsetX: Dp = 0.dp,
  offsetY: Dp = 0.dp
) {
  val scope = rememberCoroutineScope()

  // Animation state
  val expandProgress = remember { Animatable(0f) }
  var isClosing by remember { mutableStateOf(false) }

  // Animated dismiss function
  fun animatedDismiss(callback: (() -> Unit)? = null) {
    if (isClosing) return
    isClosing = true
    scope.launch {
      expandProgress.animateTo(0f, tween(120))
      callback?.invoke()
      onDismiss()
    }
  }

  // Entrance animation
  LaunchedEffect(Unit) {
    expandProgress.animateTo(
      targetValue = 1f,
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
      )
    )
  }

  Popup(
    onDismissRequest = { animatedDismiss() },
    properties = PopupProperties(focusable = true),
    offset = IntOffset(offsetX.value.toInt(), offsetY.value.toInt()),
    alignment = direction.toAlignment()
  ) {
    Column(
      modifier = Modifier
        .graphicsLayer(
          scaleX = expandProgress.value,
          scaleY = expandProgress.value,
          alpha = expandProgress.value,
          transformOrigin = direction.toTransformOrigin()
        )
        .clip(RoundedCornerShape(8.dp))
        .background(Pal.Surface)
        .padding(4.dp),
      verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
      items.forEachIndexed { index, item ->
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (item.selected) Pal.Primary.copy(alpha = 0.15f) else Color.Transparent)
            .clickable { animatedDismiss { onItemClick(index) } }
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = item.label,
            fontSize = 13.sp,
            fontWeight = if (item.selected) FontWeight.Bold else FontWeight.Normal,
            color = if (item.selected) Pal.Primary else Pal.OnSurface
          )
          if (item.detail != null) {
            Text(
              text = item.detail,
              fontSize = 10.sp,
              color = Pal.OnSurface.copy(alpha = 0.5f)
            )
          }
        }
      }
    }
  }
}

