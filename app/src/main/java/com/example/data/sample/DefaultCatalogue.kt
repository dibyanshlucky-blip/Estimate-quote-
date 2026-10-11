package com.example.data.sample

import com.example.data.model.CatalogueItemEntity
import com.example.data.model.DirectMeasurementEntity
import com.example.data.model.EstimateItemEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.RoomMeasurementEntity

object DefaultCatalogue {
    fun getDefaultItems(): List<CatalogueItemEntity> = listOf(
        CatalogueItemEntity(
            id = 1,
            category = "Preparation",
            titleEn = "Wall Cleaning & Surface Prep",
            titleHi = "दीवार सफाई और तैयारी",
            brandProduct = "Standard Prep / Sanding",
            surfaceType = "Wall",
            unit = "sqft",
            materialRate = 2.0,
            labourRate = 5.0,
            coats = 1,
            remarks = "Scraping old paint, sanding with emery paper, washing dust"
        ),
        CatalogueItemEntity(
            id = 2,
            category = "Preparation",
            titleEn = "Wall Putty (2 Coats)",
            titleHi = "वॉल पुट्टी (2 कोट)",
            brandProduct = "Birla White / Asian Paints",
            surfaceType = "Wall",
            unit = "sqft",
            materialRate = 7.0,
            labourRate = 9.0,
            coats = 2,
            remarks = "2 coats putty application with smooth knife finish"
        ),
        CatalogueItemEntity(
            id = 3,
            category = "Preparation",
            titleEn = "Wall Primer (Interior/Exterior)",
            titleHi = "वॉल प्राइमर",
            brandProduct = "Asian Paints Decoprime",
            surfaceType = "Wall",
            unit = "sqft",
            materialRate = 4.5,
            labourRate = 4.0,
            coats = 1,
            remarks = "Water thinnable primer for maximum adhesion"
        ),
        CatalogueItemEntity(
            id = 4,
            category = "Interior",
            titleEn = "Distemper Paint",
            titleHi = "डिस्टेंपर पेंट",
            brandProduct = "Asian Paints Tractor Uno",
            surfaceType = "Wall",
            unit = "sqft",
            materialRate = 5.0,
            labourRate = 6.0,
            coats = 2,
            remarks = "Economical washable distemper"
        ),
        CatalogueItemEntity(
            id = 5,
            category = "Interior",
            titleEn = "Interior Emulsion",
            titleHi = "इंटीरियर इमल्शन",
            brandProduct = "Asian Paints Tractor Emulsion",
            surfaceType = "Wall",
            unit = "sqft",
            materialRate = 9.0,
            labourRate = 8.0,
            coats = 2,
            remarks = "Smooth matt finish with scrub resistance"
        ),
        CatalogueItemEntity(
            id = 6,
            category = "Interior",
            titleEn = "Premium Interior Emulsion",
            titleHi = "प्रीमियम इंटीरियर इमल्शन",
            brandProduct = "Asian Paints Apcolite All Protek",
            surfaceType = "Wall",
            unit = "sqft",
            materialRate = 16.0,
            labourRate = 11.0,
            coats = 2,
            remarks = "Rich sheen finish, stain resistant and washable"
        ),
        CatalogueItemEntity(
            id = 7,
            category = "Interior",
            titleEn = "Luxury Interior Paint",
            titleHi = "लक्जरी इंटीरियर पेंट (Royale)",
            brandProduct = "Asian Paints Royale Luxury Emulsion",
            surfaceType = "Wall",
            unit = "sqft",
            materialRate = 28.0,
            labourRate = 15.0,
            coats = 2,
            remarks = "Ultra-sheen silky finish, anti-bacterial coating"
        ),
        CatalogueItemEntity(
            id = 8,
            category = "Exterior",
            titleEn = "Exterior Wall Primer",
            titleHi = "एक्सटीरियर वॉल प्राइमर",
            brandProduct = "Asian Paints Exterior Wall Primer",
            surfaceType = "Exterior Wall",
            unit = "sqft",
            materialRate = 5.5,
            labourRate = 5.0,
            coats = 1,
            remarks = "Weather barrier sealing primer"
        ),
        CatalogueItemEntity(
            id = 9,
            category = "Exterior",
            titleEn = "Exterior Emulsion (Apex / Weathercoat)",
            titleHi = "एक्सटीरियर इमल्शन (Apex)",
            brandProduct = "Asian Paints Apex Weatherproof",
            surfaceType = "Exterior Wall",
            unit = "sqft",
            materialRate = 18.0,
            labourRate = 12.0,
            coats = 2,
            remarks = "Algae and UV resistant exterior coating"
        ),
        CatalogueItemEntity(
            id = 10,
            category = "Textures & Finishes",
            titleEn = "Texture Paint (Royale Play)",
            titleHi = "टेक्सचर पेंट (Royale Play)",
            brandProduct = "Asian Paints Royale Play",
            surfaceType = "Wall",
            unit = "sqft",
            materialRate = 55.0,
            labourRate = 45.0,
            coats = 2,
            remarks = "Designer textured feature wall patterns"
        ),
        CatalogueItemEntity(
            id = 11,
            category = "Textures & Finishes",
            titleEn = "Decorative Stencil & Metallic Finish",
            titleHi = "सजावटी स्टेंसिल और मेटैलिक फिनिश",
            brandProduct = "Royale Special Effects",
            surfaceType = "Wall",
            unit = "sqft",
            materialRate = 40.0,
            labourRate = 35.0,
            coats = 1,
            remarks = "Custom patterns on accent walls"
        ),
        CatalogueItemEntity(
            id = 12,
            category = "Wood & Metal",
            titleEn = "Enamel Paint (Doors & Windows)",
            titleHi = "एनेमल पेंट (दरवाजे और खिड़कियां)",
            brandProduct = "Asian Paints Apcolite Premium Gloss",
            surfaceType = "Wood",
            unit = "sqft",
            materialRate = 12.0,
            labourRate = 14.0,
            coats = 2,
            remarks = "High gloss protection for wooden frames and doors"
        ),
        CatalogueItemEntity(
            id = 13,
            category = "Wood & Metal",
            titleEn = "Metal & Safety Grill Painting",
            titleHi = "मेटल और ग्रिल पेंटिंग",
            brandProduct = "Berger Luxol / Asian Paints Enamel",
            surfaceType = "Metal / Grill",
            unit = "sqft",
            materialRate = 10.0,
            labourRate = 12.0,
            coats = 2,
            remarks = "Rust-preventive red oxide primer + gloss top coat"
        ),
        CatalogueItemEntity(
            id = 14,
            category = "Wood & Metal",
            titleEn = "Natural Wood Polish (French / Oil)",
            titleHi = "नेचुरल वुड पॉलिश",
            brandProduct = "Touchwood / Sheenlac",
            surfaceType = "Wood",
            unit = "sqft",
            materialRate = 22.0,
            labourRate = 25.0,
            coats = 3,
            remarks = "Hand-rubbed wood grain gloss finish"
        ),
        CatalogueItemEntity(
            id = 15,
            category = "Wood & Metal",
            titleEn = "Melamine Polish",
            titleHi = "मेलामाइन पॉलिश",
            brandProduct = "Asian Paints Melamyne Gold",
            surfaceType = "Wood",
            unit = "sqft",
            materialRate = 38.0,
            labourRate = 32.0,
            coats = 2,
            remarks = "Heat and scratch-resistant matte/gloss wood coat"
        ),
        CatalogueItemEntity(
            id = 16,
            category = "Wood & Metal",
            titleEn = "Duco Paint Finish",
            titleHi = "डूको पेंट फिनिश",
            brandProduct = "Asian Paints Automotive Duco",
            surfaceType = "Wood",
            unit = "sqft",
            materialRate = 85.0,
            labourRate = 65.0,
            coats = 4,
            remarks = "Spray applied ultra smooth solid colour finish"
        ),
        CatalogueItemEntity(
            id = 17,
            category = "Wood & Metal",
            titleEn = "PU Polish (Polyurethane)",
            titleHi = "पीयू पॉलिश (Polyurethane)",
            brandProduct = "Asian Paints Acolook PU",
            surfaceType = "Wood",
            unit = "sqft",
            materialRate = 110.0,
            labourRate = 80.0,
            coats = 3,
            remarks = "Premium Italian gloss polyurethane finish"
        ),
        CatalogueItemEntity(
            id = 18,
            category = "Custom",
            titleEn = "Custom / Site Specific Work",
            titleHi = "अन्य कस्टम काम",
            brandProduct = "Contractor Specified",
            surfaceType = "Other",
            unit = "lumpsum",
            materialRate = 0.0,
            labourRate = 1000.0,
            coats = 1,
            remarks = "Special site work as agreed with customer"
        )
    )

