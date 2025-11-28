package minesweeper.model

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoardTest {
    
    @Test
    fun `new board should be in READY state`() {
        val board = Board(Difficulty.BEGINNER)
        assertEquals(GameState.READY, board.gameState)
    }
    
    @Test
    fun `first click should initialize board and change state to PLAYING`() {
        val board = Board(Difficulty.BEGINNER)
        board.revealCell(4, 4)
        assertEquals(GameState.PLAYING, board.gameState)
    }
    
    @Test
    fun `first click should never reveal a mine`() {
        // Run multiple times with different random seeds
        repeat(10) { seed ->
            val board = Board(Difficulty.BEGINNER, Random(seed))
            board.revealCell(4, 4)
            
            val cell = board.cells[4][4]
            assertTrue(cell.isRevealed, "First clicked cell should be revealed")
            assertFalse(cell.isMine, "First clicked cell should never be a mine")
        }
    }
    
    @Test
    fun `board should have correct number of mines`() {
        val board = Board(Difficulty.BEGINNER)
        board.revealCell(0, 0) // Initialize the board
        
        val mineCount = board.cells.flatten().count { it.isMine }
        assertEquals(Difficulty.BEGINNER.mines, mineCount)
    }
    
    @Test
    fun `toggling flag should mark cell as flagged`() {
        val board = Board(Difficulty.BEGINNER)
        board.revealCell(0, 0) // Initialize
        
        // Find an unrevealed cell
        val cell = board.cells.flatten().first { !it.isRevealed }
        board.toggleFlag(cell.row, cell.col)
        
        assertTrue(board.cells[cell.row][cell.col].isFlagged)
    }
    
    @Test
    fun `toggling flag twice should unflag cell`() {
        val board = Board(Difficulty.BEGINNER)
        board.revealCell(0, 0) // Initialize
        
        val cell = board.cells.flatten().first { !it.isRevealed }
        board.toggleFlag(cell.row, cell.col)
        board.toggleFlag(cell.row, cell.col)
        
        assertFalse(board.cells[cell.row][cell.col].isFlagged)
    }
    
    @Test
    fun `remaining mines should decrease when flagging`() {
        val board = Board(Difficulty.BEGINNER)
        board.revealCell(0, 0) // Initialize
        
        val initialRemaining = board.remainingMines
        val cell = board.cells.flatten().first { !it.isRevealed }
        board.toggleFlag(cell.row, cell.col)
        
        assertEquals(initialRemaining - 1, board.remainingMines)
    }
    
    @Test
    fun `cannot reveal flagged cell`() {
        val board = Board(Difficulty.BEGINNER)
        board.revealCell(0, 0) // Initialize
        
        val cell = board.cells.flatten().first { !it.isRevealed && !it.isMine }
        board.toggleFlag(cell.row, cell.col)
        val revealed = board.revealCell(cell.row, cell.col)
        
        assertTrue(revealed.isEmpty())
        assertFalse(board.cells[cell.row][cell.col].isRevealed)
    }
    
    @Test
    fun `reset should return board to READY state`() {
        val board = Board(Difficulty.BEGINNER)
        board.revealCell(0, 0)
        assertEquals(GameState.PLAYING, board.gameState)
        
        board.reset()
        assertEquals(GameState.READY, board.gameState)
    }
    
    @Test
    fun `difficulty settings should match expected values`() {
        assertEquals(9, Difficulty.BEGINNER.rows)
        assertEquals(9, Difficulty.BEGINNER.cols)
        assertEquals(10, Difficulty.BEGINNER.mines)
        
        assertEquals(16, Difficulty.INTERMEDIATE.rows)
        assertEquals(16, Difficulty.INTERMEDIATE.cols)
        assertEquals(40, Difficulty.INTERMEDIATE.mines)
        
        assertEquals(16, Difficulty.EXPERT.rows)
        assertEquals(30, Difficulty.EXPERT.cols)
        assertEquals(99, Difficulty.EXPERT.mines)
    }
}
