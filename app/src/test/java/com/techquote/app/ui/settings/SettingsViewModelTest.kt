package com.techquote.app.ui.settings

import com.techquote.app.domain.settings.BusinessProfile
import com.techquote.app.domain.settings.BusinessProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsEditsAndSavesBusinessProfile() = runTest(dispatcher) {
        val repository = FakeBusinessProfileRepository(
            BusinessProfile(displayName = "Técnico Demo", phone = "111", email = "", address = ""),
        )
        val viewModel = SettingsViewModel(repository)
        advanceUntilIdle()

        viewModel.onDisplayNameChange("TechQuote Servicios")
        viewModel.onPhoneChange("+54 11 5555-0101")
        viewModel.onEmailChange("presupuestos@example.com")
        viewModel.onAddressChange("Calle Demo 123")
        viewModel.saveBusinessProfile()
        advanceUntilIdle()

        assertEquals("TechQuote Servicios", repository.savedProfile.displayName)
        assertEquals("+54 11 5555-0101", repository.savedProfile.phone)
        assertEquals("presupuestos@example.com", repository.savedProfile.email)
        assertEquals("Calle Demo 123", repository.savedProfile.address)
        assertEquals("Datos del emisor guardados.", viewModel.uiState.value.feedbackMessage)
    }
}

private class FakeBusinessProfileRepository(
    initialProfile: BusinessProfile,
) : BusinessProfileRepository {
    override val profile = MutableStateFlow(initialProfile)
    var savedProfile = initialProfile

    override suspend fun save(profile: BusinessProfile) {
        savedProfile = profile
        this.profile.value = profile
    }
}
