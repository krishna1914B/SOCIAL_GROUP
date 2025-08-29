package com.socialapp.android.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.socialapp.android.data.model.User
import com.socialapp.android.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCurrentUser()
        loadAllUsers()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = userRepository.getCurrentUserId()
                if (userId.isNotEmpty()) {
                    val user = userRepository.getUser(userId)
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val users = userRepository.getAllUsers()
                _allUsers.value = users
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveUser(name: String, dateOfBirth: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val user = User(
                    id = userId,
                    name = name,
                    dateOfBirth = dateOfBirth
                )
                val success = userRepository.createUser(user)
                if (success) {
                    _currentUser.value = user
                } else {
                    _error.value = "Failed to save user"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}