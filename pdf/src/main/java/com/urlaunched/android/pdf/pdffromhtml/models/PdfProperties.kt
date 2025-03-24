package com.urlaunched.android.pdf.pdffromhtml.models

import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintAttributes.Margins

data class PdfProperties(
    val baseUrl: String = "",
    val mimeType: String = "text/HTML",
    val encoding: String = "utf-8",
    val documentName: String = "pdfResult",
    val resolutionId: String = "pdf",
    val printDpi: Int = 600,
    val convertPageRange: PageRange = PageRange(0, 1),
    val pdfSize: PrintAttributes.MediaSize = PrintAttributes.MediaSize.ISO_A4,
    val margins: Margins = Margins.NO_MARGINS
)