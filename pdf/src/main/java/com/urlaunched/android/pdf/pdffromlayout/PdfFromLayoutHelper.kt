package com.urlaunched.android.pdf.pdffromlayout

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.view.View
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.utils.PdfMerger
import com.urlaunched.android.common.files.FileHelper
import com.urlaunched.android.pdf.view.createBitmapFromView
import java.io.File
import java.io.FileOutputStream
import com.itextpdf.kernel.pdf.PdfDocument as MultiPdfDocument

object PdfFromLayoutHelper {
    /**
     * Creates a PDF document with multiple pages generated from views.
     *
     * @param context The context used to access the file system and resources.
     * @param pageCount The number of pages to create.
     * @param outputPath The file path where the final PDF document will be saved.
     * @param containerWidth The width of the container for rendering the view.
     * @param containerHeight The height of the container for rendering the view.
     * @param initView A lambda function that initializes and returns the view for a given page index.
     * @param onPageCreate A callback triggered after each page is created (optional).
     * @param onPdfDocCreate A callback triggered after the PDF document is successfully created (optional).
     */
    fun createPdfDocument(
        context: Context,
        pageCount: Int,
        outputPath: String,
        containerWidth: Int,
        containerHeight: Int,
        initView: (itemIndex: Int) -> View,
        onPageCreate: () -> Unit = {},
        onPdfDocCreate: () -> Unit = {}
    ) {
        val pdfPaths = mutableListOf<String>()

        repeat(pageCount) { itemIndex ->
            val view = initView(itemIndex)

            val bitmap = createBitmapFromView(view, containerWidth, containerHeight)

            val onePagePdfPath = context.cacheDir.path + "/pdf_page_%d.pdf".format(itemIndex)

            saveSinglePdfDocumentToFile(
                bitmap = bitmap,
                path = onePagePdfPath
            )

            pdfPaths.add(onePagePdfPath)

            onPageCreate()
        }

        mergePdfFiles(
            outputPath = outputPath,
            pdfPaths = pdfPaths,
            context = context,
            onSuccess = onPdfDocCreate
        )
    }

    private fun createPdfPage(bitmap: Bitmap, pdfDocument: PdfDocument): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return page
    }

    private fun mergePdfFiles(
        outputPath: String,
        pdfPaths: List<String>,
        context: Context,
        onSuccess: () -> Unit
    ) {
        if (pdfPaths.size == 1) {
            onSuccess()
            FileHelper.deleteTempFilesFromCache(
                context = context
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

            onSuccess()
            FileHelper.deleteTempFilesFromCache(
                context = context
            )
        }
    }

    private fun saveSinglePdfDocumentToFile(bitmap: Bitmap, path: String) {
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
}