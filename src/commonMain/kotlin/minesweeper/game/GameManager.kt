package minesweeper.game

import androidx.compose.runtime.*
import minesweeper.model.Board
import minesweeper.model.Cell
import minesweeper.model.Difficulty
import minesweeper.model.GameState

/**
 * Manages the game state and provides reactive updates to the UI.
 */
class GameManager {
    private val _board = mutableStateOf(Board(Difficulty.BEGINNER))
    val board: State<Board> = _board
    
    private val _difficulty = mutableStateOf(Difficulty.BEGINNER)
    val difficulty: State<Difficulty> = _difficulty
    
    private val _elapsedSeconds = mutableStateOf(0)
    val elapsedSeconds: State<Int> = _elapsedSeconds
    
    private val _isTimerRunning = mutableStateOf(false)
    val isTimerRunning: State<Boolean> = _isTimerRunning
    
    // Animation state for cells that were recently revealed
    private val _revealingCells = mutableStateListOf<Cell>()
    val revealingCells: List<Cell> get() = _revealingCells
    
    // Animation state for cells that were recently flagged/unflagged
    private val _animatingFlags = mutableStateMapOf<Pair<Int, Int>, Boolean>()
    val animatingFlags: Map<Pair<Int, Int>, Boolean> get() = _animatingFlags
    
    // Explosion animation state
    private val _explodingCell = mutableStateOf<Cell?>(null)
    val explodingCell: State<Cell?> = _explodingCell
    
    fun onCellClick(row: Int, col: Int) {
        val currentBoard = _board.value
        
        if (currentBoard.gameState == GameState.READY) {
            _isTimerRunning.value = true
        }
        
        val revealedCells = currentBoard.revealCell(row, col)
        
        if (currentBoard.gameState == GameState.LOST) {
            _isTimerRunning.value = false
            // Find the mine that was clicked
            val clickedMine = currentBoard.cells[row][col]
            if (clickedMine.isMine) {
                _explodingCell.value = clickedMine
            }
        }
        
        if (currentBoard.gameState == GameState.WON) {
            _isTimerRunning.value = false
        }
        
        // Add cells to the revealing animation queue
        _revealingCells.clear()
        _revealingCells.addAll(revealedCells)
        
        // Trigger recomposition
        _board.value = currentBoard
    }
    
    fun onCellRightClick(row: Int, col: Int) {
        val currentBoard = _board.value
        val flaggedCell = currentBoard.toggleFlag(row, col)
        
        if (flaggedCell != null) {
            _animatingFlags[Pair(row, col)] = flaggedCell.isFlagged
        }
        
        if (currentBoard.gameState == GameState.WON) {
            _isTimerRunning.value = false
        }
        
        // Trigger recomposition
        _board.value = currentBoard
    }
    
    fun onCellDoubleClick(row: Int, col: Int) {
        val currentBoard = _board.value
        val revealedCells = currentBoard.chord(row, col)
        
        if (currentBoard.gameState == GameState.LOST) {
            _isTimerRunning.value = false
            val cell = currentBoard.cells[row][col]
            if (cell.isMine) {
                _explodingCell.value = cell
            }
        }
        
        if (currentBoard.gameState == GameState.WON) {
            _isTimerRunning.value = false
        }
        
        _revealingCells.clear()
        _revealingCells.addAll(revealedCells)
        
        // Trigger recomposition
        _board.value = currentBoard
    }
    
    fun newGame(difficulty: Difficulty = _difficulty.value) {
        _difficulty.value = difficulty
        _board.value = Board(difficulty)
        _elapsedSeconds.value = 0
        _isTimerRunning.value = false
        _revealingCells.clear()
        _animatingFlags.clear()
        _explodingCell.value = null
    }
    
    fun incrementTimer() {
        if (_isTimerRunning.value && _elapsedSeconds.value < 999) {
            _elapsedSeconds.value++
        }
    }
    
    fun clearRevealingAnimation(cell: Cell) {
        _revealingCells.remove(cell)
    }
    
    fun clearFlagAnimation(row: Int, col: Int) {
        _animatingFlags.remove(Pair(row, col))
    }
    
    fun clearExplosionAnimation() {
        _explodingCell.value = null
    }
}
