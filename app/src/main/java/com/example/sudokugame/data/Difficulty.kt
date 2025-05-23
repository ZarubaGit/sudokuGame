package com.example.sudokugame.data

enum class Difficulty(val filledCells: Int, val hints: Int) {
    EASY(45, 5),
    MEDIUM(35, 3),
    HARD(25, 1)
} 