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

    fun generateQuotationPdf(
        context: Context,
        projectDetails: ProjectWithDetails,
        isLabourOnly: Boolean
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (595 x 842 pt)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()
        val textPaint = Paint()
        val boldPaint = Paint()
        val smallPaint = Paint()
        val headerPaint = Paint()

        // Background
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, 595f, 842f, paint)

        // Header Banner - Deep Navy Blue
        headerPaint.color = Color.rgb(15, 43, 92) // #0F2B5C
        canvas.drawRect(0f, 0f, 595f, 75f, headerPaint)

        // Gold Accent Stripe
        paint.color = Color.rgb(217, 119, 6) // Amber/Gold #D97706
        canvas.drawRect(0f, 75f, 595f, 79f, paint)

        // Header Title
        titlePaint.color = Color.WHITE
        titlePaint.textSize = 20f
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DIVYANSH ENTERPRISES", 30f, 34f, titlePaint)

        smallPaint.color = Color.rgb(254, 243, 199)
        smallPaint.textSize = 10f
        canvas.drawText("Painting Contractor • Surface Protection & Decorative Finishes", 30f, 48f, smallPaint)
        canvas.drawText("Abhimanyu Kumar | Kharadi, Pune, Maharashtra | Ph: +91 98234 56789", 30f, 62f, smallPaint)

        // Estimate Type Badge
        val badgeTitle = if (isLabourOnly) "LABOUR-ONLY QUOTATION" else "PAINTING ESTIMATE & QUOTATION"
        boldPaint.color = Color.rgb(15, 43, 92)
        boldPaint.textSize = 13f
        boldPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(badgeTitle, 30f, 102f, boldPaint)

        // Date & Estimate No
        textPaint.color = Color.rgb(51, 65, 85)
        textPaint.textSize = 9.5f
        canvas.drawText("Estimate No: ${projectDetails.project.estimateNumber}", 400f, 96f, textPaint)
        canvas.drawText("Date: ${projectDetails.project.estimateDate}", 400f, 108f, textPaint)

        // Divider
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawLine(30f, 116f, 565f, 116f, paint)

        // Client & Site Details Box
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRoundRect(30f, 122f, 565f, 185f, 6f, 6f, paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(203, 213, 225)
        canvas.drawRoundRect(30f, 122f, 565f, 185f, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        boldPaint.textSize = 9.5f
        canvas.drawText("CLIENT DETAILS:", 40f, 137f, boldPaint)
        textPaint.textSize = 9f
        canvas.drawText("Name: ${projectDetails.project.customerName}", 40f, 151f, textPaint)
        canvas.drawText("Mobile: ${projectDetails.project.mobileNumber}", 40f, 164f, textPaint)
        canvas.drawText("Flat/Property: ${projectDetails.project.flatType}", 40f, 177f, textPaint)

        canvas.drawText("SITE DETAILS:", 300f, 137f, boldPaint)
        canvas.drawText("Site: ${projectDetails.project.siteName}", 300f, 151f, textPaint)
        val siteAddr = if (projectDetails.project.siteAddress.length > 40) {
            projectDetails.project.siteAddress.take(38) + "..."
        } else {
            projectDetails.project.siteAddress
        }
        canvas.drawText("Address: $siteAddr", 300f, 164f, textPaint)
        canvas.drawText("Deduct Openings: ${if (projectDetails.project.deductOpenings) "Yes (Net Wall Area)" else "No (Gross Area)"}", 300f, 177f, textPaint)

        // Table Header
        var yPos = 205f
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRect(30f, yPos, 565f, yPos + 18f, paint)

        boldPaint.color = Color.rgb(30, 41, 59)
        boldPaint.textSize = 8.5f
        canvas.drawText("SR", 35f, yPos + 13f, boldPaint)
        canvas.drawText("WORK SPECIFICATIONS & REMARKS", 55f, yPos + 13f, boldPaint)
        canvas.drawText("QTY & UNIT", 295f, yPos + 13f, boldPaint)
        if (!isLabourOnly) {
            canvas.drawText("MAT. RATE", 365f, yPos + 13f, boldPaint)
            canvas.drawText("LAB. RATE", 435f, yPos + 13f, boldPaint)
            canvas.drawText("TOTAL (₹)", 505f, yPos + 13f, boldPaint)
        } else {
            canvas.drawText("LAB. RATE", 415f, yPos + 13f, boldPaint)
            canvas.drawText("AMOUNT (₹)", 495f, yPos + 13f, boldPaint)
        }

        yPos += 22f
        textPaint.textSize = 8.5f
        textPaint.color = Color.rgb(15, 23, 42)

        val productPaint = Paint().apply {
            color = Color.rgb(29, 78, 216) // Blue 700
            textSize = 7.3f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val remarksPaint = Paint().apply {
            color = Color.rgb(180, 83, 9) // Amber 700
            textSize = 7.0f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }

        val items = projectDetails.estimateItems
        items.take(12).forEachIndexed { index, item ->
            val desc = if (item.workDescription.length > 38) item.workDescription.take(36) + "..." else item.workDescription
            canvas.drawText("${index + 1}", 35f, yPos, textPaint)
            canvas.drawText(desc, 55f, yPos, boldPaint)
            canvas.drawText("${item.quantity.toInt()} ${item.unit}", 295f, yPos, textPaint)

            if (!isLabourOnly) {
                canvas.drawText("₹${item.materialRate}", 365f, yPos, textPaint)
                canvas.drawText("₹${item.labourRate}", 435f, yPos, textPaint)
                canvas.drawText(CurrencyUtils.formatInr(item.totalAmountMaterialAndLabour), 505f, yPos, textPaint)
            } else {
                canvas.drawText("₹${item.labourRate}", 415f, yPos, textPaint)
                canvas.drawText(CurrencyUtils.formatInr(item.totalAmountLabourOnly), 495f, yPos, textPaint)
            }

            if (item.hasProductDetails) {
                yPos += 10f
                val brandPart = if (item.brandName.isNotBlank()) "Brand: ${item.brandName}" else ""
                val prodPart = if (item.productName.isNotBlank()) "Product: ${item.productName}" else ""
                val varPart = if (item.productVariant.isNotBlank()) "(${item.productVariant})" else ""
                val finishPart = if (item.finish.isNotBlank()) "Finish: ${item.finish}" else ""
                val coatPart = "${item.coats} Coats"
                val specs = listOf(brandPart, "$prodPart $varPart".trim(), finishPart, coatPart).filter { it.isNotBlank() }.joinToString(" • ")
                val truncatedSpecs = if (specs.length > 74) specs.take(72) + "..." else specs
                canvas.drawText("  ▸ $truncatedSpecs", 55f, yPos, productPaint)
            }

            if (item.remarks.isNotBlank()) {
                yPos += 9.5f
                val remText = "Remarks: ${item.remarks}"
                val truncatedRem = if (remText.length > 76) remText.take(74) + "..." else remText
                canvas.drawText("  • $truncatedRem", 55f, yPos, remarksPaint)
            }

            paint.color = Color.rgb(241, 245, 249)
            canvas.drawLine(30f, yPos + 4f, 565f, yPos + 4f, paint)
            yPos += 14f
        }

        // Measurement Summary Snippet (Rooms)
        if (projectDetails.rooms.isNotEmpty() && yPos < 460f) {
            yPos += 10f
            paint.color = Color.rgb(254, 243, 199)
            canvas.drawRect(30f, yPos, 565f, yPos + 16f, paint)
            boldPaint.color = Color.rgb(120, 53, 15)
            boldPaint.textSize = 8.5f
            canvas.drawText("MEASUREMENT SUMMARY: ${projectDetails.rooms.size} Rooms • Net Wall: ${CurrencyUtils.formatSqft(projectDetails.totalRoomNetWallAreaSqft)} • Ceiling: ${CurrencyUtils.formatSqft(projectDetails.totalRoomCeilingAreaSqft)} • Total: ${CurrencyUtils.formatSqft(projectDetails.combinedPaintableSqft)}", 35f, yPos + 12f, boldPaint)
            yPos += 24f
        }

        // Financial Calculation Summary Box
        val boxTop = 500f.coerceAtLeast(yPos + 15f)
        paint.color = Color.rgb(248, 250, 252)
        canvas.drawRoundRect(300f, boxTop, 565f, boxTop + 130f, 6f, 6f, paint)
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(203, 213, 225)
        canvas.drawRoundRect(300f, boxTop, 565f, boxTop + 130f, 6f, 6f, paint)
        paint.style = Paint.Style.FILL

        var finY = boxTop + 18f
        textPaint.textSize = 9f
        boldPaint.textSize = 9f

        val subtotal = if (isLabourOnly) projectDetails.labourSubtotal else projectDetails.materialAndLabourSubtotal
        canvas.drawText("Subtotal:", 315f, finY, textPaint)
        canvas.drawText(CurrencyUtils.formatInr(subtotal), 485f, finY, boldPaint)
        finY += 16f

        if (projectDetails.project.additionalCharges > 0) {
            canvas.drawText("Additional Charges:", 315f, finY, textPaint)
            canvas.drawText(CurrencyUtils.formatInr(projectDetails.project.additionalCharges), 485f, finY, boldPaint)
            finY += 16f
        }

        if (projectDetails.project.discountAmount > 0) {
            canvas.drawText("Discount:", 315f, finY, textPaint)
            canvas.drawText("-${CurrencyUtils.formatInr(projectDetails.project.discountAmount)}", 485f, finY, boldPaint)
            finY += 16f
        }

        if (projectDetails.project.isTaxEnabled) {
            val tax = if (isLabourOnly) projectDetails.labourTaxAmount else projectDetails.materialAndLabourTaxAmount
            canvas.drawText("GST (${projectDetails.project.taxPercent}%):", 315f, finY, textPaint)
            canvas.drawText(CurrencyUtils.formatInr(tax), 485f, finY, boldPaint)
            finY += 16f
        }

        val grandTotal = if (isLabourOnly) projectDetails.labourGrandTotal else projectDetails.materialAndLabourGrandTotal
        paint.color = Color.rgb(15, 43, 92)
        canvas.drawRect(300f, finY - 2f, 565f, finY + 22f, paint)
        boldPaint.color = Color.WHITE
        boldPaint.textSize = 10.5f
        canvas.drawText("GRAND TOTAL:", 315f, finY + 14f, boldPaint)
        canvas.drawText(CurrencyUtils.formatInr(grandTotal), 475f, finY + 14f, boldPaint)
        finY += 34f

        canvas.drawText("Advance Received: ${CurrencyUtils.formatInr(projectDetails.totalPaid)}", 315f, finY, textPaint)
        canvas.drawText("Balance Payable: ${CurrencyUtils.formatInr(projectDetails.balancePayable)}", 315f, finY + 14f, boldPaint)

        // Amount in Words
        val wordsY = boxTop + 140f
        boldPaint.color = Color.rgb(15, 23, 42)
        boldPaint.textSize = 8.5f
        canvas.drawText("Amount in Words: ${CurrencyUtils.numberToWordsInr(grandTotal)}", 30f, wordsY, boldPaint)

        // Terms and Conditions Box
        val termsY = wordsY + 16f
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRoundRect(30f, termsY, 565f, termsY + 68f, 4f, 4f, paint)
        boldPaint.textSize = 8f
        canvas.drawText("TERMS & CONDITIONS:", 38f, termsY + 13f, boldPaint)
        smallPaint.color = Color.rgb(71, 85, 105)
        smallPaint.textSize = 7.2f
        if (isLabourOnly) {
            canvas.drawText("• Note: All paints, primers, putty, and consumable materials are to be supplied by the customer at site.", 38f, termsY + 25f, smallPaint)
        } else {
            canvas.drawText("• Estimate covers standard surface preparation, primer, putty and paint materials specified above.", 38f, termsY + 25f, smallPaint)
        }
        canvas.drawText("• Payment Schedule: 50% advance before commencement, 40% on primer/putty completion, 10% on handover.", 38f, termsY + 36f, smallPaint)
        canvas.drawText("• Continuous water and uninterrupted electricity at site to be provided by client.", 38f, termsY + 47f, smallPaint)
        canvas.drawText("• Quotation valid for 30 days from date of issue. Rates applicable for Kharadi & Pune region.", 38f, termsY + 58f, smallPaint)

        // Signature Blocks
        val sigY = termsY + 98f
        paint.color = Color.rgb(100, 116, 139)
        canvas.drawLine(40f, sigY, 200f, sigY, paint)
        canvas.drawLine(380f, sigY, 540f, sigY, paint)
        textPaint.textSize = 8.5f
        canvas.drawText("Customer Acceptance & Signature", 45f, sigY + 14f, textPaint)
        canvas.drawText("For DIVYANSH ENTERPRISES", 390f, sigY + 14f, textPaint)
        canvas.drawText("(Abhimanyu Kumar - Authorized Signatory)", 375f, sigY + 26f, smallPaint)

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
                // If customer mobile provided, try targeting WhatsApp
                setPackage("com.whatsapp")
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to standard Android Sharesheet
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
