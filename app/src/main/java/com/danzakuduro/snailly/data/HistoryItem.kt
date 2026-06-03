package com.danzakuduro.snailly.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    val id: Long? = null,
    val url: String,
    val title: String,
    val timestamp: String,
    @SerialName("is_safe")
    val isSafe: Boolean,
    @SerialName("user_id")
    val userId: String? = null
)
