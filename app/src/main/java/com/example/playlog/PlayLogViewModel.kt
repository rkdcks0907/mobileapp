package com.example.playlog

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class PlayLogViewModel : ViewModel() {

    private val _logs = mutableStateListOf<MatchLog>()
    val logs = _logs

    fun add(log: MatchLog) {
        _logs.add(0, log)
    }

    fun delete(id: String) {
        val idx = _logs.indexOfFirst { it.id == id }
        if (idx >= 0) _logs.removeAt(idx)
    }

    fun stats(): Pair<Int, Int> {
        var win = 0
        var lose = 0
        for (log in _logs) {
            if (log.result == Result.WIN) win++ else lose++
        }
        return win to lose
    }

    fun mostFrequentFlow(): Flow? {
        if (_logs.isEmpty()) return null

        val counts = mutableMapOf<Flow, Int>()
        for (log in _logs) {
            counts[log.flow] = counts.getOrDefault(log.flow, 0) + 1
        }

        var best: Flow? = null
        var bestCount = -1
        for ((flow, c) in counts) {
            if (c > bestCount) {
                best = flow
                bestCount = c
            }
        }
        return best
    }

    fun mostFrequentMoodOnLose(): Mood? {
        val loseLogs = _logs.filter { it.result == Result.LOSE }
        if (loseLogs.isEmpty()) return null

        val counts = mutableMapOf<Mood, Int>()
        for (log in loseLogs) {
            counts[log.mood] = counts.getOrDefault(log.mood, 0) + 1
        }

        var best: Mood? = null
        var bestCount = -1
        for ((mood, c) in counts) {
            if (c > bestCount) {
                best = mood
                bestCount = c
            }
        }
        return best
    }
}
