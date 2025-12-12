package fr.zetamap.minesweeper.ui.fragments

import kotlin.random.Random

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay


private data class Confetti(
  val x: Float,
  val y: Float,
  val size: Float,
  val color: Color,
  val rotation: Float,
  val velocityX: Float,
  val velocityY: Float,
  val rotationSpeed: Float
)

@Composable
fun ConfettiFragment(
  isActive: Boolean,
  modifier: Modifier = Modifier
) {
  val colors = listOf(
    Color(0xFFFF6B6B),
    Color(0xFF4ECDC4),
    Color(0xFFFFE66D),
    Color(0xFF95E1D3),
    Color(0xFFF38181),
    Color(0xFF6C5CE7),
    Color(0xFF00B894),
    Color(0xFFFDCB6E),
    Color(0xFF74B9FF),
    Color(0xFFE17055)
  )

  var confettis by remember { mutableStateOf(emptyList<Confetti>()) }
  var time by remember { mutableFloatStateOf(0f) }
  var spawningEnabled by remember { mutableStateOf(false) }

  // Initialize confettis when activated
  LaunchedEffect(isActive) {
    if (isActive) {
      spawningEnabled = true
      confettis = List(150) {
        Confetti(
          x = Random.nextFloat(),
          y = Random.nextFloat() * -1f, // Start above the screen
          size = Random.nextFloat() * 12f + 6f,
          color = colors.random(),
          rotation = Random.nextFloat() * 360f,
          velocityX = (Random.nextFloat() - 0.5f) * 0.01f,
          velocityY = Random.nextFloat() * 0.008f + 0.003f,
          rotationSpeed = (Random.nextFloat() - 0.5f) * 10f
        )
      }
      // Stop spawning new confettis after 10 seconds
      delay(10000)
      spawningEnabled = false
    }
  }

  // Animation loop
  LaunchedEffect(isActive) {
    if (isActive) {
      val startTime = withFrameNanos { it }
      while (confettis.isNotEmpty()) {
        val currentTime = withFrameNanos { it }
        val deltaTime = (currentTime - startTime) / 1_000_000_000f
        time = deltaTime

        // Update confetti positions
        confettis = confettis.mapNotNull { confetti ->
          val newY = confetti.y + confetti.velocityY + (time * 0.001f)
          val newX = confetti.x + confetti.velocityX + kotlin.math.sin(time * 3f + confetti.x * 10f) * 0.002f
          val newRotation = confetti.rotation + confetti.rotationSpeed

          // If confetti goes off-screen
          if (newY > 1.2f) {
            // Only respawn if spawning is still enabled
            if (spawningEnabled) {
              confetti.copy(
                x = Random.nextFloat(),
                y = -0.1f,
                rotation = Random.nextFloat() * 360f
              )
            } else {
              null // Remove confetti
            }
          } else {
            confetti.copy(
              x = newX,
              y = newY,
              rotation = newRotation
            )
          }
        }
      }
    }
  }

  // Only render if there are confettis
  if (confettis.isEmpty()) return

  Canvas(modifier = modifier.fillMaxSize()) {
    confettis.forEach { confetti ->
      val x = confetti.x * size.width
      val y = confetti.y * size.height

      rotate(confetti.rotation, pivot = Offset(x, y)) {
        drawRect(
          color = confetti.color,
          topLeft = Offset(x - confetti.size / 2, y - confetti.size / 2),
          size = Size(confetti.size, confetti.size * 0.6f)
        )
      }
    }
  }
}

