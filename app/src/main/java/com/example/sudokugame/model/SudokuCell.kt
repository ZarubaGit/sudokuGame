package com.example.sudokugame.model

data class SudokuCell(
    val row: Int,
    val col: Int,
    var value: Int = 0,
    val isFixed: Boolean = false,
    var isError: Boolean = false
) 