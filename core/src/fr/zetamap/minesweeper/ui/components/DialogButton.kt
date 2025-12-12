package fr.zetamap.minesweeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DialogButton(
  text: String,
  onClick: () -> Unit,
  backgroundColor: Color,
  textColor: Color,
  bold: Boolean = false
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(backgroundColor)
      .clickable(onClick = onClick)
      .padding(horizontal = 20.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontSize = 16.sp,
      color = textColor,
      fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
    )
  }
}

