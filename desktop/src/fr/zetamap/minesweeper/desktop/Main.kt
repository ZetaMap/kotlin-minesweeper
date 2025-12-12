package fr.zetamap.minesweeper.desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import fr.zetamap.minesweeper.App


fun main() = application {
  Window(
    onCloseRequest = ::exitApplication,
    title = "Minesweeper",
    icon = painterResource("assets/icons/icon.png"),
   // resizable = false
  ) {
    App(
      modifier = Modifier.fillMaxSize()/*.wrapContentSize()*/,
      onGridSizeChanged = window::pack
    )
  }
}
