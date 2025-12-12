package fr.zetamap.minesweeper.game

import kotlin.random.Random


class Board(val options: GameOptions) {
  companion object {
    private val sides: Set<Pair<Int, Int>> = setOf(
      -1 to -1, -1 to 0, -1 to 1,
       0 to -1,           0 to 1,
       1 to -1,  1 to 0,  1 to 1
    )

    operator fun Array<Array<Cell>>.get(pos: Position): Cell = this[pos.x][pos.y]
    operator fun Array<Array<Cell>>.set(pos: Position, cell: Cell) { this[pos.x][pos.y] = cell }

    operator fun List<List<Cell>>.get(pos: Position): Cell = this[pos.x][pos.y]
  }

  private var _grid: Array<Array<Cell>> = Array(options.rows) { row ->
                                          Array(options.cols) { col -> Cell(row, col) } }

  val grid: List<List<Cell>> get() = _grid.map() { it.toList() }
  val flags: Int get() = _grid.sumOf { row -> row.count { it.isFlagged } }
  val remainingMines: Int get() = options.mines - flags
  val revealedCells: Int get() = _grid.sumOf { row -> row.count { col -> col.isRevealed } }
  val history: History = History()

  var state: GameState = GameState.NOT_STARTED
      private set
  var minesPlaced: Boolean = false
      private set

  fun isValidPosition(pos: Position): Boolean = isValidPosition(pos.x, pos.y)
  fun isValidPosition(x: Int, y: Int): Boolean =
    // Not a very optimized approach =/
    x in 0 until options.rows && y in 0 until options.cols

  fun getCell(pos: Position): Cell? = if (isValidPosition(pos)) _grid[pos] else null
  fun getCell(x: Int, y: Int): Cell? = if (isValidPosition(x, y)) _grid[x][y] else null

  fun setCell(cell: Cell) {
    if (isValidPosition(cell)) _grid[cell] = cell
  }

  fun undo() { history.undo(this) }
  fun redo() { history.redo(this) }

  fun canUndo(): Boolean = history.canUndo() && !state.over
  fun canRedo(): Boolean = history.canRedo() && !state.over

  fun each(consumer: (Cell) -> Unit) {
    _grid.forEach { it.forEach(consumer) }
  }
  fun each(condition: (Cell) -> Boolean, consumer: (Cell) -> Unit) {
    _grid.forEach { row -> row.forEach { cell ->
      if (condition(cell)) consumer(cell)
    } }
  }

  fun eachSides(pos: Position, consumer: (Cell) -> Unit) {
    for ((x, y) in sides) getCell(pos.x+x, pos.y+y)?.let(consumer)
  }

  fun setEach(setter: (Cell) -> Cell) {
    _grid.forEach { row -> row.forEach { cell -> _grid[cell] = setter(cell) } }
  }
  fun setEach(condition: (Cell) -> Boolean, setter: (Cell) -> Cell) {
    _grid.forEach { row -> row.forEach { cell ->
      if (condition(cell)) _grid[cell] = setter(cell)
    } }
  }

  fun revealCell(pos: Position): List<Cell> = revealCell(pos.x, pos.y)
  /**
   * Reveals a cell at the given position.
   * This will start the game if not already.
   * @return the list of cells that were revealed.
   */
  fun revealCell(x: Int, y: Int): List<Cell> {
    if (!isValidPosition(x, y) || state.over) return emptyList()
    val cell = _grid[x][y]
    if (cell.isRevealed || cell.isFlagged) return emptyList()

    // Place mines on first click, ensuring first cell is safe
    if (!minesPlaced) {
      placeMines(cell)
      state = GameState.PLAYING
    }

    val revealedCells = mutableListOf<Cell>()
    if (cell.isMine) {
      lost()
      _grid[cell] = cell.copy(isRevealed = true)
      revealedCells.add(_grid[cell])
      history.record(cell)
    } else {
      val beforeCells = mutableListOf<Cell>()
      // Flood fill reveal
      revealCells(cell, revealedCells, beforeCells)
      if (beforeCells.any()) {
        if (beforeCells.size == 1) history.record(beforeCells.first())
        else history.record(beforeCells)
      }
    }

    checkForWin()
    return revealedCells
  }

