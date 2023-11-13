package com.devesh.learning.slack

import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object ViewExt{
    fun View.setDebounceClickListener(
        duration: Duration = 300.milliseconds,
        action: (View) -> Unit
    ) {
        this.setDebounceClickListener(duration.inWholeMilliseconds, action)
    }
    private fun View.setDebounceClickListener(
        timeInMillis: Long,
        action: (View) -> Unit
    ) {
        var debounceJob = AtomicReference<Job?>(null)
        val viewScope = findViewTreeLifecycleOwner()?.lifecycleScope ?: CoroutineScope(Dispatchers.Main + SupervisorJob())
        setOnClickListener{ view ->
            debounceJob.get()?.cancel()
            debounceJob.set(viewScope.launch {
                delay(timeInMillis)
                action(view)
            })
        }

        addOnAttachStateChangeListener(object:View.OnAttachStateChangeListener{
            override fun onViewAttachedToWindow(p0: View) {
            }

            override fun onViewDetachedFromWindow(p0: View) {
                debounceJob.get()?.cancel()
            }
        })
    }
}