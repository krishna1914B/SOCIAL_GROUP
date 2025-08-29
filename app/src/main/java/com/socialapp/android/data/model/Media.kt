package com.socialapp.android.data.model

data class Media(
    val id: String = "",
    val url: String = "",
    val uploaderName: String = "",
    val uploaderId: String = "",
    val timestamp: Long = 0L,
    val isVideo: Boolean = false
)