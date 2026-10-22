package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.BusinessProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BusinessSettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _businessProfile = MutableStateFlow(loadProfile())
    val businessProfile: StateFlow<BusinessProfile> = _businessProfile.asStateFlow()

    fun loadProfile(): BusinessProfile {
        val defaultProfile = BusinessProfile()
        return BusinessProfile(
            businessName = prefs.getString(KEY_BUSINESS_NAME, defaultProfile.businessName) ?: defaultProfile.businessName,
            ownerName = prefs.getString(KEY_OWNER_NAME, defaultProfile.ownerName) ?: defaultProfile.ownerName,
            phone = prefs.getString(KEY_PHONE, defaultProfile.phone) ?: defaultProfile.phone,
            email = prefs.getString(KEY_EMAIL, defaultProfile.email) ?: defaultProfile.email,
            address = prefs.getString(KEY_ADDRESS, defaultProfile.address) ?: defaultProfile.address,
            city = prefs.getString(KEY_CITY, defaultProfile.city) ?: defaultProfile.city,
            state = prefs.getString(KEY_STATE, defaultProfile.state) ?: defaultProfile.state,
            pincode = prefs.getString(KEY_PINCODE, defaultProfile.pincode) ?: defaultProfile.pincode,
            gstin = prefs.getString(KEY_GSTIN, defaultProfile.gstin) ?: defaultProfile.gstin,
            tagline = prefs.getString(KEY_TAGLINE, defaultProfile.tagline) ?: defaultProfile.tagline,
            upiId = prefs.getString(KEY_UPI_ID, defaultProfile.upiId) ?: defaultProfile.upiId,
            termsAndConditions = prefs.getString(KEY_TERMS, defaultProfile.termsAndConditions) ?: defaultProfile.termsAndConditions,
            quoteValidityDays = prefs.getInt(KEY_VALIDITY_DAYS, defaultProfile.quoteValidityDays),
            defaultTaxPercent = prefs.getFloat(KEY_TAX_PERCENT, defaultProfile.defaultTaxPercent.toFloat()).toDouble()
        )
    }

    fun saveProfile(profile: BusinessProfile) {
        prefs.edit()
            .putString(KEY_BUSINESS_NAME, profile.businessName.trim())
            .putString(KEY_OWNER_NAME, profile.ownerName.trim())
            .putString(KEY_PHONE, profile.phone.trim())
            .putString(KEY_EMAIL, profile.email.trim())
            .putString(KEY_ADDRESS, profile.address.trim())
            .putString(KEY_CITY, profile.city.trim())
            .putString(KEY_STATE, profile.state.trim())
            .putString(KEY_PINCODE, profile.pincode.trim())
            .putString(KEY_GSTIN, profile.gstin.trim())
            .putString(KEY_TAGLINE, profile.tagline.trim())
            .putString(KEY_UPI_ID, profile.upiId.trim())
            .putString(KEY_TERMS, profile.termsAndConditions.trim())
            .putInt(KEY_VALIDITY_DAYS, profile.quoteValidityDays)
            .putFloat(KEY_TAX_PERCENT, profile.defaultTaxPercent.toFloat())
            .apply()

        _businessProfile.value = profile
    }

    fun resetToDefaults() {
        val defaults = BusinessProfile()
        saveProfile(defaults)
    }

    companion object {
        private const val PREFS_NAME = "divyansh_business_settings_prefs"
        private const val KEY_BUSINESS_NAME = "key_business_name"
        private const val KEY_OWNER_NAME = "key_owner_name"
        private const val KEY_PHONE = "key_phone"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_ADDRESS = "key_address"
        private const val KEY_CITY = "key_city"
        private const val KEY_STATE = "key_state"
        private const val KEY_PINCODE = "key_pincode"
        private const val KEY_GSTIN = "key_gstin"
        private const val KEY_TAGLINE = "key_tagline"
        private const val KEY_UPI_ID = "key_upi_id"
        private const val KEY_TERMS = "key_terms"
        private const val KEY_VALIDITY_DAYS = "key_validity_days"
        private const val KEY_TAX_PERCENT = "key_tax_percent"

        @Volatile
        private var instance: BusinessSettingsManager? = null

        fun getInstance(context: Context): BusinessSettingsManager {
            return instance ?: synchronized(this) {
                instance ?: BusinessSettingsManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