    fun getSampleProject(): ProjectEntity = ProjectEntity(
        id = 1,
        customerName = "Vikram Sharma",
        mobileNumber = "9823456789",
        siteName = "Gera World of Joy, Tower C-704",
        siteAddress = "Kharadi Bypass Road, Kharadi, Pune, Maharashtra 411014",
        flatType = "2BHK",
        estimateNumber = "DE-2026-001",
        estimateDate = "10 Oct 2026",
        siteNotes = "Existing repaint work. Minor seepage repair needed near balcony window sill. High ceiling height 10 ft.",
        customerRequirements = "Luxury Royale emulsion in Living room and Master bedroom. Apcolite in kitchen. Enamel on safety door.",
        status = "Accepted",
        estimateMode = "MATERIAL_AND_LABOUR",
        deductOpenings = true,
        additionalCharges = 1500.0,
        additionalChargesDesc = "Floor masking & post-paint site cleaning",
        discountAmount = 2500.0,
        isTaxEnabled = false,
        taxPercent = 18.0
    )

    fun getSampleRooms(projectId: Long): List<RoomMeasurementEntity> = listOf(
        RoomMeasurementEntity(
            id = 1,
            projectId = projectId,
            roomName = "Living & Dining Room",
            lengthFt = 18.0,
            lengthIn = 6.0,
            widthFt = 12.0,
            widthIn = 0.0,
            heightFt = 10.0,
            heightIn = 0.0,
            doorCount = 2,
            doorWidthFt = 3.5,
            doorHeightFt = 7.0,
            windowCount = 2,
            windowWidthFt = 5.0,
            windowHeightFt = 4.0,
            additionalDeductionsSqft = 0.0,
            wallPaintingEnabled = true,
            ceilingPaintingEnabled = true,
            notes = "Feature wall on North side"
        ),
        RoomMeasurementEntity(
            id = 2,
            projectId = projectId,
            roomName = "Master Bedroom",
            lengthFt = 14.0,
            lengthIn = 0.0,
            widthFt = 11.0,
            widthIn = 6.0,
            heightFt = 10.0,
            heightIn = 0.0,
            doorCount = 1,
            doorWidthFt = 3.0,
            doorHeightFt = 7.0,
            windowCount = 1,
            windowWidthFt = 5.0,
            windowHeightFt = 4.0,
            additionalDeductionsSqft = 0.0,
            wallPaintingEnabled = true,
            ceilingPaintingEnabled = true,
            notes = "Include attached bathroom door frame"
        ),
        RoomMeasurementEntity(
            id = 3,
            projectId = projectId,
            roomName = "Kids Bedroom",
            lengthFt = 12.0,
            lengthIn = 0.0,
            widthFt = 10.0,
            widthIn = 0.0,
            heightFt = 10.0,
            heightIn = 0.0,
            doorCount = 1,
            doorWidthFt = 3.0,
            doorHeightFt = 7.0,
            windowCount = 1,
            windowWidthFt = 4.0,
            windowHeightFt = 4.0,
            additionalDeductionsSqft = 0.0,
            wallPaintingEnabled = true,
            ceilingPaintingEnabled = true,
            notes = "Bright pastel finish requested"
        ),
        RoomMeasurementEntity(
            id = 4,
            projectId = projectId,
            roomName = "Kitchen & Utility",
            lengthFt = 10.0,
            lengthIn = 0.0,
            widthFt = 8.0,
            widthIn = 0.0,
            heightFt = 10.0,
            heightIn = 0.0,
            doorCount = 1,
            doorWidthFt = 2.5,
            doorHeightFt = 7.0,
            windowCount = 1,
            windowWidthFt = 3.0,
            windowHeightFt = 3.0,
            additionalDeductionsSqft = 80.0, // Dado tiles deduction
            wallPaintingEnabled = true,
            ceilingPaintingEnabled = true,
            notes = "80 sqft dado tile area deducted"
        )
    )

