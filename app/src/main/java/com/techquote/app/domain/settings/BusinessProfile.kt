package com.techquote.app.domain.settings

import kotlinx.coroutines.flow.Flow

data class BusinessProfile(
    val displayName: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
)

interface BusinessProfileRepository {
    val profile: Flow<BusinessProfile>

    suspend fun save(profile: BusinessProfile)
}
