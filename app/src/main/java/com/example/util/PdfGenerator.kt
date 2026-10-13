package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.ProjectWithDetails
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object PdfGenerator {

    /**
     * Generates a professional multi-page A4 quotation PDF for Divyansh Enterprises.
     * Includes separate Wall and Ceiling measurement rows, gross area, deductions, net area,
     * paint brand, product name, number of coats, rates, putty/primer, totals, and signature block.
     */
    fun generateQuotationPdf(
        context: Context,
        projectDetails: ProjectWithDetails,
        isLabourOnly: Boolean
    ): File {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // Standard A4 width in points
        val pageHeight = 842 // Standard A4 height in points

        var currentPageNumber = 1
        var page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create())
        var canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()
        val textPaint = Paint()
        val boldPaint = Paint()
        val smallPaint = Paint()
        val headerPaint = Paint()
        val linePaint = Paint().apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 0.8f
        }

        fun drawHeaderAndClientDetails(c: Canvas) {
            // White Background
            paint.color = Color.WHITE
            c.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

            // Header Banner - Deep Navy Blue
            headerPaint.color = Color.rgb(15, 43, 92) // #0F2B5C
            c.drawRect(0f, 0f, pageWidth.toFloat(), 75f, headerPaint)

            // Gold Accent Stripe
            paint.color = Color.rgb(217, 119, 6) // Amber/Gold #D97706
            c.drawRect(0f, 75f, pageWidth.toFloat(), 79f, paint)

            // Header Title
            titlePaint.color = Color.WHITE
            titlePaint.textSize = 20f
            titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            c.drawText("DIVYANSH ENTERPRISES", 30f, 34f, titlePaint)

            smallPaint.color = Color.rgb(254, 243, 199)
            smallPaint.textSize = 10f
            c.drawText("Painting Contractor • Surface Protection & Decorative Finishes", 30f, 48f, smallPaint)
            c.drawText("Abhimanyu Kumar | Kharadi, Pune, Maharashtra | Ph: +91 98234 56789", 30f, 62f, smallPaint)

            // Estimate Type Badge
            val badgeTitle = if (isLabourOnly) "LABOUR-ONLY PAINTING QUOTATION" else "PAINTING ESTIMATE & QUOTATION"
            boldPaint.color = Color.rgb(15, 43, 92)
            boldPaint.textSize = 12f
            boldPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            c.drawText(badgeTitle, 30f, 100f, boldPaint)

            // Date & Estimate No
            textPaint.color = Color.rgb(51, 65, 85)
            textPaint.textSize = 9.5f
            c.drawText("Estimate No: ${projectDetails.project.estimateNumber}", 385f, 96f, textPaint)
            c.drawText("Date: ${projectDetails.project.estimateDate}", 385f, 108f, textPaint)

            c.drawLine(30f, 115f, 565f, 115f, linePaint)

            // Client & Site Details Box
            paint.color = Color.rgb(248, 250, 252)
            c.drawRoundRect(30f, 120f, 565f, 185f, 6f, 6f, paint)
            paint.style = Paint.Style.STROKE
            paint.color = Color.rgb(203, 213, 225)
            c.drawRoundRect(30f, 120f, 565f, 185f, 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            boldPaint.textSize = 9f
            boldPaint.color = Color.rgb(15, 23, 42)
            c.drawText("CLIENT DETAILS:", 40f, 135f, boldPaint)
            textPaint.textSize = 8.5f
            c.drawText("Name: ${projectDetails.project.customerName}", 40f, 149f, textPaint)
            c.drawText("Mobile: ${projectDetails.project.mobileNumber}", 40f, 162f, textPaint)
            c.drawText("Property: ${projectDetails.project.flatType}", 40f, 175f, textPaint)

            c.drawText("SITE DETAILS:", 290f, 135f, boldPaint)
            c.drawText("Site Name: ${projectDetails.project.siteName}", 290f, 149f, textPaint)
            val addr = if (projectDetails.project.siteAddress.length > 44) projectDetails.project.siteAddress.take(42) + "..." else projectDetails.project.siteAddress
            c.drawText("Site Address: $addr", 290f, 162f, textPaint)
            c.drawText("Mode: ${if (isLabourOnly) "Labour Only" else "Material + Labour"}", 290f, 175f, textPaint)
        }

        // Draw initial page header
        drawHeaderAndClientDetails(canvas)

        // Draw Table Column Headers
        var yPos = 205f

        fun drawTableHeader(c: Canvas, y: Float) {
            paint.color = Color.rgb(241, 245, 249)
            c.drawRect(30f, y, 565f, y + 18f, paint)

            boldPaint.color = Color.rgb(30, 41, 59)
            boldPaint.textSize = 8f
            c.drawText("ITEM / SURFACE DESCRIPTION", 35f, y + 12f, boldPaint)
            c.drawText("GROSS", 230f, y + 12f, boldPaint)
            c.drawText("DEDUCT", 275f, y + 12f, boldPaint)
            c.drawText("NET SQFT", 325f, y + 12f, boldPaint)
            if (!isLabourOnly) {
                c.drawText("MAT. ₹", 380f, y + 12f, boldPaint)
                c.drawText("LAB. ₹", 435f, y + 12f, boldPaint)
                c.drawText("TOTAL (₹)", 495f, y + 12f, boldPaint)
            } else {
                c.drawText("LAB. RATE", 420f, y + 12f, boldPaint)
                c.drawText("AMOUNT (₹)", 495f, y + 12f, boldPaint)
            }
        }

        drawTableHeader(canvas, yPos)
        yPos += 22f

        val productPaint = Paint().apply {
            color = Color.rgb(29, 78, 216) // Blue
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val prepPaint = Paint().apply {
            color = Color.rgb(4, 120, 87) // Emerald
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val rooms = projectDetails.rooms

        // Render each Room: separate Wall and Ceiling rows, deductions, brand, rate, amount
        rooms.forEachIndexed { rIndex, room ->
            val details = room.getParsedDetails()
            val netWall = room.getNetWallAreaSqft(projectDetails.project.deductOpenings)
            val netCeil = room.netCeilingAreaSqft

            val wallAmt = details.wallPaint.calculateAmount(netWall, isLabourOnly)
            val ceilAmt = details.ceilingPaint.calculateAmount(netCeil, isLabourOnly)

            // Check if page overflow
            if (yPos > 670f) {
                pdfDocument.finishPage(page)
                currentPageNumber++
                page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create())
                canvas = page.canvas
                drawHeaderAndClientDetails(canvas)
                yPos = 205f
                drawTableHeader(canvas, yPos)
                yPos += 22f
            }

            // Room Title Banner Row
            paint.color = Color.rgb(238, 242, 255)
            canvas.drawRect(30f, yPos, 565f, yPos + 14f, paint)
            boldPaint.textSize = 8.5f
            boldPaint.color = Color.rgb(30, 58, 138)
            canvas.drawText("ROOM ${rIndex + 1}: ${room.roomName.uppercase()}", 35f, yPos + 10.5f, boldPaint)
            yPos += 18f

            // 1. WALL ROW
            if (room.wallPaintingEnabled) {
                textPaint.textSize = 8.5f
                textPaint.color = Color.rgb(15, 23, 42)
                canvas.drawText("  • Wall Painting (${details.wallPaint.brand} - ${details.wallPaint.productType})", 35f, yPos, textPaint)
                canvas.drawText(CurrencyUtils.formatSqft(room.grossWallAreaSqft), 230f, yPos, textPaint)
                canvas.drawText("−${CurrencyUtils.formatSqft(room.totalDeductionsSqft)}", 275f, yPos, textPaint)
                canvas.drawText(CurrencyUtils.formatSqft(netWall), 325f, yPos, boldPaint)

                if (!isLabourOnly) {
                    canvas.drawText("₹${details.wallPaint.materialRate}", 380f, yPos, textPaint)
                    canvas.drawText("₹${details.wallPaint.labourRate}", 435f, yPos, textPaint)
                    canvas.drawText(CurrencyUtils.formatInr(wallAmt), 495f, yPos, boldPaint)
                } else {
                    canvas.drawText("₹${details.wallPaint.labourRate}", 420f, yPos, textPaint)
                    canvas.drawText(CurrencyUtils.formatInr(wallAmt), 495f, yPos, boldPaint)
                }
                yPos += 11f

                val wallSpecs = "     Brand: ${details.wallPaint.brand} • ${details.wallPaint.productType} ${details.wallPaint.productVariant} • ${details.wallPaint.coats} Coats"
                canvas.drawText(wallSpecs, 35f, yPos, productPaint)
                yPos += 13f
            }

            // 2. CEILING ROW
            if (room.ceilingPaintingEnabled) {
                textPaint.textSize = 8.5f
                textPaint.color = Color.rgb(15, 23, 42)
                canvas.drawText("  • Ceiling Painting (${details.ceilingPaint.brand} - ${details.ceilingPaint.productType})", 35f, yPos, textPaint)
                canvas.drawText(CurrencyUtils.formatSqft(room.grossCeilingAreaSqft), 230f, yPos, textPaint)
                canvas.drawText("−${CurrencyUtils.formatSqft(room.totalCeilingDeductionsSqft)}", 275f, yPos, textPaint)
                canvas.drawText(CurrencyUtils.formatSqft(netCeil), 325f, yPos, boldPaint)

                if (!isLabourOnly) {
                    canvas.drawText("₹${details.ceilingPaint.materialRate}", 380f, yPos, textPaint)
                    canvas.drawText("₹${details.ceilingPaint.labourRate}", 435f, yPos, textPaint)
                    canvas.drawText(CurrencyUtils.formatInr(ceilAmt), 495f, yPos, boldPaint)
                } else {
                    canvas.drawText("₹${details.ceilingPaint.labourRate}", 420f, yPos, textPaint)
                    canvas.drawText(CurrencyUtils.formatInr(ceilAmt), 495f, yPos, boldPaint)
                }
                yPos += 11f

                val ceilSpecs = "     Brand: ${details.ceilingPaint.brand} • ${details.ceilingPaint.productType} ${details.ceilingPaint.productVariant} • ${details.ceilingPaint.coats} Coats"
                canvas.drawText(ceilSpecs, 35f, yPos, productPaint)
                yPos += 13f
            }

            // 3. Optional Putty and Primer lines if enabled in room
            val prepItems = mutableListOf<String>()
            if (details.wallPutty.enabled) prepItems.add("Wall Putty (${details.wallPutty.brand})")
            if (details.wallPrimer.enabled) prepItems.add("Wall Primer (${details.wallPrimer.brand})")
            if (details.ceilingPutty.enabled) prepItems.add("Ceiling Putty")
            if (details.ceilingPrimer.enabled) prepItems.add("Ceiling Primer")

            if (prepItems.isNotEmpty()) {
                val prepSummary = "     Surface Prep: ${prepItems.joinToString(" • ")}"
                canvas.drawText(prepSummary, 35f, yPos, prepPaint)
                yPos += 12f
            }

            canvas.drawLine(30f, yPos, 565f, yPos, linePaint)
            yPos += 6f
        }

        // Direct sqft items if any
        if (projectDetails.directMeasurements.isNotEmpty()) {
            projectDetails.directMeasurements.forEach { d ->
                if (yPos > 670f) {
                    pdfDocument.finishPage(page)
                    currentPageNumber++
                    page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create())
                    canvas = page.canvas
                    drawHeaderAndClientDetails(canvas)
                    yPos = 205f
                    drawTableHeader(canvas, yPos)
                    yPos += 22f
                }
                canvas.drawText("Direct Area: ${d.surfaceName}", 35f, yPos, textPaint)
                canvas.drawText("${CurrencyUtils.formatSqft(d.quantity)}", 325f, yPos, textPaint)
                yPos += 14f
            }
        }

        // Financial Calculation Summary Box
        val boxTop = 500f.coerceAtLeast(yPos + 10f)
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRoundRect(290f, boxTop, 565f, boxTop + 140f, 6f, 6f, paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(203, 213, 225)
        canvas.drawRoundRect(290f, boxTop, 565f, boxTop + 140f, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        var finY = boxTop + 16f
        textPaint.textSize = 8.5f
        boldPaint.textSize = 8.5f

        val subtotal = if (isLabourOnly) projectDetails.labourSubtotal else projectDetails.materialAndLabourSubtotal
        canvas.drawText("Subtotal:", 305f, finY, textPaint)
        canvas.drawText(CurrencyUtils.formatInr(subtotal), 485f, finY, boldPaint)
        finY += 15f

        if (projectDetails.project.additionalCharges > 0) {
            canvas.drawText("Additional Charges:", 305f, finY, textPaint)
            canvas.drawText(CurrencyUtils.formatInr(projectDetails.project.additionalCharges), 485f, finY, boldPaint)
            finY += 15f
        }

        if (projectDetails.project.discountAmount > 0) {
            canvas.drawText("Discount:", 305f, finY, textPaint)
            canvas.drawText("−${CurrencyUtils.formatInr(projectDetails.project.discountAmount)}", 485f, finY, boldPaint)
            finY += 15f
        }

        if (projectDetails.project.isTaxEnabled) {
            val tax = if (isLabourOnly) projectDetails.labourTaxAmount else projectDetails.materialAndLabourTaxAmount
            canvas.drawText("GST (${projectDetails.project.taxPercent}%):", 305f, finY, textPaint)
            canvas.drawText(CurrencyUtils.formatInr(tax), 485f, finY, boldPaint)
            finY += 15f
        }

        val grandTotal = if (isLabourOnly) projectDetails.labourGrandTotal else projectDetails.materialAndLabourGrandTotal
        paint.color = Color.rgb(15, 43, 92)
        canvas.drawRect(290f, finY - 2f, 565f, finY + 22f, paint)
        boldPaint.color = Color.WHITE
        boldPaint.textSize = 10.5f
        canvas.drawText("GRAND TOTAL:", 305f, finY + 14f, boldPaint)
        canvas.drawText(CurrencyUtils.formatInr(grandTotal), 475f, finY + 14f, boldPaint)
        finY += 34f

        canvas.drawText("Advance Received: ${CurrencyUtils.formatInr(projectDetails.totalPaid)}", 305f, finY, textPaint)
        canvas.drawText("Balance Payable: ${CurrencyUtils.formatInr(projectDetails.balancePayable)}", 305f, finY + 13f, boldPaint)

        // Amount in Words
        val wordsY = boxTop + 150f
        boldPaint.color = Color.rgb(15, 23, 42)
        boldPaint.textSize = 8.5f
        canvas.drawText("Amount in Words: ${CurrencyUtils.numberToWordsInr(grandTotal)}", 30f, wordsY, boldPaint)

        // Terms and Conditions Box
        val termsY = wordsY + 14f
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRoundRect(30f, termsY, 565f, termsY + 65f, 4f, 4f, paint)
        boldPaint.textSize = 8f
        canvas.drawText("TERMS & CONDITIONS (नियम एवं शर्तें):", 38f, termsY + 12f, boldPaint)
        smallPaint.color = Color.rgb(71, 85, 105)
        smallPaint.textSize = 7.2f
        if (isLabourOnly) {
            canvas.drawText("• Note: All paints, primers, putty, and consumable materials are to be supplied by the customer at site.", 38f, termsY + 23f, smallPaint)
        } else {
            canvas.drawText("• Estimate covers standard surface preparation, primer, putty and paint materials specified above.", 38f, termsY + 23f, smallPaint)
        }
        canvas.drawText("• Payment Schedule: 50% advance before commencement, 40% on primer/putty completion, 10% on handover.", 38f, termsY + 34f, smallPaint)
        canvas.drawText("• Continuous water and uninterrupted electricity at site to be provided by client.", 38f, termsY + 45f, smallPaint)
        canvas.drawText("• Quotation valid for 30 days. Contact Abhimanyu Kumar, Kharadi, Pune | +91 98234 56789.", 38f, termsY + 56f, smallPaint)

        // Customer acceptance and signature space
        val sigY = termsY + 95f
        paint.color = Color.rgb(100, 116, 139)
        canvas.drawLine(40f, sigY, 210f, sigY, paint)
        canvas.drawLine(370f, sigY, 540f, sigY, paint)
        textPaint.textSize = 8.5f
        canvas.drawText("Customer Acceptance & Signature", 45f, sigY + 13f, textPaint)
        canvas.drawText("For DIVYANSH ENTERPRISES", 380f, sigY + 13f, textPaint)
        canvas.drawText("(Abhimanyu Kumar - Authorized Signatory)", 365f, sigY + 24f, smallPaint)

        pdfDocument.finishPage(page)

        // Write to cache file
        val outputDir = File(context.cacheDir, "quotations")
        if (!outputDir.exists()) outputDir.mkdirs()
        val file = File(outputDir, "Quotation_${projectDetails.project.estimateNumber}.pdf")
        val outputStream = FileOutputStream(file)
        pdfDocument.writeTo(outputStream)
        outputStream.close()
        pdfDocument.close()

        return file
    }

    fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun sharePdfViaWhatsApp(
        context: Context,
        file: File,
        customerMobile: String,
        summaryText: String
    ) {
        try {
            val uri = getFileUri(context, file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, summaryText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.whatsapp")
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val uri = getFileUri(context, file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, summaryText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Quotation PDF"))
        }
    }

    fun printPdf(context: Context, file: File, jobName: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Toast.makeText(context, "Print service unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        printManager.print(
            jobName,
            object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: android.os.Bundle?
                ) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onLayoutCancelled()
                        return
                    }
                    val info = PrintDocumentInfo.Builder(file.name)
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build()
                    callback?.onLayoutFinished(info, true)
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    var input: FileInputStream? = null
                    var output: FileOutputStream? = null
                    try {
                        input = FileInputStream(file)
                        output = FileOutputStream(destination?.fileDescriptor)
                        val buf = ByteArray(1024)
                        var bytesRead: Int
                        while (input.read(buf).also { bytesRead = it } > 0) {
                            if (cancellationSignal?.isCanceled == true) {
                                callback?.onWriteCancelled()
                                return
                            }
                            output.write(buf, 0, bytesRead)
                        }
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    } finally {
                        try {
                            input?.close()
                            output?.close()
                        } catch (_: IOException) {}
                    }
                }
            },
            PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
        )
    }
}
