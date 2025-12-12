package fr.zetamap.minesweeper.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

import kotlinx.browser.document

import fr.zetamap.minesweeper.App


@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  ComposeViewport(document.body!!) {
    App()
  }
}
