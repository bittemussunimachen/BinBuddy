package com.example.binbuddy.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Helper class to collect Flow values from Java code.
 */
class FlowCollector<T>(
    private val flow: Flow<T>,
    private val onValue: (T) -> Unit,
    private val onError: ((Throwable) -> Unit)? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    fun start() {
        scope.launch {
            try {
                flow.collect { value ->
                    onValue(value)
                }
            } catch (e: Throwable) {
                // Ignore coroutine cancellation; surface only real errors
                if (e is CancellationException) return@launch
                onError?.invoke(e)
            }
        }
    }
    
    fun cancel() {
        scope.cancel()
    }
}
