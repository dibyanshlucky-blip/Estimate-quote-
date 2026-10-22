package com.example.data.model

data class BusinessProfile(
    val businessName: String = "Divyansh Enterprises",
    val ownerName: String = "Abhimanyu Kumar",
    val phone: String = "+91 98234 56789",
    val email: String = "divyansh.pune@gmail.com",
    val address: String = "Shop No. 4, Kharadi Road, Pune, Maharashtra",
    val city: String = "Pune",
    val state: String = "Maharashtra",
    val pincode: String = "411014",
    val gstin: String = "27AABCD1234E1Z0",
    val tagline: String = "Painting Contractor • Surface Protection & Decorative Finishes",
    val upiId: String = "divyansh@okaxis",
    val termsAndConditions: String = "• Standard surface preparation, primer, putty and paint materials specified above.\n• Payment Schedule: 50% advance before commencement, 40% on primer/putty completion, 10% on handover.\n• Continuous water and uninterrupted electricity at site to be provided by client.\n• Quotation valid for 30 days.",
    val quoteValidityDays: Int = 30,
    val defaultTaxPercent: Double = 18.0
) {
    val fullFormattedAddress: String
        get() = buildString {
            append(address)
            if (city.isNotBlank() && !address.contains(city, ignoreCase = true)) {
                append(", ").append(city)
            }
            if (state.isNotBlank() && !address.contains(state, ignoreCase = true)) {
                append(", ").append(state)
            }
            if (pincode.isNotBlank() && !address.contains(pincode)) {
                append(" - ").append(pincode)
            }
        }
}
