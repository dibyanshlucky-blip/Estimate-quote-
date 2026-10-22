package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.CatalogueItemEntity
import com.example.data.model.DirectMeasurementEntity
import com.example.data.model.EstimateItemEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectWithDetails
import com.example.data.model.RoomMeasurementEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object BackupUtils {

    fun exportToJson(
        projects: List<ProjectEntity>,
        rooms: List<RoomMeasurementEntity>,
        directs: List<DirectMeasurementEntity>,
        estimates: List<EstimateItemEntity>,
        payments: List<PaymentEntity>,
        catalogue: List<CatalogueItemEntity>
    ): String {
        val root = JSONObject()
        root.put("app", "Divyansh Enterprises Painting App")
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        val projectsArr = JSONArray()
        projects.forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("customerName", p.customerName)
            obj.put("mobileNumber", p.mobileNumber)
            obj.put("siteName", p.siteName)
            obj.put("siteAddress", p.siteAddress)
            obj.put("flatType", p.flatType)
            obj.put("estimateNumber", p.estimateNumber)
            obj.put("estimateDate", p.estimateDate)
            obj.put("siteNotes", p.siteNotes)
            obj.put("customerRequirements", p.customerRequirements)
            obj.put("status", p.status)
            obj.put("estimateMode", p.estimateMode)
            obj.put("deductOpenings", p.deductOpenings)
            obj.put("additionalCharges", p.additionalCharges)
            obj.put("discountAmount", p.discountAmount)
            obj.put("isTaxEnabled", p.isTaxEnabled)
            obj.put("taxPercent", p.taxPercent)
            projectsArr.put(obj)
        }
        root.put("projects", projectsArr)

        val roomsArr = JSONArray()
        rooms.forEach { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("projectId", r.projectId)
            obj.put("roomName", r.roomName)
            obj.put("lengthFt", r.lengthFt)
            obj.put("lengthIn", r.lengthIn)
            obj.put("widthFt", r.widthFt)
            obj.put("widthIn", r.widthIn)
            obj.put("heightFt", r.heightFt)
            obj.put("heightIn", r.heightIn)
            obj.put("doorCount", r.doorCount)
            obj.put("doorWidthFt", r.doorWidthFt)
            obj.put("doorHeightFt", r.doorHeightFt)
            obj.put("windowCount", r.windowCount)
            obj.put("windowWidthFt", r.windowWidthFt)
            obj.put("windowHeightFt", r.windowHeightFt)
            obj.put("additionalDeductionsSqft", r.additionalDeductionsSqft)
            obj.put("wallPaintingEnabled", r.wallPaintingEnabled)
            obj.put("ceilingPaintingEnabled", r.ceilingPaintingEnabled)
            obj.put("notes", r.notes)
            roomsArr.put(obj)
        }
        root.put("rooms", roomsArr)

        val directArr = JSONArray()
        directs.forEach { d ->
            val obj = JSONObject()
            obj.put("id", d.id)
            obj.put("projectId", d.projectId)
            obj.put("surfaceName", d.surfaceName)
            obj.put("quantity", d.quantity)
            obj.put("unit", d.unit)
            obj.put("notes", d.notes)
            directArr.put(obj)
        }
        root.put("directMeasurements", directArr)

        val estimatesArr = JSONArray()
        estimates.forEach { e ->
            val obj = JSONObject()
            obj.put("id", e.id)
            obj.put("projectId", e.projectId)
            obj.put("category", e.category)
            obj.put("workDescription", e.workDescription)
            obj.put("brandName", e.brandName)
            obj.put("productName", e.productName)
            obj.put("productVariant", e.productVariant)
            obj.put("finish", e.finish)
            obj.put("surfaceType", e.surfaceType)
            obj.put("quantity", e.quantity)
            obj.put("unit", e.unit)
            obj.put("materialRate", e.materialRate)
            obj.put("labourRate", e.labourRate)
            obj.put("coats", e.coats)
            obj.put("remarks", e.remarks)
            estimatesArr.put(obj)
        }
        root.put("estimates", estimatesArr)

        val paymentsArr = JSONArray()
        payments.forEach { pay ->
            val obj = JSONObject()
            obj.put("id", pay.id)
            obj.put("projectId", pay.projectId)
            obj.put("amount", pay.amount)
            obj.put("paymentDate", pay.paymentDate)
            obj.put("paymentMethod", pay.paymentMethod)
            obj.put("referenceNumber", pay.referenceNumber)
            obj.put("remarks", pay.remarks)
            paymentsArr.put(obj)
        }
        root.put("payments", paymentsArr)

        val catArr = JSONArray()
        catalogue.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("category", c.category)
            obj.put("titleEn", c.titleEn)
            obj.put("titleHi", c.titleHi)
            obj.put("brandProduct", c.brandProduct)
            obj.put("surfaceType", c.surfaceType)
            obj.put("unit", c.unit)
            obj.put("materialRate", c.materialRate)
            obj.put("labourRate", c.labourRate)
            obj.put("coats", c.coats)
            obj.put("remarks", c.remarks)
            catArr.put(obj)
        }
        root.put("catalogue", catArr)

        return root.toString(2)
    }

    fun exportToCsv(projectDetails: ProjectWithDetails): String {
        val sb = StringBuilder()
        sb.append("Divyansh Enterprises - Painting Estimate\n")
        sb.append("Estimate No,${projectDetails.project.estimateNumber}\n")
        sb.append("Customer,${projectDetails.project.customerName}\n")
        sb.append("Mobile,${projectDetails.project.mobileNumber}\n")
        sb.append("Site,${projectDetails.project.siteName}\n")
        sb.append("Date,${projectDetails.project.estimateDate}\n\n")

        sb.append("ROOM MEASUREMENTS\n")
        sb.append("Room,Length(ft),Width(ft),Height(ft),Net Wall Area(sqft),Ceiling Area(sqft),Paintable Area(sqft)\n")
        projectDetails.rooms.forEach { r ->
            sb.append("\"${r.roomName}\",")
            sb.append("${r.effectiveLength},")
            sb.append("${r.effectiveWidth},")
            sb.append("${r.effectiveHeight},")
            sb.append("${r.getNetWallAreaSqft(projectDetails.project.deductOpenings)},")
            sb.append("${r.floorCeilingAreaSqft},")
            sb.append("${r.getTotalPaintableAreaSqft(projectDetails.project.deductOpenings)}\n")
        }

        sb.append("\nESTIMATE ITEMS\n")
        sb.append("Category,Work Description,Brand,Product,Variant,Finish,Coats,Remarks,Quantity,Unit,Material Rate,Labour Rate,Combined Rate,Material Total,Labour Total,Grand Total\n")
        projectDetails.estimateItems.forEach { i ->
            sb.append("\"${i.category}\",")
            sb.append("\"${i.workDescription}\",")
            sb.append("\"${i.brandName}\",")
            sb.append("\"${i.productName}\",")
            sb.append("\"${i.productVariant}\",")
            sb.append("\"${i.finish}\",")
            sb.append("${i.coats},")
            sb.append("\"${i.remarks}\",")
            sb.append("${i.quantity},")
            sb.append("${i.unit},")
            sb.append("${i.materialRate},")
            sb.append("${i.labourRate},")
            sb.append("${i.combinedRate},")
            sb.append("${i.materialAmount},")
            sb.append("${i.labourAmount},")
            sb.append("${i.totalAmountMaterialAndLabour}\n")
        }

        if (projectDetails.woodPolishItems.isNotEmpty()) {
            sb.append("\nWOODWORK & POLISH MEASUREMENTS\n")
            sb.append("Category,Item Name,Location,Type,Polish Type,Brand,Qty,Sides,Height(ft),Width(ft),Gross Area(sqft),Deduction(sqft),Multiplier,Plus,Minus,Net Area(sqft),Labour Rate,Material Rate,Total Amount\n")
            projectDetails.woodPolishItems.forEach { w ->
                sb.append("\"${w.category}\",\"${w.itemName}\",\"${w.roomName}\",\"${w.itemType}\",\"${w.polishType}\",\"${w.polishBrand}\",${w.quantity},${w.polishSides},${w.effectiveHeightFt},${w.effectiveWidthFt},${w.grossAreaSqft},${w.deductionAreaSqft},${w.multiplier},${w.plusAdjustment},${w.minusAdjustment},${w.finalBillableAreaSqft},${w.labourRate},${w.materialRate},${w.totalAmount}\n")
            }
        }

        sb.append("\nFINANCIAL SUMMARY\n")
        sb.append("Labour Subtotal,${projectDetails.labourSubtotal}\n")
        sb.append("Material Subtotal,${projectDetails.materialSubtotal}\n")
        sb.append("Combined Subtotal,${projectDetails.materialAndLabourSubtotal}\n")
        sb.append("Additional Charges,${projectDetails.project.additionalCharges}\n")
        sb.append("Discount,${projectDetails.project.discountAmount}\n")
        sb.append("Grand Total,${projectDetails.currentGrandTotal}\n")
        sb.append("Total Paid,${projectDetails.totalPaid}\n")
        sb.append("Balance Payable,${projectDetails.balancePayable}\n")

        return sb.toString()
    }

    fun shareExportFile(context: Context, content: String, fileName: String, mimeType: String) {
        val file = File(context.cacheDir, fileName)
        val fos = FileOutputStream(file)
        fos.write(content.toByteArray())
        fos.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share $fileName"))
    }
}
