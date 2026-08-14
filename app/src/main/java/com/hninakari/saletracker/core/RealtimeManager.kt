package com.hninakari.saletracker.core

import android.content.Context
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RealtimeManager(
    private val context: Context,
    private val supabaseClient: SupabaseClient,
    private val syncManager: SyncManager
) {

    private val tag = "RealtimeManager"
    private val scope = CoroutineScope(Dispatchers.IO)

    private var job: Job? = null
    private var channel: RealtimeChannel? = null

    private fun getUserId(): String {
        return UserPreferences.getInstance(context).getUserId()
    }

    fun startListening() {
        stopListening()

        val userId = getUserId()

        Log.d(tag, "========================================")
        Log.d(tag, "🚀 STARTING REALTIME")
        Log.d(tag, "User ID: $userId")

        job = scope.launch {
            try {
                channel = supabaseClient.channel("sync-$userId")
                val realtimeChannel = channel!!

                Log.d(tag, "Channel created: sync-$userId")

                // Listen for database changes
                launch {
                    realtimeChannel
                        .postgresChangeFlow<PostgresAction>(schema = "public") {
                            table = "sync_data"
                        }
                        .collectLatest { action ->

                            Log.d(tag, "🔥 EVENT: ${action::class.simpleName}")

                            val record = when (action) {
                                is PostgresAction.Insert -> action.record
                                is PostgresAction.Update -> action.record
                                else -> null
                            }

                            val eventUserId = record
                                ?.get("user_id")
                                ?.toString()
                                ?.trim('"')

                            Log.d(tag, "Event user: $eventUserId")

                            if (eventUserId == userId) {
                                Log.d(tag, "📥 DOWNLOADING LATEST DATA")

                                val result = syncManager.downloadData()

                                if (result.isSuccess) {
                                    Log.d(tag, "✅ DOWNLOAD SUCCESS")
                                } else {
                                    Log.e(
                                        tag,
                                        "❌ DOWNLOAD FAILED",
                                        result.exceptionOrNull()
                                    )
                                }
                            } else {
                                Log.d(tag, "Ignoring event")
                            }
                        }
                }

                // Subscribe after registering the listener
                realtimeChannel.subscribe()
                Log.d(tag, "✅ SUBSCRIBED")

            } catch (e: Exception) {
                Log.e(tag, "❌ REALTIME ERROR", e)
            }
        }
    }

    fun stopListening() {
        Log.d(tag, "⏹️ STOPPING REALTIME")

        job?.cancel()
        job = null

        scope.launch {
            try {
                channel?.unsubscribe()
                Log.d(tag, "✅ CHANNEL UNSUBSCRIBED")
            } catch (_: Exception) {
            }
            channel = null
        }
    }
}
