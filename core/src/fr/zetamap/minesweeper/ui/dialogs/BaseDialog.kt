package fr.zetamap.minesweeper.ui.dialogs

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import fr.zetamap.minesweeper.ui.Pal


interface DialogScope : ColumnScope {
  fun dismiss(onComplete: (() -> Unit)? = null)
}

private class DialogScopeImpl(
  private val columnScope: ColumnScope,
  private val animatedDismiss: ((() -> Unit)?) -> Unit
) : DialogScope, ColumnScope by columnScope {
  override fun dismiss(onComplete: (() -> Unit)?) {
    animatedDismiss(onComplete)
  }
}


@Composable
fun BaseDialog(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable DialogScope.() -> Unit
) {
  val scope = rememberCoroutineScope()

  // Animation states
  val overlayAlpha = remember { Animatable(0f) }
  val contentScale = remember { Animatable(0.5f) }
  var isClosing by remember { mutableStateOf(false) }

  // Animated dismiss function
  fun animatedDismiss(onComplete: (() -> Unit)? = null) {
    if (isClosing) return
    isClosing = true
    scope.launch {
      launch { overlayAlpha.animateTo(0f, tween(200)) }
      contentScale.animateTo(0.5f, tween(150))
      onComplete?.invoke()
      onDismiss()
    }
  }

  // Entrance animation
  LaunchedEffect(Unit) {
    launch { overlayAlpha.animateTo(1f, tween(300)) }
    contentScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .graphicsLayer(alpha = overlayAlpha.value)
      .background(Color.Black.copy(alpha = 0.7f))
      .clickable(onClick = ::animatedDismiss),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = modifier
        .scale(contentScale.value)
        .clip(RoundedCornerShape(20.dp))
        .background(Pal.Surface)
        .clickable(enabled = false, onClick = {})
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      val dialogScope = DialogScopeImpl(this, ::animatedDismiss)
      dialogScope.content()
    }
  }
}

