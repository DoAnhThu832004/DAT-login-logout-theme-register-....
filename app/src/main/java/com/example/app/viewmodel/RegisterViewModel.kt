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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
                    genreLoadError = "Lỗi mạng: không thể tải thể loại"
                ) }
            }
        }
    }

    fun updateUsernameInput(input: String) {
        _registerUiState.update { it.copy(usernameInput = input, usernameError = null) }
    }

    fun updatePasswordInput(input: String) {
        _registerUiState.update { it.copy(passwordInput = input, passwordError = null) }
    }

    fun updateFirstNameInput(input: String) {
        _registerUiState.update { it.copy(firstNameInput = input, firstNameError = null) }
    }

    fun updateLastNameInput(input: String) {
        _registerUiState.update { it.copy(lastNameInput = input, lastNameError = null) }
    }

    fun updateDobInput(input: String) {
        _registerUiState.update { it.copy(dobInput = input, dobError = null) }
    }

    /** Validate rồi chuyển sang bước chọn genre (Bước 2) */
    fun proceedToGenreSelection() {
        val state = _registerUiState.value
        var usernameErr: String? = null
        var passwordErr: String? = null
        var firstNameErr: String? = null
        var lastNameErr: String? = null
        var dobErr: String? = null

        // --- Username ---
        when {
            state.usernameInput.isBlank() -> usernameErr = "Tên đăng nhập không được để trống"
            state.usernameInput.trim().length < 4 -> usernameErr = "Tên đăng nhập tối thiểu 4 ký tự"
            state.usernameInput.trim().length > 50 -> usernameErr = "Tên đăng nhập tối đa 50 ký tự"
        }

        // --- Password ---
        when {
            state.passwordInput.isBlank() -> passwordErr = "Mật khẩu không được để trống"
            state.passwordInput.length < 6 -> passwordErr = "Mật khẩu tối thiểu 6 ký tự"
            state.passwordInput.length > 100 -> passwordErr = "Mật khẩu tối đa 100 ký tự"
        }

        // --- firstName ---
        when {
            state.firstNameInput.isBlank() -> firstNameErr = "Họ không được để trống"
            state.firstNameInput.trim().length > 50 -> firstNameErr = "Họ tối đa 50 ký tự"
        }

        // --- lastName ---
        when {
            state.lastNameInput.isBlank() -> lastNameErr = "Tên không được để trống"
            state.lastNameInput.trim().length > 50 -> lastNameErr = "Tên tối đa 50 ký tự"
        }

        // --- DOB ---
        when {
            state.dobInput.isBlank() -> dobErr = "Ngày sinh không được để trống"
            else -> {
                try {
                    val dob = LocalDate.parse(state.dobInput.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val today = LocalDate.now()
                    val age = today.year - dob.year -
                        (if (today.dayOfYear < dob.dayOfYear) 1 else 0)
                    if (age < 10) dobErr = "Bạn phải ít nhất 10 tuổi"
                    if (dob.isAfter(today)) dobErr = "Ngày sinh không hợp lệ"
                } catch (e: DateTimeParseException) {
                    dobErr = "Định dạng ngày: yyyy-MM-dd (VD: 2000-01-31)"
                }
            }
        }

        // Nếu có lỗi thì cập nhật state và dừng
        if (listOf(usernameErr, passwordErr, firstNameErr, lastNameErr, dobErr).any { it != null }) {
            _registerUiState.update {
                it.copy(
                    usernameError = usernameErr,
                    passwordError = passwordErr,
                    firstNameError = firstNameErr,
                    lastNameError = lastNameErr,
                    dobError = dobErr
                )
            }
            return
        }

        // Hợp lệ → chuyển bước
        _registerUiState.update { it.copy(
            usernameError = null,
            passwordError = null,
            firstNameError = null,
            lastNameError = null,
            dobError = null,
            isOnGenreStep = true
        ) }
    }

    /** Quay lại bước nhập thông tin (Bước 1) */
    fun backToInfoStep() {
        _registerUiState.update { it.copy(isOnGenreStep = false, error = null) }
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
                        username = state.usernameInput.trim(),
                        password = state.passwordInput,
                        firstName = state.firstNameInput.trim(),
                        lastName = state.lastNameInput.trim(),
                        dob = state.dobInput.trim(),
                        preferredGenreIds = state.selectedGenreIds.ifEmpty { null }
                    )
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.code == 1000) {
                        // Telemetry: auth_register_success
                        com.example.app.analytics.AnalyticsHelper.logRegisterSuccess("email")
                        if (state.selectedGenreIds.isNotEmpty()) {
                            com.example.app.analytics.AnalyticsHelper.setPreferredGenre(state.selectedGenreIds.joinToString(","))
                        }

                        _registerUiState.update { it.copy(
                            isLoading = false,
                            isSuccessful = true,
                            error = null
                        ) }
                    } else {
                        _registerUiState.update { it.copy(
                            isLoading = false,
                            error = body?.message ?: "Đăng ký thất bại"
                        ) }
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    val msg = when (apiErr?.code) {
                        1002 -> "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác"
                        else -> apiErr?.message ?: "Đăng ký thất bại, vui lòng thử lại"
                    }
                    _registerUiState.update { it.copy(isLoading = false, error = msg) }
                }
            } catch (e: Exception) {
                _registerUiState.update { it.copy(
                    isLoading = false,
                    error = "Lỗi kết nối, vui lòng thử lại"
                ) }
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
        // Field-level errors
        val usernameError: String? = null,
        val passwordError: String? = null,
        val firstNameError: String? = null,
        val lastNameError: String? = null,
        val dobError: String? = null,
        // Genre selection
        val isOnGenreStep: Boolean = false,
        val isLoadingGenres: Boolean = true,
        val genreLoadError: String? = null,
        val availableGenres: List<Genre> = emptyList(),
        val selectedGenreIds: List<String> = emptyList()
    )
}