package com.urlaunched.android.pdf.pdffromhtml

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintLayoutResultCallback
import android.print.PrintWriteResultCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import com.urlaunched.android.pdf.pdffromhtml.models.PdfProperties
import com.urlaunched.android.common.response.ErrorData
import com.urlaunched.android.common.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object PdfFromHtmlHelper {
    suspend fun generatePdf(
        context: Context,
        pdfProperties: PdfProperties = PdfProperties(),
        htmlString: String,
        outputUri: Uri,
        onSuccess: (file: File) -> Unit,
        onError: (message: String) -> Unit
    ) = try {
        val pdfTempFile = withContext(Dispatchers.Main) {
            createPdfFileFromHtml(
                context = context,
                htmlString = htmlString,
                pdfProperties = pdfProperties
            )
        }

        context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
            pdfTempFile.inputStream().use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        onSuccess(pdfTempFile)
    } catch (exception: Exception) {
        onError(exception.message.toString())
    }
}

private suspend fun createPdfFileFromHtml(
    context: Context,
    htmlString: String,
    pdfProperties: PdfProperties = PdfProperties()
) = suspendCancellableCoroutine<File> { continuation ->
    val webView = WebView(context)

    val outputPdfFile = File.createTempFile(
        /* prefix = */
        pdfProperties.documentName,
        /* suffix = */
        null,
        /* directory = */
        context.cacheDir
    ).apply {
        createNewFile()
    }
    val fileDescriptor = ParcelFileDescriptor.open(
        /* file = */
        outputPdfFile,
        /* mode = */
        ParcelFileDescriptor.MODE_TRUNCATE or ParcelFileDescriptor.MODE_READ_WRITE
    )
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            try {
                val documentAdapter = webView.createPrintDocumentAdapter(pdfProperties.documentName)
                documentAdapter.onLayout(
                    /* oldAttributes = */
                    null,
                    /* newAttributes = */
                    PrintAttributes
                        .Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .setResolution(
                            PrintAttributes.Resolution(
                                pdfProperties.resolutionId,
                                pdfProperties.resolutionId,
                                pdfProperties.printDpi,
                                pdfProperties.printDpi
                            )
                        )
                        .build(),
                    /* cancellationSignal = */
                    null,
                    /* callback = */
                    PrintLayoutResultCallback(),
                    /* extras = */
                    null
                )
                documentAdapter.onWrite(
                    /* pages = */
                    arrayOf(pdfProperties.convertPageRange),
                    /* destination = */
                    fileDescriptor,
                    /* cancellationSignal = */
                    null,
                    /* callback = */
                    object : PrintWriteResultCallback() {
                        override fun onWriteFinished(pages: Array<out PageRange>?) {
                            fileDescriptor.close()
                            webView.destroy()
                            continuation.resume(outputPdfFile)
                        }

                        override fun onWriteFailed(error: CharSequence?) {
                            fileDescriptor.close()
                            webView.destroy()
                            continuation.resumeWithException(Exception(error.toString()))
                        }
                    }
                )
            } catch (exception: Exception) {
                continuation.resumeWithException(exception)
            }
        }
    }

    webView.settings.allowFileAccess = true
    webView.loadDataWithBaseURL(
        /* baseUrl = */
        pdfProperties.baseUrl,
        /* data = */
        htmlString,
        /* mimeType = */
        pdfProperties.mimeType,
        /* encoding = */
        pdfProperties.encoding,
        /* historyUrl = */
        null
    )
}