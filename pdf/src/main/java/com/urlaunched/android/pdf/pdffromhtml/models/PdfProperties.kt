package com.urlaunched.android.pdf.pdffromhtml.models

import android.print.PageRange

data class PdfProperties(
    val baseUrl: String = "",
    val mimeType: String = "text/HTML",
    val encoding: String = "utf-8",
    val documentName: String = "receipt",
    val resolutionId: String = "pdf",
    val printDpi: Int = 600,
    val convertPageRange: PageRange = PageRange(0, 1)
)