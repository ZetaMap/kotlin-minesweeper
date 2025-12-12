package fr.zetamap.minesweeper.game


class Manager {
  var board: Board = Board(Preset.Easy)
      private set
  var time: Int = 0
      private set
  var lastRevealedCells: List<Cell> = emptyList()
      private set
  var lastPosition: Position? = null
      private set
  var showingExplosion: Boolean = false
      private set
  var showingVictory: Boolean = false
      private set

  fun newGame(options: GameOptions = board.options) {
    board = Board(options)
    reset()
  }

  fun reset() {
    time = 0
    lastRevealedCells = emptyList()
    lastPosition = null
    showingExplosion = false
    showingVictory = false
  }

  fun revealCell(pos: Position) {
    if (board.state.over) return
    lastRevealedCells = board.revealCell(pos)
    lastPosition = pos
    updateGameState()
  }

  fun toggleFlag(pos: Position) {
    if (board.state.over) return
    board.toggleFlag(pos)
    lastPosition = pos
    updateGameState()
  }

  fun chord(pos: Position) {
    if (board.state.over) return
    lastRevealedCells = board.chord(pos)
    lastPosition = pos
    updateGameState()
  }

  fun undo() {
    board.undo()
    updateGameState()
  }

  fun redo() {
    board.redo()
    updateGameState()
  }

  private fun updateGameState() {
    when (board.state) {
      GameState.LOST -> showingExplosion = true
      GameState.WON -> showingVictory = true
      else -> {
        showingExplosion = false
        showingVictory = false
      }
    }
  }

  fun tick() {
    if (board.state == GameState.PLAYING) time++
  }

  fun clearRecentlyRevealed() {
    lastRevealedCells = emptyList()
  }
}
