package com.techquote.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techquote.app.domain.settings.BusinessProfile
import com.techquote.app.domain.settings.BusinessProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val businessProfileRepository: BusinessProfileRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(SettingsUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            businessProfileRepository.profile.collect { profile ->
                mutableUiState.update {
                    it.copy(
                        displayName = profile.displayName,
                        phone = profile.phone,
                        email = profile.email,
                        address = profile.address,
                    )
                }
            }
        }
    }

    fun onDisplayNameChange(value: String) = update { copy(displayName = value, feedbackMessage = null, errorMessage = null) }

    fun onPhoneChange(value: String) = update { copy(phone = value, feedbackMessage = null, errorMessage = null) }

    fun onEmailChange(value: String) = update { copy(email = value, feedbackMessage = null, errorMessage = null) }

    fun onAddressChange(value: String) = update { copy(address = value, feedbackMessage = null, errorMessage = null) }

    fun saveBusinessProfile() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isSaving = true, feedbackMessage = null, errorMessage = null) }
            try {
                val current = mutableUiState.value
                businessProfileRepository.save(
                    BusinessProfile(
                        displayName = current.displayName,
                        phone = current.phone,
                        email = current.email,
                        address = current.address,
                    ),
                )
                mutableUiState.update {
                    it.copy(
                        isSaving = false,
                        feedbackMessage = "Datos del emisor guardados.",
                    )
                }
            } catch (_: Exception) {
                mutableUiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "No se pudieron guardar los datos del emisor.",
                    )
                }
            }
        }
    }

    fun clearMessages() {
        mutableUiState.update { it.copy(feedbackMessage = null, errorMessage = null) }
    }

    private fun update(transform: SettingsUiState.() -> SettingsUiState) {
        mutableUiState.update { it.transform() }
    }
}