    fun getSampleDirectMeasurements(projectId: Long): List<DirectMeasurementEntity> = listOf(
        DirectMeasurementEntity(
            id = 1,
            projectId = projectId,
            surfaceName = "Balcony Ceiling & Exterior Face",
            quantity = 95.0,
            unit = "sqft",
            notes = "Weathercoat exterior paint"
        ),
        DirectMeasurementEntity(
            id = 2,
            projectId = projectId,
            surfaceName = "Balcony Safety Grills",
            quantity = 65.0,
            unit = "sqft",
            notes = "Black enamel paint with red oxide primer"
        ),
        DirectMeasurementEntity(
            id = 3,
            projectId = projectId,
            surfaceName = "Main Entrance Door & Frame",
            quantity = 48.0,
            unit = "sqft",
            notes = "Melamine wood polish"
        )
    )

    fun getSampleEstimateItems(projectId: Long): List<EstimateItemEntity> = listOf(
        EstimateItemEntity(
            id = 1,
            projectId = projectId,
            category = "Preparation",
            workDescription = "Wall Scraping, Sanding & Surface Preparation",
            brandName = "Standard Site Prep",
            productName = "Emery Sandpaper 80/120",
            productVariant = "Surface Scraping",
            finish = "Uniform Raw Surface",
            surfaceType = "Wall",
            quantity = 1580.0,
            unit = "sqft",
            materialRate = 2.0,
            labourRate = 5.0,
            coats = 1,
            remarks = "Old loose paint scraped, cracks filled with knife putty"
        ),
        EstimateItemEntity(
            id = 2,
            projectId = projectId,
            category = "Preparation",
            workDescription = "Wall Putty Application",
            brandName = "Birla White",
            productName = "WallCare Putty",
            productVariant = "Water Resistant",
            finish = "Smooth Matt",
            surfaceType = "Wall",
            quantity = 1580.0,
            unit = "sqft",
            materialRate = 7.0,
            labourRate = 9.0,
            coats = 2,
            remarks = "2 coats applied with steel blade for pin-hole free base"
        ),
        EstimateItemEntity(
            id = 3,
            projectId = projectId,
            category = "Preparation",
            workDescription = "Interior Wall Primer",
            brandName = "Asian Paints",
            productName = "Decoprime",
            productVariant = "Water Thinnable",
            finish = "Matt Primer",
            surfaceType = "Wall",
            quantity = 1580.0,
            unit = "sqft",
            materialRate = 4.5,
            labourRate = 4.0,
            coats = 1,
            remarks = "Deep penetrating base coat to seal putty"
        ),
        EstimateItemEntity(
            id = 4,
            projectId = projectId,
            category = "Interior",
            workDescription = "Interior Wall Painting",
            brandName = "Asian Paints",
            productName = "Royale",
            productVariant = "Luxury Emulsion",
            finish = "Matt Finish",
            surfaceType = "Wall",
            quantity = 1120.0,
            unit = "sqft",
            materialRate = 28.0,
            labourRate = 15.0,
            coats = 2,
            remarks = "Apply over approved primer; final shade subject to customer approval."
        ),
        EstimateItemEntity(
            id = 5,
            projectId = projectId,
            category = "Interior",
            workDescription = "Ceiling Painting",
            brandName = "Asian Paints",
            productName = "Tractor Emulsion",
            productVariant = "Super White",
            finish = "Matt",
            surfaceType = "Ceiling",
            quantity = 460.0,
            unit = "sqft",
            materialRate = 9.0,
            labourRate = 8.0,
            coats = 2,
            remarks = "Anti-fungal white ceiling topcoat"
        ),
        EstimateItemEntity(
            id = 6,
            projectId = projectId,
            category = "Wood & Metal",
            workDescription = "Safety Grills & Metal Painting",
            brandName = "Asian Paints",
            productName = "Apcolite",
            productVariant = "Premium Gloss Enamel",
            finish = "High Gloss",
            surfaceType = "Metal / Grill",
            quantity = 65.0,
            unit = "sqft",
            materialRate = 10.0,
            labourRate = 12.0,
            coats = 2,
            remarks = "Rust-preventive red oxide primer base + 2 topcoats"
        ),
        EstimateItemEntity(
            id = 7,
            projectId = projectId,
            category = "Wood & Metal",
            workDescription = "Main Door Wood Polish",
            brandName = "Asian Paints",
            productName = "Melamyne Gold",
            productVariant = "Clear Wood Finish",
            finish = "Satin Sheen",
            surfaceType = "Wood",
            quantity = 48.0,
            unit = "sqft",
            materialRate = 38.0,
            labourRate = 32.0,
            coats = 2,
            remarks = "Hand-rubbed natural teak wood grain finish"
        )
    )

