package com.hninakari.saletracker.core

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.ktor.client.engine.okhttp.OkHttp

object SupabaseClient {
    val instance: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SupabaseConfig.URL,
            supabaseKey = SupabaseConfig.API_KEY
        ) {
            httpEngine = OkHttp.create()
            install(Postgrest)
            install(Realtime)
        }
    }

    fun from(table: String) = instance.postgrest[table]
    fun realtime() = instance.realtime
}
