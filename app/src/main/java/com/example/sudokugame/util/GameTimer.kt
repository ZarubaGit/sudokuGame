package com.example.sudokugame.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class GameTimer {
    private val _time = MutableStateFlow(0L)
    val time: StateFlow<Long> = _time

    private var isRunning = false
    private val scope = CoroutineScope(Dispatchers.Main)

    fun start() {
        if (isRunning) return
        isRunning = true
        scope.launch {
            while (isRunning) {
                delay(1000)
                _time.value += 1
            }
        }
    }

    fun stop() {
        isRunning = false
    }

    fun reset() {
        stop()
        _time.value = 0
    }
} 