    fun getSamplePayments(projectId: Long): List<PaymentEntity> = listOf(
        PaymentEntity(
            id = 1,
            projectId = projectId,
            amount = 25000.0,
            paymentDate = "10 Oct 2026",
            paymentMethod = "UPI",
            referenceNumber = "UPI/GPay/439201938210",
            remarks = "50% Token advance before material procurement"
        ),
        PaymentEntity(
            id = 2,
            projectId = projectId,
            amount = 15000.0,
            paymentDate = "14 Oct 2026",
            paymentMethod = "Bank Transfer",
            referenceNumber = "NEFT/HDFC/8839210",
            remarks = "Stage 2 payment after putty & primer complete"
        )
    )

    fun getDefaultWorkers(): List<com.example.data.model.WorkerEntity> = listOf(
        com.example.data.model.WorkerEntity(
            id = 1,
            name = "Ramesh Kumar",
            role = "Master Painter (कारीगर)",
            dailyRate = 900.0,
            mobileNumber = "9823112233"
        ),
        com.example.data.model.WorkerEntity(
            id = 2,
            name = "Sunil Yadav",
            role = "Painter (कारीगर)",
            dailyRate = 850.0,
            mobileNumber = "9823445566"
        ),
        com.example.data.model.WorkerEntity(
            id = 3,
            name = "Mukesh Paswan",
            role = "Helper (मजदूर)",
            dailyRate = 550.0,
            mobileNumber = "9823778899"
        ),
        com.example.data.model.WorkerEntity(
            id = 4,
            name = "Dinesh Vishwakarma",
            role = "Polisher (पॉलिशर)",
            dailyRate = 1000.0,
            mobileNumber = "9823001122"
        )
    )

