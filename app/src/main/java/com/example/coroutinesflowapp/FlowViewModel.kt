package com.example.coroutinesflowapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Educational ViewModel demonstrating Cold Streams and Hot Streams in Kotlin Flow
 *
 * KEY CONCEPTS:
 * 1. COLD STREAM (Regular Flow): Created on demand, starts collection from scratch each time
 * 2. HOT STREAM (StateFlow/SharedFlow): Always running, new subscribers get most recent or missed values
 */
class FlowViewModel : ViewModel() {

    // ============ COLD STREAM EXAMPLES ============
    // Cold streams are created and started fresh for each collector
    // They emit values from the beginning for each new subscriber

    /**
     * Simple Cold Stream - Emits incrementing numbers
     * Each subscriber gets values from 1 to 5
     * Collector 2 starts from 1 (not from where Collector 1 started)
     */
    fun createSimpleColdFlow(): Flow<Int> = flow {
        repeat(5) { index ->
            emit(index + 1)
            delay(1000)
        }
    }

    /**
     * Data fetching Cold Stream - Simulates API call
     * Each new subscription will perform the data fetch again
     */
    fun fetchDataColdFlow(): Flow<String> = flow {
        emit("Fetching data...")
        delay(2000) // Simulate network delay
        emit("Data fetched successfully!")
    }

    // ============ HOT STREAM EXAMPLES ============
    // Hot streams are always active and emit values regardless of collectors
    // New subscribers don't start from the beginning

    /**
     * StateFlow - A hot stream that holds state
     * - Always has a current value
     * - New subscribers immediately receive the current state
     * - Replays the latest value to new collectors
     * - Only keeps the most recent value in memory
     */
    private val _countStateFlow = MutableStateFlow(0)
    val countStateFlow: StateFlow<Int> = _countStateFlow.asStateFlow()

    /**
     * SharedFlow - A hot stream for broadcasting events
     * - Can have multiple collectors
     * - New collectors don't receive past events (unless configured with replay)
     * - Useful for event broadcasting
     */
    private val _eventSharedFlow = MutableSharedFlow<String>()
    val eventSharedFlow = _eventSharedFlow.asSharedFlow()

    /**
     * SharedFlow with replay - Replays past events to new subscribers
     * - Keeps last N emissions in a buffer
     * - New collectors receive the buffered values
     */
    private val _replayEventFlow = MutableSharedFlow<String>(replay = 3)
    val replayEventFlow = _replayEventFlow.asSharedFlow()

    // ============ DEMONSTRATING HOT STREAM BEHAVIOR ============

    /**
     * Increments the count in StateFlow
     * All observers immediately see the new value
     */
    fun incrementCount() {
        viewModelScope.launch {
            _countStateFlow.value = _countStateFlow.value + 1
        }
    }

    /**
     * Emits an event to SharedFlow
     * Only active collectors receive it
     */
    fun emitEvent(message: String) {
        viewModelScope.launch {
            _eventSharedFlow.emit(message)
        }
    }

    /**
     * Emits an event to replay SharedFlow
     * New collectors will receive the last 3 events
     */
    fun emitReplayEvent(message: String) {
        viewModelScope.launch {
            _replayEventFlow.emit(message)
        }
    }

    /**
     * Automatically emits events to StateFlow
     * Demonstrates continuous hot stream behavior
     */
    fun startAutoEmit() {
        viewModelScope.launch {
            repeat(10) {
                _countStateFlow.value = it
                delay(1000)
            }
        }
    }

    /**
     * Educational cold flow showing request-response pattern
     * Demonstrates why cold flows are better for one-time operations
     */
    fun fetchUserDataColdFlow(userId: String): Flow<String> = flow {
        emit("Loading user $userId...")
        delay(1500)
        emit("User $userId data: Name=John, Age=30")
    }

    /**
     * Demonstrates the difference between cold and hot streams
     */
    fun demonstrateFlowDifferences(): Flow<String> = flow {
        emit("""
            ========== COLD vs HOT STREAMS ==========
            
            COLD STREAM (Regular Flow):
            • Created when someone subscribes
            • Each subscriber gets independent execution
            • Starts from the beginning for each subscriber
            • Best for: Data fetching, one-time operations
            • Example: fetchDataColdFlow()
            
            HOT STREAM (StateFlow/SharedFlow):
            • Always active, regardless of subscribers
            • Shares single execution among all subscribers
            • New subscriber joins ongoing stream
            • Best for: State management, event broadcasting
            • Examples: countStateFlow, eventSharedFlow
            
            ========== PRACTICAL EXAMPLES ==========
            
            COLD STREAM USE CASE:
            fun getWeatherData(city: String): Flow<Weather> = flow {
                val data = fetchFromApi(city)
                emit(data)
            }
            Each call triggers a new API request
            
            HOT STREAM USE CASE:
            private val _userLocation = MutableStateFlow(Location.UNKNOWN)
            val userLocation: StateFlow<Location> = _userLocation.asStateFlow()
            Location updates are shared with all observers
        """.trimIndent())
    }
}