  private fun revealCells(pos: Position, revealed: MutableList<Cell>, beforeCells: MutableList<Cell>) {
    val cell = _grid[pos]
    if (cell.isRevealed || cell.isFlagged || cell.isMine) return

    // Save state before modification
    beforeCells.add(cell)
    _grid[cell] = cell.copy(isRevealed = true)
    revealed.add(_grid[cell])

    // If cell has no adjacent mines, reveal neighbors
    if (cell.adjacentMines == 0)
      eachSides(cell) { revealCells(it, revealed, beforeCells) }
  }

  fun toggleFlag(pos: Position): Cell? = toggleFlag(pos.x, pos.y)
  /**
   * Toggles flag on a cell.
   * This will start the game if not already.
   * @return {@code null} if the position is invalid or the cell is already revealed, else the updated cell.
   */
  fun toggleFlag(x: Int, y: Int): Cell? {
    if (!isValidPosition(x, y) || state.over) return null

    val cell = _grid[x][y]
    if (cell.isRevealed) return null

    // This will also start the game
    if (state == GameState.NOT_STARTED)
      state = GameState.PLAYING

    _grid[x][y] = cell.copy(isFlagged = !cell.isFlagged)
    history.record(cell)
    return _grid[x][y]
  }

  fun chord(pos: Position): List<Cell> = chord(pos.x, pos.y)
  /** Reveals all adjacent cells if flag count matches adjacent mines. */
  fun chord(x: Int, y: Int): List<Cell> {
    val cell = getCell(x, y)
    if (cell == null || !cell.isRevealed || cell.adjacentMines == 0) return emptyList()

    val adjacentFlags = adjacentFlags(cell)
    if (adjacentFlags != cell.adjacentMines) return emptyList()

    val beforeCells = mutableListOf<Cell>()
    val revealedCells = mutableListOf<Cell>()
    eachSides(cell) {
      if (it.isRevealed || it.isFlagged) return@eachSides
      if (it.isMine) {
        lost()
        _grid[it] = it.copy(isRevealed = true)
        beforeCells.add(it)
        revealedCells.add(_grid[it])
      } else revealCells(it, revealedCells, beforeCells)
    }

    if (beforeCells.any()) {
      if (beforeCells.size == 1) history.record(beforeCells.first())
      else history.record(beforeCells)
    }
    checkForWin()
    return revealedCells
  }

  /** Trigger a game over. This will reveal all mines. */
  fun lost() {
    state = GameState.LOST
    setEach({ it.isMine }, { it.copy(isRevealed = true) })
  }

  /** Trigger a win. This will flag remaining mines. */
  fun win() {
    state = GameState.WON
    setEach({ it.isMine && !it.isFlagged }, { it.copy(isFlagged = true) })
  }

  fun checkForWin() {
    if (state != GameState.PLAYING) return

    val nonMineCells = options.cells - options.mines
    val revealedNonMines = _grid.sumOf { row -> row.count { col -> col.isRevealed && !col.isMine } }
    if (revealedNonMines == nonMineCells) win()
  }

  /** Place the mines on the board. Do nothing if mines are already placed. */
  fun placeMines(pos: Position) {
    if (minesPlaced) return
    minesPlaced = true

    val safeZone = mutableSetOf<Position>()
    safeZone.add(pos)
    eachSides(pos, safeZone::add)

    val mines = arrayListOf<Position>()
    each({ it !in safeZone }, mines::add)
    mines.shuffle(Random)
    mines.subList(options.mines, mines.size).clear()

    // Place mines and calculate adjacent counts
    each {
      val isMine = it in mines
      _grid[it] = _grid[it].copy(
        isMine = isMine,
        adjacentMines = if (isMine) 0 else adjacentMines(it, mines)
      )
    }
  }

  private fun adjacentMines(pos: Position, mines: List<Position>): Int {
    var count = 0
    eachSides(pos) {
      if (it in mines) count++
    }
    return count
  }

  fun adjacentMines(pos: Position): Int {
    var count = 0
    eachSides(pos) {
      if (it.isMine) count++
    }
    return count
  }

  fun adjacentFlags(pos: Position): Int {
    var count = 0
    eachSides(pos) {
      if (it.isFlagged) count++
    }
    return count
  }
}
