package com.focuszone.util

import androidx.appcompat.app.AppCompatActivity

class PermissionQueue(private val activity: AppCompatActivity) {
    private val queue: MutableList<() -> Unit> = mutableListOf()
    private var isProcessing = false

    fun add(permissionCheck: () -> Unit) {
        queue.add(permissionCheck)
        processNext()
    }

    private fun processNext() {
        if (isProcessing || queue.isEmpty()) return

        isProcessing = true
        val nextTask = queue.removeAt(0)
        nextTask.invoke()
    }

    fun onTaskComplete() {
        isProcessing = false
        processNext()
    }
}
