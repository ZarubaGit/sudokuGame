package com.example.sudokugame.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState

@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onDelete: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (number in 1..9) {
            AnimatedNumber(
                number = number.toString(),
                onClick = { onNumberClick(number) },
                enabled = enabled
            )
        }
        AnimatedNumber(
            number = "⌫",
            onClick = onDelete,
            enabled = enabled
        )
    }
}

@Composable
fun AnimatedNumber(
    number: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 1.2f else 1f)

    // Реагируем на изменение pressed
    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }

    Text(
        text = number,
        fontSize = 28.sp,
        color = if (enabled) Color.Black else Color.Gray,
        modifier = Modifier
            .padding(8.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(enabled = enabled) {
                pressed = true
                onClick()
            }
    )
} 