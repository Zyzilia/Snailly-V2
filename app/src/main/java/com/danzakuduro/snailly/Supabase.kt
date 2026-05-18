package com.danzakuduro.snailly

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://ugzywnkfgvjioszaeqbb.supabase.co",
    supabaseKey = "sb_publishable_pAGbrgM1omw2S1ag86Dlsg_c7x5uNl2"
) {
    install(Postgrest)
    install(Auth)
}
