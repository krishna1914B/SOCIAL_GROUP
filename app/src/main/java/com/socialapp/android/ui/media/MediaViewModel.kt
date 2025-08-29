package com.socialapp.android.ui.media

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.socialapp.android.data.model.Media
import com.socialapp.android.data.repository.MediaRepository
import com.socialapp.android.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaViewModel(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _mediaList = MutableStateFlow<List<Media>>(emptyList())
    val mediaList: StateFlow<List<Media>> = _mediaList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _uploadSuccess = MutableStateFlow(false)
    val uploadSuccess: StateFlow<Boolean> = _uploadSuccess.asStateFlow()

    init {
        loadMedia()
    }

    fun loadMedia() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val media = mediaRepository.getAllMedia()
                _mediaList.value = media
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadMedia(fileUri: Uri, isVideo: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _uploadSuccess.value = false
            try {
                // Get current user info
                val userId = userRepository.getCurrentUserId()
                val user = userRepository.getUser(userId)
                
                if (user != null) {
                    val mediaId = mediaRepository.uploadMedia(
                        fileUri = fileUri,
                        uploaderName = user.name,
                        uploaderId = userId,
                        isVideo = isVideo
                    )
                    
                    if (mediaId != null) {
                        _uploadSuccess.value = true
                        // Refresh media list
                        loadMedia()
                    } else {
                        _error.value = "Failed to upload media"
                    }
                } else {
                    _error.value = "User not found"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}