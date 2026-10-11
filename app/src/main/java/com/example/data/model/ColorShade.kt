package com.example.data.model

data class ColorShade(
    val name: String,
    val code: String,
    val brand: String,
    val category: String,
    val hexCode: String,
    val recommendedFor: String
)

object ColorPaletteLibrary {
    val shades: List<ColorShade> = listOf(
        // Whites & Creams
        ColorShade("Cotton Wool", "L104", "Asian Paints", "Whites & Creams", "#F8F6F0", "All Ceilings & Base Walls"),
        ColorShade("Morning Glory", "0765", "Asian Paints", "Whites & Creams", "#F4EEDB", "Living Room Base"),
        ColorShade("Almond Frost", "0313", "Asian Paints", "Whites & Creams", "#EFE5D0", "Bedrooms & Corridors"),
        ColorShade("Pure Ivory", "0948", "Asian Paints", "Whites & Creams", "#FFFDF4", "Modern Minimalist Walls"),

        // Warm Accents & Pastels
        ColorShade("Desert Sun", "7943", "Asian Paints Royale", "Warm Accents", "#E8A87C", "Living Room Feature Wall"),
        ColorShade("Terracotta Clay", "9921", "Berger Paints", "Warm Accents", "#D06A4C", "Dining Area Accent"),
        ColorShade("Warm Peach", "0512", "Asian Paints", "Warm Accents", "#FAD2B8", "Kids Bedroom"),
        ColorShade("Marigold Saffron", "0429", "Asian Paints", "Warm Accents", "#F6B352", "Foyer / Entrance Accent"),

        // Royal Blues & Deep Accents
        ColorShade("Royale Blue / Deep Ocean", "7246", "Asian Paints Royale", "Royal Blues", "#1A365D", "Master Bedroom Bedwall"),
        ColorShade("Midnight Sky", "9120", "Asian Paints", "Royal Blues", "#1B2A4A", "TV Unit Accent Wall"),
        ColorShade("Serene Sky", "7312", "Asian Paints", "Royal Blues", "#A2C2E1", "Study Room & Balcony"),
        ColorShade("Teal Whisper", "7489", "Berger Silk", "Royal Blues", "#31708E", "Drawing Room Feature"),

        // Greens & Natural Earth
        ColorShade("Olive Grove", "7683", "Asian Paints Royale", "Natural Greens", "#586F56", "Balcony & Master Bed"),
        ColorShade("Sage Green", "8341", "Berger Paints", "Natural Greens", "#8FA287", "Kitchen & Dining"),
        ColorShade("Mint Frost", "7749", "Asian Paints", "Natural Greens", "#C8D6AF", "Puja Room & Passage"),

        // Grays & Modern Neutrals
        ColorShade("Urban Gray", "8294", "Asian Paints", "Modern Neutrals", "#7E8287", "Contemporary Living Room"),
        ColorShade("Silver Cloud", "8310", "Asian Paints", "Modern Neutrals", "#D3D6DB", "Complete Apartment Base"),
        ColorShade("Charcoal Shadow", "8240", "Berger", "Modern Neutrals", "#3E424B", "Safety Door & Grills")
    )
}
