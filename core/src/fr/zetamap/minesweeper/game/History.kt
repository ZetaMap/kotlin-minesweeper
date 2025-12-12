package fr.zetamap.minesweeper.game


class History {
  private val undo = mutableListOf<Entry>()
  private val redo = mutableListOf<Entry>()

  fun record(cells: List<Cell>) { record(Entry.ChordEntry(cells)) }
  fun record(cell: Cell) { record(Entry.CellEntry(cell)) }
  fun record(entry: Entry) {
    undo.add(entry)
    clearRedo()
  }

  fun undo(board: Board): Entry? = swap(board, undo, redo)
  fun redo(board: Board): Entry? = swap(board, redo, undo)
  private fun swap(board: Board, from: MutableList<Entry>, to: MutableList<Entry>): Entry? {
    if (from.isEmpty()) return null
    val entry = from.removeLast().swap(board)
    to.add(entry)
    return entry
  }

  fun clear() {
    clearUndo()
    clearRedo()
  }
  fun clearUndo() { undo.clear() }
  fun clearRedo() { redo.clear() }

  fun canUndo(): Boolean = undo.isNotEmpty()
  fun canRedo(): Boolean = redo.isNotEmpty()


  interface Entry {
    fun swap(board: Board): Entry

    data class CellEntry(val cell: Cell) : Entry {
      override fun swap(board: Board): Entry {
        val current = board.getCell(cell)!!
        board.setCell(cell)
        return CellEntry(current)
      }
    }

    data class ChordEntry(val cells: List<Cell>) : Entry {
      override fun swap(board: Board): Entry {
        val current = cells.map { board.getCell(it)!! }
        cells.forEach(board::setCell)
        return ChordEntry(current)
      }
    }
  }
}
