package com.example.sudokugame.model

class SudokuBoard(val size: Int = 9) {
    val cells: Array<Array<SudokuCell>> = Array(size) { row ->
        Array(size) { col -> SudokuCell(row, col) }
    }

    fun getCell(row: Int, col: Int): SudokuCell = cells[row][col]

    fun isValid(): Boolean {
        // Проверка строк
        for (row in 0 until size) {
            val rowValues = mutableSetOf<Int>()
            for (col in 0 until size) {
                val value = cells[row][col].value
                if (value != 0 && !rowValues.add(value)) return false
            }
        }

        // Проверка столбцов
        for (col in 0 until size) {
            val colValues = mutableSetOf<Int>()
            for (row in 0 until size) {
                val value = cells[row][col].value
                if (value != 0 && !colValues.add(value)) return false
            }
        }

        // Проверка блоков 3x3
        for (blockRow in 0 until 3) {
            for (blockCol in 0 until 3) {
                val blockValues = mutableSetOf<Int>()
                for (i in 0..2) {
                    for (j in 0..2) {
                        val row = blockRow * 3 + i
                        val col = blockCol * 3 + j
                        val value = cells[row][col].value
                        if (value != 0 && !blockValues.add(value)) return false
                    }
                }
            }
        }

        return true
    }

    fun clearErrors() {
        for (row in 0 until size) {
            for (col in 0 until size) {
                cells[row][col].isError = false
            }
        }
    }

    fun copy(): SudokuBoard {
        val newBoard = SudokuBoard(size)
        for (row in 0 until size) {
            for (col in 0 until size) {
                val cell = cells[row][col]
                newBoard.cells[row][col] = cell.copy()
            }
        }
        return newBoard
    }
} 