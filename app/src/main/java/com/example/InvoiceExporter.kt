package com.example

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object InvoiceExporter {

    fun exportToPdf(
        context: Context,
        clientName: String,
        serviceDescription: String,
        subtotal: Double,
        taxAmount: Double,
        total: Double
    ): Result<File> {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size 72 PPI
            val page = document.startPage(pageInfo)

            val canvas: Canvas = page.canvas
            val paint = Paint()

            // Draw header
            paint.isAntiAlias = true
            paint.textSize = 24f
            paint.color = Color.BLACK
            paint.isFakeBoldText = true
            canvas.drawText("FREELANCE INVOICE", 50f, 80f, paint)

            paint.textSize = 14f
            paint.isFakeBoldText = false
            canvas.drawText("Billed To: $clientName", 50f, 130f, paint)

            paint.color = Color.DKGRAY
            canvas.drawText("Description", 50f, 180f, paint)
            canvas.drawText("Amount", 450f, 180f, paint)

            paint.color = Color.BLACK
            // Draw lines for table
            canvas.drawLine(50f, 190f, 545f, 190f, paint)
            
            // Draw item
            canvas.drawText(serviceDescription, 50f, 220f, paint)
            canvas.drawText("$${String.format("%.2f", subtotal)}", 450f, 220f, paint)
            
            canvas.drawLine(50f, 240f, 545f, 240f, paint)

            // Draw totals
            canvas.drawText("Subtotal:", 350f, 270f, paint)
            canvas.drawText("$${String.format("%.2f", subtotal)}", 450f, 270f, paint)

            canvas.drawText("Tax (5%):", 350f, 300f, paint)
            canvas.drawText("$${String.format("%.2f", taxAmount)}", 450f, 300f, paint)

            paint.isFakeBoldText = true
            paint.textSize = 18f
            canvas.drawText("Total:", 350f, 340f, paint)
            canvas.drawText("$${String.format("%.2f", total)}", 450f, 340f, paint)

            document.finishPage(page)

            // Save the document
            val directory = File(context.externalCacheDir, "invoices")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = "Invoice_${System.currentTimeMillis()}.pdf"
            val file = File(directory, fileName)
            
            document.writeTo(FileOutputStream(file))
            document.close()

            Result.success(file)
        } catch (e: IOException) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
