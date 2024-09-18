package com.urlaunched.android.common.pdf.pdffromlayout

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.utils.PdfMerger
import com.urlaunched.android.common.files.FileHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import com.itextpdf.kernel.pdf.PdfDocument as MultiPdfDocument

object PdfFromLayoutHelper {
    private fun createPdfPage(bitmap: Bitmap, pdfDocument: PdfDocument): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return page
    }

    fun mergePdfFiles(
        filePrefix: String,
        outputPath: String,
        pdfPaths: List<String>,
        context: Context,
        onSuccess: () -> Unit
    ) {
        if (pdfPaths.size == 1) {
            moveFileToSystemDownloads(
                path = pdfPaths.first(),
                filePrefix = filePrefix,
                onSuccess = {
                    onSuccess()
                    FileHelper.deleteTempFilesFromCache(
                        context = context
                    )
                }
            )
        } else {
            val outputFile = File(outputPath)
            val outputPdf = MultiPdfDocument(PdfWriter(FileOutputStream(outputFile)))
            val merger = PdfMerger(outputPdf)

            pdfPaths.forEach { pdfPath ->
                val inputPdf = MultiPdfDocument(PdfReader(pdfPath))
                merger.merge(inputPdf, 1, inputPdf.numberOfPages)
                inputPdf.close()
                File(pdfPath).delete()
            }

            merger.close()
            outputPdf.close()

            moveFileToSystemDownloads(
                path = outputPath,
                filePrefix = filePrefix,
                onSuccess = {
                    onSuccess()
                    FileHelper.deleteTempFilesFromCache(
                        context = context
                    )
                }
            )
        }
    }

    private fun moveFileToSystemDownloads(
        filePrefix: String,
        path: String,
        onSuccess: () -> Unit
    ) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val sourceFile = File(path)

        val timeStamp = System.currentTimeMillis()
        val fileName = "$filePrefix${"_"}$timeStamp$PDF_EXTENSION"
        val destinationFile = File(downloadsDir, fileName)

        val inputStream = FileInputStream(sourceFile)
        val outputStream = FileOutputStream(destinationFile)

        try {
            val buffer = ByteArray(1024 * 1000)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            sourceFile.delete()
            onSuccess()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream.close()
            outputStream.close()
        }
    }

    fun saveSinglePdfDocumentToFile(bitmap: Bitmap, path: String) {
        val pdfDocument = PdfDocument()

        val file = File(path)
        val outputStream = FileOutputStream(file)

        val page = createPdfPage(
            bitmap = bitmap,
            pdfDocument = pdfDocument
        )
        pdfDocument.finishPage(page)
        pdfDocument.writeTo(outputStream)

        outputStream.close()
        pdfDocument.close()

        bitmap.recycle()
    }

    private const val PDF_EXTENSION = ".pdf"
}