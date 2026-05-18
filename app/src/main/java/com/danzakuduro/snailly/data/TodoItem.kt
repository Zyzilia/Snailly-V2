package com.danzakuduro.snailly.data

import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(
    val id: Long,
    val name: String,
    val isCompleted: Boolean = false
)
