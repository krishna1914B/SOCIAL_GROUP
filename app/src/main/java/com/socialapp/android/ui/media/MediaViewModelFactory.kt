package com.socialapp.android.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.socialapp.android.data.repository.MediaRepository
import com.socialapp.android.data.repository.UserRepository

class MediaViewModelFactory(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MediaViewModel(mediaRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}