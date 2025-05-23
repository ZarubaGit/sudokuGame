package com.example.sudokugame.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudokugame.model.SudokuBoard
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun SudokuBoardView(
    board: SudokuBoard,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (row: Int, col: Int) -> Unit
) {
    val selectedValue = selectedCell?.let { board.getCell(it.first, it.second).value }?.takeIf { it != 0 }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .fillMaxWidth()
            .drawWithContent {
                drawContent() // сначала клетки
                val cellSize = size.width / 9f
                // Вертикальные линии
                for (i in 1 until 9) {
                    val stroke = when {
                        i % 3 == 0 -> 5.dp.toPx()
                        else -> 2.dp.toPx()
                    }
                    drawLine(
                        color = Color.Black,
                        start = Offset(i * cellSize, 0f),
                        end = Offset(i * cellSize, size.height),
                        strokeWidth = stroke
                    )
                }
                // Горизонтальные линии
                for (i in 1 until 9) {
                    val stroke = when {
                        i % 3 == 0 -> 5.dp.toPx()
                        else -> 2.dp.toPx()
                    }
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, i * cellSize),
                        end = Offset(size.width, i * cellSize),
                        strokeWidth = stroke
                    )
                }
                // Внешний квадрат
                drawRect(
                    color = Color.Black,
                    size = size,
                    style = Stroke(width = 6.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            for (row in 0 until board.size) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    for (col in 0 until board.size) {
                        val cell = board.getCell(row, col)
                        val isSelected = selectedCell == Pair(row, col)
                        val isRelated = selectedCell?.let { (selRow, selCol) ->
                            selRow == row || selCol == col || (selRow / 3 == row / 3 && selCol / 3 == col / 3)
                        } ?: false
                        val isSameValue = selectedValue != null && cell.value == selectedValue && cell.value != 0

                        val bgColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> Color.LightGray
                                cell.isFixed -> Color(0xFFE0E0E0)
                                isRelated -> Color(0xFFADD8E6)
                                cell.isError -> Color.Red
                                isSameValue -> Color(0xFFB3E5FC)
                                else -> Color.White
                            },
                            animationSpec = tween(300)
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.08f else 1f,
                            animationSpec = tween(200)
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .background(bgColor)
                                .let { mod ->
                                    if (!cell.isFixed) mod.clickable { onCellClick(row, col) } else mod
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell.value != 0) {
                                Text(
                                    text = cell.value.toString(),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (cell.isFixed) Color.Black else Color(0xFF1976D2),
                                    fontWeight = if (cell.isFixed) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 