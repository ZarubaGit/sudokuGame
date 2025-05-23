package com.example.sudokugame.model

import kotlin.random.Random

object SudokuGenerator {
    fun generate(filledCells: Int): SudokuBoard {
        val board = SudokuBoard()
        fillBoard(board)
        removeCells(board, filledCells)
        for (row in 0 until board.size) {
            for (col in 0 until board.size) {
                val cell = board.getCell(row, col)
                if (cell.value != 0) {
                    board.cells[row][col] = cell.copy(isFixed = true)
                }
            }
        }
        return board
    }

    private fun fillBoard(board: SudokuBoard): Boolean {
        val emptyCell = findEmptyCell(board)
        if (emptyCell == null) return true

        val (row, col) = emptyCell
        val numbers = (1..9).toList().shuffled()

        for (num in numbers) {
            if (isValid(board, row, col, num)) {
                board.getCell(row, col).value = num
                if (fillBoard(board)) return true
                board.getCell(row, col).value = 0
            }
        }
        return false
    }

    private fun findEmptyCell(board: SudokuBoard): Pair<Int, Int>? {
        for (row in 0 until board.size) {
            for (col in 0 until board.size) {
                if (board.getCell(row, col).value == 0) return Pair(row, col)
            }
        }
        return null
    }

    private fun isValid(board: SudokuBoard, row: Int, col: Int, num: Int): Boolean {
        // Проверка строки
        for (x in 0 until board.size) {
            if (board.getCell(row, x).value == num) return false
        }

        // Проверка столбца
        for (x in 0 until board.size) {
            if (board.getCell(x, col).value == num) return false
        }

        // Проверка блока 3x3
        val startRow = row - row % 3
        val startCol = col - col % 3
        for (i in 0..2) {
            for (j in 0..2) {
                if (board.getCell(i + startRow, j + startCol).value == num) return false
            }
        }

        return true
    }

    private fun removeCells(board: SudokuBoard, filledCells: Int) {
        val cells = mutableListOf<Pair<Int, Int>>()
        for (row in 0 until board.size) {
            for (col in 0 until board.size) {
                cells.add(Pair(row, col))
            }
        }
        cells.shuffle()

        for (i in 0 until (81 - filledCells)) {
            val (row, col) = cells[i]
            board.getCell(row, col).value = 0
        }
    }
} 