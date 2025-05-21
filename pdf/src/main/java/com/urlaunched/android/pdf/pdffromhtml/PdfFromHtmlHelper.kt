package com.urlaunched.android.pdf.pdffromhtml

import android.content.Context
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintLayoutResultCallback
import android.print.PrintWriteResultCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import com.urlaunched.android.pdf.pdffromhtml.models.PdfProperties
import java.io.File

object PdfFromHtmlHelper {
    fun createPdfFileFromHtml(
        context: Context,
        htmlString: String,
        pdfProperties: PdfProperties = PdfProperties(),
        onSuccess: (file: File) -> Unit,
        onError: (message: String) -> Unit
    ) {
        try {
            val webView = WebView(context)
            val pdfFile = File.createTempFile(
                pdfProperties.documentName,
                null,
                context.cacheDir
            ).apply {
                createNewFile()
            }

            val fileDescriptor = ParcelFileDescriptor.open(
                pdfFile,
                ParcelFileDescriptor.MODE_TRUNCATE or ParcelFileDescriptor.MODE_READ_WRITE
            )

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                        val documentAdapter = webView.createPrintDocumentAdapter(pdfProperties.documentName)
                        documentAdapter.onLayout(
                            null,
                            PrintAttributes.Builder()
                                .setMediaSize(pdfProperties.pdfSize)
                                .setMinMargins(pdfProperties.margins)
                                .setResolution(
                                    PrintAttributes.Resolution(
                                        pdfProperties.resolutionId,
                                        pdfProperties.resolutionId,
                                        pdfProperties.printDpi,
                                        pdfProperties.printDpi
                                    )
                                )
                                .build(),
                            null,
                            PrintLayoutResultCallback(),
                            null
                        )

                        documentAdapter.onWrite(
                            arrayOf(pdfProperties.convertPageRange),
                            fileDescriptor,
                            null,
                            object : PrintWriteResultCallback() {
                                override fun onWriteFinished(pages: Array<out PageRange>?) {
                                    fileDescriptor.close()
                                    webView.destroy()

                                    onSuccess(pdfFile)
                                }

                                override fun onWriteFailed(error: CharSequence?) {
                                    fileDescriptor.close()
                                    webView.destroy()
                                    onError(error.toString())
                                }
                            }
                        )
                    } catch (exception: Exception) {
                        onError(exception.message.toString())
                    }
                }
            }

            webView.settings.allowFileAccess = true
            webView.loadDataWithBaseURL(
                pdfProperties.baseUrl,
                htmlString,
                pdfProperties.mimeType,
                pdfProperties.encoding,
                null
            )
        } catch (exception: Exception) {
            onError(exception.message.toString())
        }
    }
}