package com.example.sudokugame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sudokugame.viewmodel.SudokuViewModel
import com.example.sudokugame.data.Difficulty
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import com.example.sudokugame.R
import com.example.sudokugame.model.SudokuBoard

@Composable
fun SudokuScreen(viewModel: SudokuViewModel = viewModel()) {
    val board by viewModel.board.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val hintsRemaining by viewModel.hintsRemaining.collectAsState()
    val isGameOver by viewModel.isGameOver.collectAsState()
    val time by viewModel.time.collectAsState()
    val status by viewModel.statusMessage.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDifficultyDialog by remember { mutableStateOf(false) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var showWinDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isGameOver) {
        if (isGameOver) showWinDialog = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF0FB))
            .windowInsetsPadding(WindowInsets.statusBars),
        contentAlignment = Alignment.Center
    ) {
        val minSide = minOf(maxWidth, maxHeight)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TopBar
            Text(
                text = stringResource(R.string.sudoku_title),
                fontSize = dimensionResource(R.dimen.sudoku_cell_font).value.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color.Red)
                Text(time.formatAsTime(), modifier = Modifier.padding(start = 4.dp, end = 16.dp))
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFD600))
                Text(stringResource(R.string.hints_left, hintsRemaining), modifier = Modifier.padding(start = 4.dp, end = 16.dp))
                AnimatedButton(
                    onClick = { showDifficultyDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9)),
                    contentPadding = PaddingValues(
                        horizontal = dimensionResource(R.dimen.button_padding_h),
                        vertical = dimensionResource(R.dimen.button_padding_v)
                    )
                ) {
                    Text(selectedDifficulty.name, fontSize = 14.sp)
                }
            }
            // DifficultySelector
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Difficulty.values().forEach { diff ->
                    AnimatedButton(
                        onClick = {
                            selectedDifficulty = diff
                            viewModel.startNewGame(diff)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (diff == selectedDifficulty) Color(0xFF90CAF9) else Color.LightGray
                        ),
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.button_margin))
                    ) {
                        Text(stringResource(
                            when (diff) {
                                Difficulty.EASY -> R.string.easy
                                Difficulty.MEDIUM -> R.string.medium
                                Difficulty.HARD -> R.string.hard
                            }
                        ))
                    }
                }
            }
            // ActionButtonsRow
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                AnimatedButton(
                    onClick = { viewModel.startNewGame(selectedDifficulty) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB39DDB)),
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.button_margin))
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Text(" "+stringResource(R.string.new_game))
                }
                AnimatedButton(
                    onClick = { viewModel.startTimer() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.button_margin))
                ) {
                    Text(stringResource(R.string.start))
                }
                AnimatedButton(
                    onClick = { viewModel.resetBoard() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.button_margin))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Text(" "+stringResource(R.string.reset))
                }
            }
            // Status message с анимацией
            AnimatedVisibility(visible = status.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
                Text(status, fontSize = dimensionResource(R.dimen.status_text_size).value.sp, color = if (isGameOver) Color(0xFF388E3C) else Color.Red, modifier = Modifier.padding(8.dp))
            }
            // Hint & Check buttons
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                AnimatedButton(
                    onClick = { viewModel.useHint() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600)),
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.button_margin))
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.Black)
                    Text(" "+stringResource(R.string.hint)+" ($hintsRemaining)", color = Color.Black)
                }
                AnimatedButton(
                    onClick = { viewModel.checkSolution() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.button_margin))
                ) {
                    Text(stringResource(R.string.check), color = Color.White)
                }
            }
            // Undo
            AnimatedButton(
                onClick = { viewModel.undo() },
                enabled = !isGameOver,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9)),
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.button_margin))
            ) { Text(stringResource(R.string.undo)) }
            // Sudoku Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .aspectRatio(1f)
            ) {
                SudokuBoardView(
                    board = board,
                    selectedCell = selectedCell,
                    onCellClick = { row, col -> viewModel.selectCell(row, col) }
                )
            }
            // NumberPad
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(4.dp)
            ) {
                NumberPad(
                    onNumberClick = { viewModel.setCellValue(it) },
                    onDelete = { viewModel.deleteCellValue() },
                    enabled = !isGameOver && selectedCell != null
                )
            }
            // Instructions
            Column(modifier = Modifier.padding(8.dp)) {
                Text("🎯 "+stringResource(R.string.select_cell_instruction), fontSize = dimensionResource(R.dimen.instruction_text_size).value.sp)
                Text("💡 "+stringResource(R.string.use_hints_instruction), fontSize = dimensionResource(R.dimen.instruction_text_size).value.sp, color = Color(0xFFFFD600))
                Text("✅ "+stringResource(R.string.check_instruction), fontSize = dimensionResource(R.dimen.instruction_text_size).value.sp, color = Color(0xFF388E3C))
            }
        }
    }
    if (showDifficultyDialog) {
        DifficultyDialog(
            onSelect = { difficulty ->
                selectedDifficulty = difficulty
                viewModel.startNewGame(difficulty)
                showDifficultyDialog = false
            },
            onDismiss = { showDifficultyDialog = false }
        )
    }
    // Диалог победы
    if (showWinDialog) {
        AlertDialog(
            onDismissRequest = { showWinDialog = false },
            title = { Text(stringResource(R.string.victory)) },
            text = { Text(stringResource(R.string.win_message)) },
            confirmButton = {
                Button(onClick = { showWinDialog = false }) { Text(stringResource(R.string.confirm)) }
            }
        )
    }
    // Диалог подтверждения сброса
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.reset_confirm_title)) },
            text = { Text(stringResource(R.string.reset_confirm_text)) },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetBoard()
                    showResetDialog = false
                }) { Text(stringResource(R.string.yes)) }
            },
            dismissButton = {
                Button(onClick = { showResetDialog = false }) { Text(stringResource(R.string.no)) }
            }
        )
    }
    // Сохранение прогресса при каждом изменении
    LaunchedEffect(board, time, hintsRemaining, selectedDifficulty) {
        scope.launch {
            saveProgress(context, board, time, hintsRemaining, selectedDifficulty)
        }
    }
    // Загрузка прогресса при старте
    LaunchedEffect(Unit) {
        scope.launch {
            val progress = loadProgress(context)
            if (progress != null) {
                viewModel.loadProgress(progress)
            }
        }
    }
}

