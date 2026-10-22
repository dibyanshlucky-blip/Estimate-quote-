package com.example.data.model

object BrandProductRegistry {
    val commonWorkDescriptions = listOf(
        "Interior Wall Painting",
        "Ceiling Painting",
        "Wall Putty Application",
        "Interior Wall Primer",
        "Exterior Wall Primer",
        "Exterior Wall Painting",
        "Designer Texture Painting",
        "Door & Window Enamel Painting",
        "Metal & Safety Grill Painting",
        "Wood Melamine Polish",
        "PU Polish Finish",
        "Duco Paint Spray Finish",
        "Natural Touchwood Polish",
        "Custom Surface Work"
    )

    val popularBrands = listOf(
        "Asian Paints",
        "Berger Paints",
        "Nerolac",
        "Dulux",
        "Indigo Paints",
        "Birla White",
        "J.K. Cement",
        "Sirca Wood Coatings",
        "Other / Unbranded"
    )

    val wallProductTypes = listOf(
        "Distemper",
        "Interior Emulsion",
        "Premium Emulsion",
        "Luxury Emulsion",
        "Primer",
        "Putty",
        "Texture Paint",
        "Exterior Paint",
        "OBD (Oil Bound Distemper)",
        "Tractor Emulsion",
        "Royale Luxury",
        "Luster Paint",
        "Other / Custom"
    )

    val ceilingProductTypes = listOf(
        "Distemper",
        "Interior Emulsion",
        "Premium Emulsion",
        "Luxury Emulsion",
        "Primer",
        "Putty",
        "Ceiling White",
        "Texture Paint",
        "Other / Custom"
    )

    val roomCategories = listOf(
        "Bedroom",
        "Hall",
        "Kitchen",
        "Bathroom",
        "Passage",
        "Balcony",
        "Other"
    )

    val wallDeductionTypes = listOf(
        "Door",
        "Window",
        "Ventilator",
        "Opening",
        "Other"
    )

    val ceilingDeductionTypes = listOf(
        "Cutout",
        "Skylight",
        "Opening",
        "Shaft",
        "Other"
    )

    val brandOptions = listOf(
        "Asian Paints",
        "Berger",
        "Nerolac",
        "Dulux",
        "Indigo Paints",
        "Other / Custom Brand"
    )

    val commonFinishes = listOf(
        "Matt",
        "Sheen",
        "Satin",
        "Gloss",
        "High Gloss",
        "Luxury Silk",
        "Eggshell",
        "Metallic Finish",
        "Custom"
    )

    val brandProductsMap = mapOf(
        "Asian Paints" to listOf(
            "Royale Luxury Emulsion",
            "Royale Aspira",
            "Royale Matt",
            "Apcolite Premium Emulsion",
            "Apcolite All Protek",
            "Tractor Emulsion",
            "Tractor Uno Distemper",
            "Decoprime Wall Primer",
            "Apex Weatherproof Exterior",
            "Apex Ultima",
            "Royale Play Texture",
            "Apcolite Premium Gloss Enamel",
            "Touchwood Wood Finish",
            "Melamyne Gold Polish",
            "PU Luxury Polish"
        ),
        "Berger Paints" to listOf(
            "Silk Glamor Luxury Emulsion",
            "Easy Clean Washable Emulsion",
            "Bison Emulsion",
            "WeatherCoat Long Life",
            "WeatherCoat Anti Dust",
            "Luxol High Gloss Enamel",
            "BP White Primer",
            "Bison Wall Putty"
        ),
        "Nerolac" to listOf(
            "Beauty Gold Emulsion",
            "Impressions Ultra HD",
            "Excel Total Exterior",
            "Suraksha Plus",
            "Synthetic Enamel"
        ),
        "Dulux" to listOf(
            "Velvet Touch",
            "Promise Interior Emulsion",
            "Weathershield Powerflexx",
            "Gloss Enamel"
        ),
        "Birla White" to listOf(
            "WallCare Putty",
            "Extocare Primer",
            "White Cement"
        ),
        "Indigo Paints" to listOf(
            "Metallic Emulsion",
            "Bright Ceiling White",
            "Floor Coat Emulsion"
        )
    )

    fun getSuggestedProducts(brand: String): List<String> {
        return brandProductsMap[brand] ?: listOf(
            "Premium Emulsion",
            "Wall Primer",
            "Wall Putty",
            "Weatherproof Exterior",
            "Gloss Enamel",
            "Wood Polish"
        )
    }
}
