package com.example.sudokugame.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sudokugame.model.SudokuBoard
import com.example.sudokugame.data.Difficulty
import com.example.sudokugame.model.SudokuGenerator
import com.example.sudokugame.ui.ProgressData
import com.example.sudokugame.util.GameTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SudokuViewModel : ViewModel() {
    private val _board = MutableStateFlow(SudokuBoard())
    val board: StateFlow<SudokuBoard> = _board

    private val _selectedCell = MutableStateFlow<Pair<Int, Int>?>(null)
    val selectedCell: StateFlow<Pair<Int, Int>?> = _selectedCell

    private val _hintsRemaining = MutableStateFlow(0)
    val hintsRemaining: StateFlow<Int> = _hintsRemaining

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver

    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage

    private val timer = GameTimer()
    val time: StateFlow<Long> = timer.time

    private var solution: SudokuBoard? = null
    private var initialBoard: SudokuBoard? = null

    private val undoStack = mutableListOf<SudokuBoard>()

    fun startNewGame(difficulty: Difficulty) {
        val generated = SudokuGenerator.generate(difficulty.filledCells)
        _board.value = generated.copy()
        initialBoard = generated.copy()
        solution = solveBoard(generated)
        _hintsRemaining.value = difficulty.hints
        _isGameOver.value = false
        _selectedCell.value = null
        _statusMessage.value = ""
        timer.reset()
    }

    fun startTimer() {
        timer.start()
    }

    fun stopTimer() {
        timer.stop()
    }

    fun resetBoard() {
        initialBoard?.let {
            _board.value = it.copy()
            _selectedCell.value = null
            _isGameOver.value = false
            _statusMessage.value = ""
            timer.reset()
        }
    }

    fun selectCell(row: Int, col: Int) {
        if (_isGameOver.value) return
        val cell = _board.value.getCell(row, col)
        if (cell.isFixed) return
        _selectedCell.value = Pair(row, col)
    }

    fun setCellValue(value: Int) {
        val selected = _selectedCell.value ?: return
        val (row, col) = selected
        val currentBoard = _board.value
        val cell = currentBoard.getCell(row, col)
        if (cell.isFixed || _isGameOver.value) return
        if (cell.value == value) {
            // Подсветить все такие клетки и показать подсказку
            val newBoard = currentBoard.copy()
            for (r in 0 until newBoard.size) {
                for (c in 0 until newBoard.size) {
                    val ccell = newBoard.getCell(r, c)
                    if (ccell.value == value && value != 0) {
                        newBoard.cells[r][c] = ccell.copy(isError = true)
                    }
                }
            }
            _board.value = newBoard
            _statusMessage.value = "Это число уже стоит в выбранной клетке!" // Можно заменить на Snackbar
            return
        }
        undoStack.add(currentBoard.copy())
        val newBoard = currentBoard.copy()
        newBoard.cells[row][col] = cell.copy(value = value)
        newBoard.clearErrors()
        _board.value = newBoard
        _statusMessage.value = ""
    }

    fun deleteCellValue() {
        val selected = _selectedCell.value ?: return
        val (row, col) = selected
        val currentBoard = _board.value
        val cell = currentBoard.getCell(row, col)
        if (cell.isFixed || _isGameOver.value) return
        undoStack.add(currentBoard.copy())
        val newBoard = currentBoard.copy()
        newBoard.cells[row][col] = cell.copy(value = 0)
        newBoard.clearErrors()
        _board.value = newBoard
        _statusMessage.value = ""
    }

    fun useHint() {
        val selected = _selectedCell.value ?: return
        if (_hintsRemaining.value <= 0 || _isGameOver.value) return
        val (row, col) = selected
        val currentBoard = _board.value
        val cell = currentBoard.getCell(row, col)
        if (cell.isFixed) return
        val correct = solution?.getCell(row, col)?.value ?: return
        undoStack.add(currentBoard.copy())
        val newBoard = currentBoard.copy()
        newBoard.cells[row][col] = cell.copy(value = correct)
        newBoard.clearErrors()
        _board.value = newBoard
        _hintsRemaining.value = _hintsRemaining.value - 1
        _statusMessage.value = ""
    }

    fun checkSolution() {
        val sol = solution ?: return
        var hasError = false
        for (row in 0 until _board.value.size) {
            for (col in 0 until _board.value.size) {
                val userCell = _board.value.getCell(row, col)
                val solCell = sol.getCell(row, col)
                if (userCell.value != solCell.value) {
                    userCell.isError = true
                    hasError = true
                } else {
                    userCell.isError = false
                }
            }
        }
        if (!hasError) {
            _isGameOver.value = true
            timer.stop()
            _statusMessage.value = "Поздравляем! Судоку решено верно."
        } else {
            _statusMessage.value = "Есть ошибки! Исправьте выделенные клетки."
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            _board.value = undoStack.removeAt(undoStack.lastIndex)
        }
    }

    fun loadProgress(progress: ProgressData) {
        _board.value = progress.board.copy()
        _hintsRemaining.value = progress.hints
        _selectedCell.value = null
        _isGameOver.value = false
        _statusMessage.value = ""
        timer.reset()
        for (i in 0 until progress.time) timer.start() // Быстро "накручиваем" таймер (или можно просто установить значение)
    }

    // Решатель для получения полного решения (backtracking)
    private fun solveBoard(board: SudokuBoard): SudokuBoard? {
        val copy = board.copy()
        return if (solve(copy)) copy else null
    }

    private fun solve(board: SudokuBoard): Boolean {
        for (row in 0 until board.size) {
            for (col in 0 until board.size) {
                if (board.getCell(row, col).value == 0) {
                    for (num in 1..9) {
                        if (isValid(board, row, col, num)) {
                            board.getCell(row, col).value = num
                            if (solve(board)) return true
                            board.getCell(row, col).value = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(board: SudokuBoard, row: Int, col: Int, num: Int): Boolean {
        for (x in 0 until board.size) {
            if (board.getCell(row, x).value == num) return false
        }
        for (x in 0 until board.size) {
            if (board.getCell(x, col).value == num) return false
        }
        val startRow = row - row % 3
        val startCol = col - col % 3
        for (i in 0..2) {
            for (j in 0..2) {
                if (board.getCell(i + startRow, j + startCol).value == num) return false
            }
        }
        return true
    }
} 