    fun getSampleAttendance(projectId: Long): List<com.example.data.model.AttendanceRecordEntity> = listOf(
        com.example.data.model.AttendanceRecordEntity(
            id = 1,
            workerId = 1,
            projectId = projectId,
            date = "10 Oct 2026",
            status = "Full Day",
            overtimeHours = 1.0,
            advancePaid = 200.0,
            notes = "Wall sanding and scraping"
        ),
        com.example.data.model.AttendanceRecordEntity(
            id = 2,
            workerId = 2,
            projectId = projectId,
            date = "10 Oct 2026",
            status = "Full Day",
            overtimeHours = 0.0,
            advancePaid = 200.0,
            notes = "Putty 1st coat living room"
        ),
        com.example.data.model.AttendanceRecordEntity(
            id = 3,
            workerId = 3,
            projectId = projectId,
            date = "10 Oct 2026",
            status = "Full Day",
            overtimeHours = 0.0,
            advancePaid = 100.0,
            notes = "Floor masking & dust cleaning"
        )
    )

    fun getSampleProgress(projectId: Long): com.example.data.model.SiteProgressEntity = com.example.data.model.SiteProgressEntity(
        projectId = projectId,
        stage1Scraping = true,
        stage2Putty = true,
        stage3Primer = true,
        stage4Paint = false,
        stage5Handover = false,
        notes = "Putty and primer done. Top coat scheduled for tomorrow."
    )
}
