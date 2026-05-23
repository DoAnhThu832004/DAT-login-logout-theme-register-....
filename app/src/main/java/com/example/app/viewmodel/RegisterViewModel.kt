package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.repository.SongRepository
import com.example.app.model.repository.UserRepository
import com.example.app.model.request.UserCreationRequest
import com.example.app.model.response.Genre
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: UserRepository,
    private val songRepository: SongRepository? = null
) : ViewModel() {
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    init {
        loadGenres()
    }

    private fun loadGenres() {
        if (songRepository == null) {
            _registerUiState.update { it.copy(
                isLoadingGenres = false,
                genreLoadError = "Chưa kết nối API"
            ) }
            return
        }
        
        _registerUiState.update { it.copy(isLoadingGenres = true, genreLoadError = null) }
        
        viewModelScope.launch {
            try {
                val response = songRepository.getGenres()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        _registerUiState.update { it.copy(
                            availableGenres = body.result,
                            isLoadingGenres = false,
                            genreLoadError = null
                        ) }
                    } else {
                        _registerUiState.update { it.copy(
                            isLoadingGenres = false,
                            genreLoadError = "Không tải được danh sách thể loại"
                        ) }
                    }
                } else {
                    _registerUiState.update { it.copy(
                        isLoadingGenres = false,
                        genreLoadError = "Lỗi máy chủ (${response.code()})"
                    ) }
                }
            } catch (e: Exception) {
                _registerUiState.update { it.copy(
                    isLoadingGenres = false,
                    genreLoadError = "Lỗi mạng: ${e.message}"
                ) }
            }
        }
    }

    fun updateUsernameInput(input: String) {
       _registerUiState.update { it.copy(usernameInput = input) }
    }

    fun updatePasswordInput(input: String) {
        _registerUiState.update { it.copy(passwordInput = input) }
    }

    fun updateFirstNameInput(input: String) {
        _registerUiState.update { it.copy(firstNameInput = input) }
    }

    fun updateLastNameInput(input: String) {
        _registerUiState.update { it.copy(lastNameInput = input) }
    }

    fun updateDobInput(input: String) {
        _registerUiState.update { it.copy(dobInput = input) }
    }

    /** Chuyển sang bước chọn genre (Bước 2) */
    fun proceedToGenreSelection() {
        _registerUiState.update { it.copy(isOnGenreStep = true) }
    }

    /** Quay lại bước nhập thông tin (Bước 1) */
    fun backToInfoStep() {
        _registerUiState.update { it.copy(isOnGenreStep = false) }
    }

    /** Toggle chọn/bỏ chọn một genre */
    fun toggleGenre(genreId: String) {
        _registerUiState.update { state ->
            val current = state.selectedGenreIds.toMutableList()
            if (current.contains(genreId)) {
                current.remove(genreId)
            } else {
                current.add(genreId)
            }
            state.copy(selectedGenreIds = current)
        }
    }

    fun register() {
        val state = _registerUiState.value
        viewModelScope.launch {
            _registerUiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = repository.createUser(
                    UserCreationRequest(
                        username = state.usernameInput,
                        password = state.passwordInput,
                        firstName = state.firstNameInput,
                        lastName = state.lastNameInput,
                        dob = state.dobInput,
                        preferredGenreIds = state.selectedGenreIds.ifEmpty { null }
                    )
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.code == 1000) {
                        _registerUiState.update { it.copy(
                            isLoading = false,
                            isSuccessful = true,
                            error = "Đăng ký thành công"
                        ) }
                    } else {
                        resetRegisterUiState(response.message())
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    resetRegisterUiState(apiErr?.message ?: "Đăng ký thất bại")
                }
            } catch (e: Exception) {
                resetRegisterUiState("Lỗi: ${e.message}")
            }
        }
    }

    fun resetRegisterUiState(message: String) {
        viewModelScope.launch {
            _registerUiState.update { it.copy(isLoading = true, error = message) }
            delay(1500)
            _registerUiState.update { it.copy(isLoading = false, error = message) }
        }
    }

    data class RegisterUiState(
        val isLoading: Boolean = false,
        val isSuccessful: Boolean = false,
        var usernameInput: String = "",
        val passwordInput: String = "",
        val firstNameInput: String = "",
        val lastNameInput: String = "",
        val dobInput: String = "",
        val error: String? = null,
        // Genre selection
        val isOnGenreStep: Boolean = false,
        val isLoadingGenres: Boolean = true,
        val genreLoadError: String? = null,
        val availableGenres: List<Genre> = emptyList(),
        val selectedGenreIds: List<String> = emptyList()
    )
}