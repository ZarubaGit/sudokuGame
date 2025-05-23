package com.example.sudokugame.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign

@Composable
fun TimerView(time: String) {
    Text(
        text = time,
        textAlign = TextAlign.Center
    )
} 