package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BusinessProfile
import com.example.util.BusinessSettingsManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BusinessSettingsTest {

    @Test
    fun testDefaultBusinessProfile() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = BusinessSettingsManager(context)
        val profile = manager.loadProfile()

        assertEquals("Divyansh Enterprises", profile.businessName)
        assertEquals("Abhimanyu Kumar", profile.ownerName)
        assertEquals("Pune", profile.city)
        assertEquals(30, profile.quoteValidityDays)
    }

    @Test
    fun testSaveAndLoadCustomBusinessProfile() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = BusinessSettingsManager(context)

        val customProfile = BusinessProfile(
            businessName = "Shree Ganesh Painting Services",
            ownerName = "Ramesh Sharma",
            phone = "+91 99887 76655",
            email = "shreeganesh@paint.com",
            address = "Baner Road, Pune",
            city = "Pune",
            state = "Maharashtra",
            pincode = "411045",
            gstin = "27AABCS1234F1Z9",
            tagline = "Premium Interior & Exterior Paints",
            upiId = "shreeganesh@upi",
            quoteValidityDays = 45
        )

        manager.saveProfile(customProfile)

        val loaded = manager.loadProfile()
        assertEquals("Shree Ganesh Painting Services", loaded.businessName)
        assertEquals("Ramesh Sharma", loaded.ownerName)
        assertEquals("+91 99887 76655", loaded.phone)
        assertEquals("Baner Road, Pune", loaded.address)
        assertEquals(45, loaded.quoteValidityDays)

        // Reset back to defaults
        manager.resetToDefaults()
        val afterReset = manager.loadProfile()
        assertEquals("Divyansh Enterprises", afterReset.businessName)
        assertEquals("Abhimanyu Kumar", afterReset.ownerName)
    }
}
