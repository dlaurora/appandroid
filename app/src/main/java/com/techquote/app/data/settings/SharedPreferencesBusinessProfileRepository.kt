package com.techquote.app.data.settings

import android.content.Context
import androidx.core.content.edit
import com.techquote.app.domain.settings.BusinessProfile
import com.techquote.app.domain.settings.BusinessProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesBusinessProfileRepository @Inject constructor(
    @ApplicationContext context: Context,
) : BusinessProfileRepository {
    private val preferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)
    private val mutableProfile = MutableStateFlow(readProfile())

    override val profile: StateFlow<BusinessProfile> = mutableProfile

    override suspend fun save(profile: BusinessProfile) {
        val cleanProfile = profile.clean()
        preferences.edit {
            putString(KeyDisplayName, cleanProfile.displayName)
            putString(KeyPhone, cleanProfile.phone)
            putString(KeyEmail, cleanProfile.email)
            putString(KeyAddress, cleanProfile.address)
        }
        mutableProfile.value = cleanProfile
    }

    private fun readProfile(): BusinessProfile {
        return BusinessProfile(
            displayName = preferences.getString(KeyDisplayName, "").orEmpty(),
            phone = preferences.getString(KeyPhone, "").orEmpty(),
            email = preferences.getString(KeyEmail, "").orEmpty(),
            address = preferences.getString(KeyAddress, "").orEmpty(),
        )
    }

    private fun BusinessProfile.clean(): BusinessProfile {
        return copy(
            displayName = displayName.trim(),
            phone = phone.trim(),
            email = email.trim(),
            address = address.trim(),
        )
    }

    companion object {
        private const val PreferencesName = "techquote_business_profile"
        private const val KeyDisplayName = "display_name"
        private const val KeyPhone = "phone"
        private const val KeyEmail = "email"
        private const val KeyAddress = "address"
    }
}
