package com.hninakari.saletracker.core

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SyncTrigger {

    private const val tag = "SyncTrigger"

    private val scope = CoroutineScope(Dispatchers.IO)

    private var uploadJob: Job? = null

    private var uploader: (suspend () -> Unit)? = null

    @Volatile
    private var suppress = false

    fun initialize(
        upload: suspend () -> Unit
    ) {
        uploader = upload

        Log.d(
            tag,
            "✅ SyncTrigger initialized"
        )
    }

    fun triggerUpload() {

        if (suppress) {
            Log.d(
                tag,
                "⏭️ Upload suppressed"
            )
            return
        }

        uploadJob?.cancel()

        uploadJob = scope.launch {

            /*
             * Small delay so several quick local changes
             * don't cause many uploads.
             */
            delay(300)

            try {

                Log.d(
                    tag,
                    "📤 Triggering upload..."
                )

                uploader?.invoke()

                Log.d(
                    tag,
                    "✅ Triggered upload complete"
                )

            } catch (e: Exception) {

                Log.e(
                    tag,
                    "❌ Triggered upload failed",
                    e
                )
            }
        }
    }

    fun setSuppressed(value: Boolean) {

        suppress = value

        Log.d(
            tag,
            "Upload suppression = $value"
        )
    }
}