// Вспомогательная функция для форматирования времени
private fun Long.formatAsTime(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "%02d:%02d".format(minutes, seconds)
}

// Функции сохранения/загрузки прогресса
private fun saveProgress(context: Context, board: SudokuBoard, time: Long, hints: Int, difficulty: Difficulty) {
    val prefs = context.getSharedPreferences("sudoku_prefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    // Сохраняем значения клеток
    for (row in 0 until 9) {
        for (col in 0 until 9) {
            editor.putInt("cell_${row}_${col}", board.getCell(row, col).value)
        }
    }
    editor.putLong("time", time)
    editor.putInt("hints", hints)
    editor.putString("difficulty", difficulty.name)
    editor.apply()
}

private fun loadProgress(context: Context): ProgressData? {
    val prefs = context.getSharedPreferences("sudoku_prefs", Context.MODE_PRIVATE)
    val board = SudokuBoard()
    var hasData = false
    for (row in 0 until 9) {
        for (col in 0 until 9) {
            val value = prefs.getInt("cell_${row}_${col}", -1)
            if (value != -1) {
                board.getCell(row, col).value = value
                hasData = true
            }
        }
    }
    if (!hasData) return null
    val time = prefs.getLong("time", 0L)
    val hints = prefs.getInt("hints", 3)
    val difficulty = Difficulty.valueOf(prefs.getString("difficulty", "MEDIUM") ?: "MEDIUM")
    return ProgressData(board, time, hints, difficulty)
}

data class ProgressData(val board: SudokuBoard, val time: Long, val hints: Int, val difficulty: Difficulty)

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 1.08f else 1f)
    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }
    Button(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        content = content
    )
} 