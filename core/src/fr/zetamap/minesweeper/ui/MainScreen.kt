package fr.zetamap.minesweeper.ui

import kotlinx.coroutines.delay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.zetamap.minesweeper.game.GameOptions

import fr.zetamap.minesweeper.game.GameState
import fr.zetamap.minesweeper.game.Manager
import fr.zetamap.minesweeper.game.Preset
import fr.zetamap.minesweeper.ui.dialogs.ConfirmDialog
import fr.zetamap.minesweeper.ui.dialogs.SettingsDialog
import fr.zetamap.minesweeper.ui.dialogs.ShopDialog
import fr.zetamap.minesweeper.ui.fragments.ConfettiFragment
import fr.zetamap.minesweeper.ui.fragments.FlyoutDirection
import fr.zetamap.minesweeper.ui.fragments.FlyoutMenuItem
import fr.zetamap.minesweeper.ui.fragments.FlyoutMenuFragment


@Composable
fun MainScreen(
  manager: Manager = remember(::Manager),
  onGridSizeChanged: (() -> Unit)? = null
) {
  // Trick to force recomposition without using mutableStateOf in Manager
  var tick by remember { mutableIntStateOf(0) }
  fun recompose(action: () -> Unit) {
    action()
    tick++
  }
  fun recomposeLater(action: () -> Unit): () -> Unit = { recompose(action) }
  fun <T>recomposeLater(action: (T) -> Unit): (T) -> Unit = {
    action(it)
    tick++
  }
  @Suppress("UNUSED_EXPRESSION")
  tick

  var showConfirmDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var showDifficultyMenu by remember { mutableStateOf(false) }
  var showShopDialog by remember { mutableStateOf(false) }
  var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
  var customOptions by remember { mutableStateOf(GameOptions(15, 10, 0.5f)) }

  // Request confirmation before action (if game is in progress)
  fun confirmIfNeeded(action: () -> Unit) {
    val state = manager.board.state
    if (state == GameState.PLAYING) {
      pendingAction = action
      showConfirmDialog = true
    } else {
      action()
      tick++
    }
  }

  val board = manager.board
  val state = board.state
  val time = manager.time
  val lastRevealedCells = manager.lastRevealedCells

  // Grid dimensions (use tick to force re-read after newGame)
  val cols = board.options.cols
  val rows = board.options.rows
  val cellSize = when {
    cols <= 8 -> 40
    cols <= 12 -> 35
    cols <= 16 -> 30
    else -> 25
  }

  // Notify window to resize when grid size changes
  LaunchedEffect(rows, cols) {
    onGridSizeChanged?.invoke()
  }

  // Show confetti on victory
  val showConfetti = state == GameState.WON

  // Timer. Pauses when dialogs are shown
  LaunchedEffect(state, showConfirmDialog, showSettingsDialog) {
    while (state == GameState.PLAYING && !showConfirmDialog && !showSettingsDialog) {
      delay(1000)
      recompose(manager::tick)
    }
  }

  // Clear recently revealed cells after animation
  LaunchedEffect(lastRevealedCells) {
    if (lastRevealedCells.any()) {
      delay(300)
      recompose(manager::clearRecentlyRevealed)
    }
  }

  // Calculate grid width for header/footer sizing
  val gridWidth = (cols * (cellSize + 2)).dp
  val minWidth = 450.dp

  Box(
    modifier = Modifier
      .wrapContentSize()
      .background(Pal.Background),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Game header (mines counter, status, timer)
      GameHeader(
        remainingMines = board.remainingMines,
        elapsedSeconds = time,
        state = state,
        showConfirmDialog = showConfirmDialog || showSettingsDialog || showShopDialog,
        onShowConfirmDialog = { showConfirmDialog = it },
        onNewGame = { confirmIfNeeded(recomposeLater { manager.newGame() }) },
        onUndo = recomposeLater(manager::undo),
        onRedo = recomposeLater(manager::redo),
        onShopClick = { showShopDialog = true },
        canUndo = board.canUndo(),
        canRedo = board.canRedo(),
        modifier = Modifier.widthIn(min = minWidth).width(gridWidth)
      )

      // Game grid
      GameGrid(
        grid = board.grid,
        state = state,
        lastRevealedCells = lastRevealedCells,
        lastPosition = manager.lastPosition,
        onRevealCell = recomposeLater(manager::revealCell),
        onFlagCell = recomposeLater(manager::toggleFlag),
        onChordCell = recomposeLater(manager::chord)
      )

      // Game footer (difficulty selector + settings)
      Box(modifier = Modifier.widthIn(min = minWidth).width(gridWidth)) {
        GameFooter(
          currentOptions = board.options,
          onDifficultyClick = { showDifficultyMenu = true },
          onSettingsClick = { showSettingsDialog = true }
        )

        // Difficulty flyout menu
        if (showDifficultyMenu) {
          val currentPreset = board.options as? Preset
          val isCustom = currentPreset == null

          // Build menu items: presets + custom option
          val menuItems = Preset.All.map { preset ->
            FlyoutMenuItem(
              label = preset.name,
              detail = "${preset.rows}×${preset.cols} • ${preset.mines}💣",
              selected = preset == currentPreset
            )
          } + FlyoutMenuItem(
            label = "Custom",
            detail = "${customOptions.rows}×${customOptions.cols} • ${customOptions.mines}💣",
            selected = isCustom
          )

          FlyoutMenuFragment(
            items = menuItems,
            onItemClick = { index ->
              showDifficultyMenu = false
              if (index < Preset.All.size) {
                // Preset selected
                val preset = Preset.All.elementAt(index)
                if (preset != currentPreset) confirmIfNeeded(recomposeLater { manager.newGame(preset) })
              // Custom selected
              } else if (!isCustom) confirmIfNeeded(recomposeLater { manager.newGame(customOptions) })
            },
            onDismiss = { showDifficultyMenu = false },
            direction = FlyoutDirection.UP,
            offsetY = (-50).dp
          )
        }
      }
    }

    // Confirm dialog overlay
    if (showConfirmDialog) {
      ConfirmDialog(
        onConfirm = {
          showConfirmDialog = false
          pendingAction?.invoke()
          pendingAction = null
          tick++
        },
        onDismiss = {
          showConfirmDialog = false
          pendingAction = null
        }
      )
    }

    // Settings dialog overlay
    if (showSettingsDialog) {
      SettingsDialog(
        currentOptions = customOptions,
        onApply = { options ->
          showSettingsDialog = false
          customOptions = options
          if (options != board.options) {
            confirmIfNeeded(recomposeLater { manager.newGame(options) })
          }
        },
        onDismiss = { showSettingsDialog = false }
      )
    }

    // Shop dialog overlay
    if (showShopDialog) {
      ShopDialog(onDismiss = { showShopDialog = false })
    }

    // Confetti fragment on victory
    ConfettiFragment(isActive = showConfetti)
  }
}
