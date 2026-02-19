package com.example.markdownreader.util

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient

object PdfExporter {

    fun export(context: Context, fileName: String, markdownContent: String) {
        val html = MarkdownToHtml.convert(markdownContent)
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                createPrintJob(context, webView, fileName)
            }
        }
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    private fun createPrintJob(context: Context, webView: WebView, fileName: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = fileName.removeSuffix(".md").removeSuffix(".markdown")
        val printAdapter = webView.createPrintDocumentAdapter(jobName)
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()
        printManager.print(jobName, printAdapter, printAttributes)
    }
}
