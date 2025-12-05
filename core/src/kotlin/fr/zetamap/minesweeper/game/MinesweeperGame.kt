package fr.zetamap.minesweeper.game

import kotlin.random.Random

/**
 * Core Minesweeper game logic
 */
class MinesweeperGame(
    val rows: Int,
    val cols: Int,
    val totalMines: Int
) {
    private var _grid: Array<Array<Cell>> = Array(rows) { row ->
        Array(cols) { col -> Cell(row, col) }
    }

    val grid: List<List<Cell>> get() = _grid.map { it.toList() }

    private var _gameState: GameState = GameState.NOT_STARTED
    val gameState: GameState get() = _gameState

    private var minesPlaced = false

    val flagsPlaced: Int
        get() = _grid.flatten().count { it.isFlagged }

    val remainingMines: Int
        get() = totalMines - flagsPlaced

    val revealedCells: Int
        get() = _grid.flatten().count { it.isRevealed }

    /**
     * Reveals a cell at the given position
     * Returns list of cells that were revealed (for animation purposes)
     */
    fun revealCell(row: Int, col: Int): List<Cell> {
        if (!isValidPosition(row, col)) return emptyList()

        val cell = _grid[row][col]
        if (cell.isRevealed || cell.isFlagged) return emptyList()

        // Place mines on first click, ensuring first cell is safe
        if (!minesPlaced) {
            placeMines(row, col)
            minesPlaced = true
            _gameState = GameState.PLAYING
        }

        val revealedCells = mutableListOf<Cell>()

        if (cell.isMine) {
            // Game over - reveal all mines
            revealAllMines()
            _gameState = GameState.LOST
            _grid[row][col] = cell.copy(isRevealed = true)
            revealedCells.add(_grid[row][col])
        } else {
            // Flood fill reveal
            revealCellRecursive(row, col, revealedCells)
        }

        checkWinCondition()
        return revealedCells
    }

    private fun revealCellRecursive(row: Int, col: Int, revealed: MutableList<Cell>) {
        if (!isValidPosition(row, col)) return

        val cell = _grid[row][col]
        if (cell.isRevealed || cell.isFlagged || cell.isMine) return

        _grid[row][col] = cell.copy(isRevealed = true)
        revealed.add(_grid[row][col])

        // If cell has no adjacent mines, reveal neighbors
        if (cell.adjacentMines == 0) {
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr != 0 || dc != 0) {
                        revealCellRecursive(row + dr, col + dc, revealed)
                    }
                }
            }
        }
    }

    /**
     * Toggles flag on a cell
     */
    fun toggleFlag(row: Int, col: Int): Cell? {
        if (!isValidPosition(row, col)) return null

        val cell = _grid[row][col]
        if (cell.isRevealed) return null

        if (_gameState == GameState.NOT_STARTED) {
            _gameState = GameState.PLAYING
        }

        _grid[row][col] = cell.copy(isFlagged = !cell.isFlagged)
        return _grid[row][col]
    }

    /**
     * Chord - reveals all adjacent cells if flag count matches adjacent mines
     */
    fun chord(row: Int, col: Int): List<Cell> {
        if (!isValidPosition(row, col)) return emptyList()

        val cell = _grid[row][col]
        if (!cell.isRevealed || cell.adjacentMines == 0) return emptyList()

        val adjacentFlags = countAdjacentFlags(row, col)
        if (adjacentFlags != cell.adjacentMines) return emptyList()

        val revealedCells = mutableListOf<Cell>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr != 0 || dc != 0) {
                    val newRow = row + dr
                    val newCol = col + dc
                    if (isValidPosition(newRow, newCol)) {
                        val neighbor = _grid[newRow][newCol]
                        if (!neighbor.isRevealed && !neighbor.isFlagged) {
                            if (neighbor.isMine) {
                                revealAllMines()
                                _gameState = GameState.LOST
                                _grid[newRow][newCol] = neighbor.copy(isRevealed = true)
                                revealedCells.add(_grid[newRow][newCol])
                            } else {
                                revealCellRecursive(newRow, newCol, revealedCells)
                            }
                        }
                    }
                }
            }
        }

        checkWinCondition()
        return revealedCells
    }

    private fun placeMines(safeRow: Int, safeCol: Int) {
        val safeZone = mutableSetOf<Pair<Int, Int>>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                safeZone.add(safeRow + dr to safeCol + dc)
            }
        }

        val allPositions = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (r to c !in safeZone) {
                    allPositions.add(r to c)
                }
            }
        }

        allPositions.shuffle(Random)
        val minePositions = allPositions.take(totalMines).toSet()

        // Place mines and calculate adjacent counts
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val isMine = (r to c) in minePositions
                val adjacentMines = if (isMine) 0 else countAdjacentMines(r, c, minePositions)
                _grid[r][c] = _grid[r][c].copy(
                    isMine = isMine,
                    adjacentMines = adjacentMines
                )
            }
        }
    }

    private fun countAdjacentMines(row: Int, col: Int, minePositions: Set<Pair<Int, Int>>): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr != 0 || dc != 0) {
                    if ((row + dr to col + dc) in minePositions) {
                        count++
                    }
                }
            }
        }
        return count
    }

    private fun countAdjacentFlags(row: Int, col: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr != 0 || dc != 0) {
                    val r = row + dr
                    val c = col + dc
                    if (isValidPosition(r, c) && _grid[r][c].isFlagged) {
                        count++
                    }
                }
            }
        }
        return count
    }

    private fun revealAllMines() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (_grid[r][c].isMine) {
                    _grid[r][c] = _grid[r][c].copy(isRevealed = true)
                }
            }
        }
    }

    private fun checkWinCondition() {
        if (_gameState != GameState.PLAYING) return

        val nonMineCells = rows * cols - totalMines
        val revealedNonMines = _grid.flatten().count { it.isRevealed && !it.isMine }

        if (revealedNonMines == nonMineCells) {
            _gameState = GameState.WON
            // Auto-flag remaining mines
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (_grid[r][c].isMine && !_grid[r][c].isFlagged) {
                        _grid[r][c] = _grid[r][c].copy(isFlagged = true)
                    }
                }
            }
        }
    }

    private fun isValidPosition(row: Int, col: Int): Boolean {
        return row in 0 until rows && col in 0 until cols
    }

    fun getCell(row: Int, col: Int): Cell? {
        return if (isValidPosition(row, col)) _grid[row][col] else null
    }

    companion object {
        fun create(difficulty: Difficulty): MinesweeperGame {
            return MinesweeperGame(difficulty.rows, difficulty.cols, difficulty.mines)
        }
    }
}

