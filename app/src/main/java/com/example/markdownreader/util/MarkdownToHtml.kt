package com.example.markdownreader.util

import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

object MarkdownToHtml {
    private val extensions = listOf(TablesExtension.create())
    private val parser = Parser.builder().extensions(extensions).build()
    private val renderer = HtmlRenderer.builder().extensions(extensions).build()

    fun convert(markdown: String): String {
        val document = parser.parse(markdown)
        val htmlBody = renderer.render(document)

        return """
        <!DOCTYPE html>
        <html>
        <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                line-height: 1.6;
                padding: 20px;
                color: #1a1a1a;
                background: #ffffff;
                max-width: 800px;
                margin: 0 auto;
            }
            h1, h2, h3, h4, h5, h6 {
                margin-top: 1.5em;
                margin-bottom: 0.5em;
                font-weight: 600;
            }
            h1 { font-size: 2em; border-bottom: 1px solid #eee; padding-bottom: 0.3em; }
            h2 { font-size: 1.5em; border-bottom: 1px solid #eee; padding-bottom: 0.3em; }
            code {
                background: #f4f4f4;
                padding: 2px 6px;
                border-radius: 4px;
                font-size: 0.9em;
                font-family: 'SF Mono', Monaco, Consolas, monospace;
            }
            pre {
                background: #f4f4f4;
                padding: 16px;
                border-radius: 8px;
                overflow-x: auto;
            }
            pre code { padding: 0; background: none; }
            blockquote {
                border-left: 4px solid #ddd;
                margin-left: 0;
                padding-left: 16px;
                color: #666;
            }
            table { border-collapse: collapse; width: 100%; }
            th, td {
                border: 1px solid #ddd;
                padding: 8px 12px;
                text-align: left;
            }
            th { background: #f0f0f0; font-weight: 600; }
            img { max-width: 100%; height: auto; }
            hr { border: none; border-top: 1px solid #ddd; margin: 2em 0; }
            a { color: #006c4c; }
            ul, ol { padding-left: 2em; }
            li { margin-bottom: 0.3em; }
        </style>
        </head>
        <body>
        $htmlBody
        </body>
        </html>
        """.trimIndent()
    }
}
