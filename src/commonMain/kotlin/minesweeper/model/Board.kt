package minesweeper.model

import kotlin.random.Random

/**
 * Represents the Minesweeper game board.
 */
class Board(
    val difficulty: Difficulty,
    private val random: Random = Random.Default
) {
    val rows: Int = difficulty.rows
    val cols: Int = difficulty.cols
    val totalMines: Int = difficulty.mines
    
    private var _cells: List<List<Cell>> = createEmptyGrid()
    val cells: List<List<Cell>> get() = _cells
    
    var gameState: GameState = GameState.READY
        private set
    
    val flagCount: Int
        get() = cells.flatten().count { it.isFlagged }
    
    val remainingMines: Int
        get() = totalMines - flagCount
    
    private fun createEmptyGrid(): List<List<Cell>> {
        return List(rows) { row ->
            List(cols) { col ->
                Cell(row = row, col = col)
            }
        }
    }
    
    /**
     * Initializes the board with mines, ensuring the first clicked cell is safe.
     */
    private fun initializeMines(safeRow: Int, safeCol: Int) {
        val minePositions = mutableSetOf<Pair<Int, Int>>()
        val safeZone = getSafeZone(safeRow, safeCol)
        
        while (minePositions.size < totalMines) {
            val row = random.nextInt(rows)
            val col = random.nextInt(cols)
            val pos = Pair(row, col)
            
            if (pos !in safeZone && pos !in minePositions) {
                minePositions.add(pos)
            }
        }
        
        _cells = List(rows) { row ->
            List(cols) { col ->
                val isMine = Pair(row, col) in minePositions
                val adjacentMines = if (!isMine) countAdjacentMines(row, col, minePositions) else 0
                Cell(
                    row = row,
                    col = col,
                    isMine = isMine,
                    adjacentMines = adjacentMines
                )
            }
        }
    }
    
    private fun getSafeZone(row: Int, col: Int): Set<Pair<Int, Int>> {
        val zone = mutableSetOf<Pair<Int, Int>>()
        for (dr in -1..1) {
            for (dc in -1..1) {
                val nr = row + dr
                val nc = col + dc
                if (nr in 0 until rows && nc in 0 until cols) {
                    zone.add(Pair(nr, nc))
                }
            }
        }
        return zone
    }
    
    private fun countAdjacentMines(row: Int, col: Int, minePositions: Set<Pair<Int, Int>>): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = row + dr
                val nc = col + dc
                if (Pair(nr, nc) in minePositions) {
                    count++
                }
            }
        }
        return count
    }
    
    /**
     * Reveals a cell at the given position.
     * Returns the list of cells that were revealed (for animation purposes).
     */
    fun revealCell(row: Int, col: Int): List<Cell> {
        if (gameState == GameState.WON || gameState == GameState.LOST) {
            return emptyList()
        }
        
        val cell = cells[row][col]
        if (cell.isRevealed || cell.isFlagged) {
            return emptyList()
        }
        
        // First click initializes the board
        if (gameState == GameState.READY) {
            initializeMines(row, col)
            gameState = GameState.PLAYING
        }
        
        val currentCell = cells[row][col]
        
        if (currentCell.isMine) {
            // Game over - reveal all mines
            gameState = GameState.LOST
            return revealAllMines()
        }
        
        val revealedCells = mutableListOf<Cell>()
        revealCellsRecursively(row, col, revealedCells)
        
        checkWinCondition()
        
        return revealedCells
    }
    
    private fun revealCellsRecursively(row: Int, col: Int, revealed: MutableList<Cell>) {
        if (row !in 0 until rows || col !in 0 until cols) return
        
        val cell = cells[row][col]
        if (cell.isRevealed || cell.isFlagged || cell.isMine) return
        
        val newCell = cell.copy(isRevealed = true)
        updateCell(row, col, newCell)
        revealed.add(newCell)
        
        // If cell has no adjacent mines, reveal neighbors
        if (newCell.adjacentMines == 0) {
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    revealCellsRecursively(row + dr, col + dc, revealed)
                }
            }
        }
    }
    
    private fun revealAllMines(): List<Cell> {
        val revealedMines = mutableListOf<Cell>()
        _cells = cells.map { rowList ->
            rowList.map { cell ->
                if (cell.isMine && !cell.isRevealed) {
                    val revealed = cell.copy(isRevealed = true)
                    revealedMines.add(revealed)
                    revealed
                } else {
                    cell
                }
            }
        }
        return revealedMines
    }
    
    /**
     * Toggles the flag on a cell.
     */
    fun toggleFlag(row: Int, col: Int): Cell? {
        if (gameState == GameState.WON || gameState == GameState.LOST) {
            return null
        }
        
        val cell = cells[row][col]
        if (cell.isRevealed) {
            return null
        }
        
        val newCell = cell.copy(isFlagged = !cell.isFlagged)
        updateCell(row, col, newCell)
        
        checkWinCondition()
        
        return newCell
    }
    
    /**
     * Performs a chord action (reveal surrounding cells if enough flags are placed).
     */
    fun chord(row: Int, col: Int): List<Cell> {
        if (gameState != GameState.PLAYING) {
            return emptyList()
        }
        
        val cell = cells[row][col]
        if (!cell.isRevealed || cell.adjacentMines == 0) {
            return emptyList()
        }
        
        // Count adjacent flags
        var flagCount = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = row + dr
                val nc = col + dc
                if (nr in 0 until rows && nc in 0 until cols && cells[nr][nc].isFlagged) {
                    flagCount++
                }
            }
        }
        
        // If flag count matches adjacent mines, reveal surrounding cells
        if (flagCount == cell.adjacentMines) {
            val revealedCells = mutableListOf<Cell>()
            for (dr in -1..1) {
                for (dc in -1..1) {
                    if (dr == 0 && dc == 0) continue
                    val nr = row + dr
                    val nc = col + dc
                    if (nr in 0 until rows && nc in 0 until cols) {
                        val neighbor = cells[nr][nc]
                        if (!neighbor.isRevealed && !neighbor.isFlagged) {
                            if (neighbor.isMine) {
                                gameState = GameState.LOST
                                return revealAllMines()
                            }
                            revealCellsRecursively(nr, nc, revealedCells)
                        }
                    }
                }
            }
            checkWinCondition()
            return revealedCells
        }
        
        return emptyList()
    }
    
    private fun updateCell(row: Int, col: Int, newCell: Cell) {
        _cells = cells.mapIndexed { r, rowList ->
            if (r == row) {
                rowList.mapIndexed { c, cell ->
                    if (c == col) newCell else cell
                }
            } else {
                rowList
            }
        }
    }
    
    private fun checkWinCondition() {
        val allNonMinesRevealed = cells.flatten()
            .filter { !it.isMine }
            .all { it.isRevealed }
        
        if (allNonMinesRevealed) {
            gameState = GameState.WON
            // Auto-flag remaining mines
            _cells = cells.map { rowList ->
                rowList.map { cell ->
                    if (cell.isMine && !cell.isFlagged) {
                        cell.copy(isFlagged = true)
                    } else {
                        cell
                    }
                }
            }
        }
    }
    
    /**
     * Resets the board for a new game.
     */
    fun reset() {
        _cells = createEmptyGrid()
        gameState = GameState.READY
    }
}
