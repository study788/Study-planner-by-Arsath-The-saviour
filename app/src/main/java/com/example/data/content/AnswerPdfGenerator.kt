package com.example.data.content

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.model.QuestionItem
import com.example.data.model.TestAttempt
import java.io.File
import java.io.FileOutputStream

object AnswerPdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f
    private const val CONTENT_WIDTH = (PAGE_WIDTH - 2 * MARGIN).toInt()

    // Distinct rich ink color per Question
    val questionColors: List<Int> = listOf(
        Color.rgb(30, 58, 138),   // Deep Cobalt
        Color.rgb(109, 40, 217),  // Royal Purple
        Color.rgb(15, 118, 110),  // Deep Teal
        Color.rgb(154, 52, 18),   // Burnt Terracotta
        Color.rgb(157, 23, 77),   // Berry Magenta
        Color.rgb(29, 78, 216),   // Sapphire
        Color.rgb(4, 120, 87),    // Forest Jade
        Color.rgb(124, 45, 18),   // Chestnut
        Color.rgb(67, 56, 202),   // Indigo
        Color.rgb(180, 83, 9)     // Rich Amber
    )

    // Distinct contrasting color per Answer & Explanation
    val answerColors: List<Int> = listOf(
        Color.rgb(6, 95, 70),     // Emerald Ink
        Color.rgb(185, 28, 28),   // Crimson Ink
        Color.rgb(3, 105, 161),   // Ocean Cyan Ink
        Color.rgb(126, 34, 206),  // Amethyst Ink
        Color.rgb(180, 83, 9),    // Ochre Ink
        Color.rgb(21, 128, 61),   // Shamrock Ink
        Color.rgb(190, 18, 60),   // Rose Red Ink
        Color.rgb(30, 64, 175),   // Cobalt Ink
        Color.rgb(134, 25, 143),  // Plum Ink
        Color.rgb(194, 65, 12)    // Tangerine Ink
    )

    fun generateHomeworkAnswerPdf(
        context: Context,
        exam: String,
        dateKey: String,
        studentName: String,
        questions: List<QuestionItem>
    ): File {
        val outDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val file = File(outDir, "Arsath_AnswerKey_${exam}_${dateKey}.pdf")

        val nativeResult = runCatching {
            val document = PdfDocument()
            val symbolBitmap = runCatching {
                BitmapFactory.decodeResource(context.resources, R.drawable.app_symbol)
            }.getOrNull()

            var pageNumber = 1
            var page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
            var canvas = page.canvas

            drawPageFrame(canvas, pageNumber)
            var y = drawInstituteHeader(
                canvas = canvas,
                symbolBitmap = symbolBitmap,
                title = "ARSATH COACHING CENTRE",
                subtitle = "Daily Tough Competitive Homework — Official Answer Key ($exam)",
                meta = "Date: $dateKey  |  Student: $studentName  |  Created by Mohammed Arsath H"
            )

            val dottedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(100, 116, 139)
                strokeWidth = 1.4f
                style = Paint.Style.STROKE
                pathEffect = DashPathEffect(floatArrayOf(6f, 5f), 0f)
            }

            val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
            val optionLetters = listOf("A", "B", "C", "D")

            questions.forEachIndexed { idx, q ->
                val qColor = questionColors[idx % questionColors.size]
                val aColor = answerColors[idx % answerColors.size]

                val qTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = qColor
                    textSize = 11.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                val aTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = aColor
                    textSize = 10.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                }
                val aBoldPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = aColor
                    textSize = 11f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }

                val qTitle = "Q${idx + 1}. [${q.subject} · ${q.chapter} · ${q.difficulty}]\n${q.questionText}"
                val qLayout = StaticLayout.Builder.obtain(qTitle, 0, qTitle.length, qTextPaint, CONTENT_WIDTH - 16)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(2f, 1.12f)
                    .build()

                val ansLetter = optionLetters.getOrElse(q.correctIndex) { "A" }
                val ansValue = q.options.getOrElse(q.correctIndex) { "" }
                val ansHeader = "Answer (${ansLetter}): $ansValue"
                val ansHeaderLayout = StaticLayout.Builder.obtain(ansHeader, 0, ansHeader.length, aBoldPaint, CONTENT_WIDTH - 16)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(1.5f, 1.1f)
                    .build()

                val solText = "Solution: ${q.explanation}"
                val solLayout = StaticLayout.Builder.obtain(solText, 0, solText.length, aTextPaint, CONTENT_WIDTH - 16)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(1.5f, 1.1f)
                    .build()

                val blockHeight = qLayout.height + ansHeaderLayout.height + solLayout.height + 34f
                if (y + blockHeight > PAGE_HEIGHT - MARGIN - 24f) {
                    document.finishPage(page)
                    pageNumber++
                    page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
                    canvas = page.canvas
                    drawPageFrame(canvas, pageNumber)
                    y = drawContinuationHeader(canvas, symbolBitmap, exam, dateKey, pageNumber)
                }

                badgePaint.color = qColor
                canvas.drawRect(MARGIN, y + 2f, MARGIN + 4f, y + qLayout.height + 2f, badgePaint)

                canvas.save()
                canvas.translate(MARGIN + 10f, y)
                qLayout.draw(canvas)
                canvas.restore()
                y += qLayout.height + 6f

                badgePaint.color = aColor
                canvas.drawRect(MARGIN, y + 2f, MARGIN + 4f, y + ansHeaderLayout.height + solLayout.height + 4f, badgePaint)

                canvas.save()
                canvas.translate(MARGIN + 10f, y)
                ansHeaderLayout.draw(canvas)
                canvas.restore()
                y += ansHeaderLayout.height + 3f

                canvas.save()
                canvas.translate(MARGIN + 10f, y)
                solLayout.draw(canvas)
                canvas.restore()
                y += solLayout.height + 10f

                canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, dottedPaint)
                y += 14f
            }

            document.finishPage(page)
            FileOutputStream(file).use { out -> document.writeTo(out) }
            document.close()
        }

        if (nativeResult.isFailure) {
            writePureKotlinFallbackPdf(file, exam, dateKey, studentName, questions)
        }
        return file
    }

    private fun writePureKotlinFallbackPdf(
        file: File,
        exam: String,
        dateKey: String,
        studentName: String,
        questions: List<QuestionItem>
    ) {
        val optionLetters = listOf("A", "B", "C", "D")
        val contentLines = buildString {
            append("BT /F1 12 Tf 40 800 Td (ARSATH COACHING CENTRE - $exam Answer Key [$dateKey]) Tj ET\n")
            append("BT /F1 10 Tf 40 784 Td (Student: $studentName | Created by Mohammed Arsath H) Tj ET\n")
            var y = 760
            questions.take(10).forEachIndexed { idx, q ->
                val ansL = optionLetters.getOrElse(q.correctIndex) { "A" }
                val qClean = q.questionText.replace("(", "[").replace(")", "]").take(75)
                val aClean = "Ans ($ansL): ${q.options.getOrElse(q.correctIndex) { "" }}".replace("(", "[").replace(")", "]").take(75)
                append("BT /F1 10 Tf 40 $y Td (Q${idx + 1}. $qClean) Tj ET\n")
                y -= 14
                append("BT /F1 10 Tf 40 $y Td ($aClean) Tj ET\n")
                y -= 18
            }
        }
        val streamBytes = contentLines.toByteArray(Charsets.ISO_8859_1)
        val pdfText = buildString {
            append("%PDF-1.4\n")
            append("1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n")
            append("2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n")
            append("3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >> endobj\n")
            append("4 0 obj << /Length ${streamBytes.size} >> stream\n")
            append(contentLines)
            append("\nendstream endobj\n")
            append("5 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj\n")
            append("xref\n0 6\n0000000000 65535 f \ntrailer << /Size 6 /Root 1 0 R >>\nstartxref\n0\n%%EOF")
        }
        file.writeBytes(pdfText.toByteArray(Charsets.ISO_8859_1))
    }

    fun generateProgressReportPdf(
        context: Context,
        studentName: String,
        exam: String,
        planLabel: String,
        attempts: List<TestAttempt>
    ): File {
        val outDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val safeName = studentName.replace(Regex("[^A-Za-z0-9]"), "_").ifBlank { "Student" }
        val file = File(outDir, "Arsath_ProgressReport_${safeName}.pdf")
        val document = PdfDocument()
        val symbolBitmap = runCatching {
            BitmapFactory.decodeResource(context.resources, R.drawable.app_symbol)
        }.getOrNull()

        var pageNum = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create())
        var canvas = page.canvas
        drawPageFrame(canvas, pageNum)
        var y = drawInstituteHeader(
            canvas,
            symbolBitmap,
            "ARSATH COACHING CENTRE",
            "Official Student Test & Exam Question Report Card",
            "Student: $studentName  |  Track: $exam ($planLabel)  |  Creator: Mohammed Arsath H"
        )

        val dottedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(148, 163, 184)
            strokeWidth = 1.2f
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(5f, 4f), 0f)
        }

        if (attempts.isEmpty()) {
            val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(30, 41, 59)
                textSize = 12f
            }
            canvas.drawText("No tests completed yet. Attempt a Daily, Weekly, or Mock test to record results.", MARGIN + 6f, y + 18f, tp)
        } else {
            attempts.take(25).forEachIndexed { idx, att ->
                val qColor = questionColors[idx % questionColors.size]
                val aColor = answerColors[idx % answerColors.size]
                val headerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = qColor
                    textSize = 11.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                val detailPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = aColor
                    textSize = 10.5f
                }

                val summary = "${idx + 1}. ${att.title} (${att.exam} · ${att.dateLabel}) — Score: ${att.score}/${att.total} (${att.pct}%, Grade ${att.grade})"
                val summaryLayout = StaticLayout.Builder.obtain(summary, 0, summary.length, headerPaint, CONTENT_WIDTH - 12).build()

                val qDetails = att.questions.take(6).mapIndexed { qIdx, qMap ->
                    val stem = (qMap["questionText"] as? String).orEmpty().take(90)
                    val chosen = (qMap["selectedOption"] as? String).orEmpty()
                    val correct = (qMap["correctOption"] as? String).orEmpty()
                    val ok = (qMap["isCorrect"] as? Boolean) ?: false
                    "  Q${qIdx + 1}: $stem | Yours: ${chosen.ifBlank { "Skipped" }} | Correct: $correct [${if (ok) "RIGHT" else "WRONG"}]"
                }.joinToString("\n")
                val qLayout = if (qDetails.isNotBlank()) {
                    StaticLayout.Builder.obtain(qDetails, 0, qDetails.length, detailPaint, CONTENT_WIDTH - 12).build()
                } else null

                val blockH = summaryLayout.height + (qLayout?.height ?: 0) + 26f
                if (y + blockH > PAGE_HEIGHT - MARGIN - 24f) {
                    document.finishPage(page)
                    pageNum++
                    page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create())
                    canvas = page.canvas
                    drawPageFrame(canvas, pageNum)
                    y = drawContinuationHeader(canvas, symbolBitmap, exam, "Progress Report", pageNum)
                }

                canvas.save()
                canvas.translate(MARGIN + 6f, y)
                summaryLayout.draw(canvas)
                canvas.restore()
                y += summaryLayout.height + 4f

                if (qLayout != null) {
                    canvas.save()
                    canvas.translate(MARGIN + 6f, y)
                    qLayout.draw(canvas)
                    canvas.restore()
                    y += qLayout.height + 8f
                }

                canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, dottedPaint)
                y += 12f
            }
        }

        document.finishPage(page)
        FileOutputStream(file).use { out -> document.writeTo(out) }
        document.close()
        return file
    }

    private fun drawPageFrame(canvas: Canvas, pageNumber: Int) {
        canvas.drawColor(Color.WHITE)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 58, 138)
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
        }
        canvas.drawRect(
            MARGIN - 12f,
            MARGIN - 12f,
            PAGE_WIDTH - MARGIN + 12f,
            PAGE_HEIGHT - MARGIN + 12f,
            borderPaint
        )
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(100, 116, 139)
            textSize = 9.5f
        }
        canvas.drawText(
            "Arsath Coaching Centre  •  Study Planner by Mohammed Arsath H",
            MARGIN,
            PAGE_HEIGHT - MARGIN + 4f,
            footerPaint
        )
        canvas.drawText(
            "Page $pageNumber",
            PAGE_WIDTH - MARGIN - 40f,
            PAGE_HEIGHT - MARGIN + 4f,
            footerPaint
        )
    }

    private fun drawInstituteHeader(
        canvas: Canvas,
        symbolBitmap: Bitmap?,
        title: String,
        subtitle: String,
        meta: String
    ): Float {
        val headerBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(13, 19, 33)
            style = Paint.Style.FILL
        }
        val boxRect = RectF(MARGIN, MARGIN, PAGE_WIDTH - MARGIN, MARGIN + 68f)
        canvas.drawRoundRect(boxRect, 8f, 8f, headerBg)

        if (symbolBitmap != null) {
            val dest = RectF(MARGIN + 8f, MARGIN + 8f, MARGIN + 60f, MARGIN + 60f)
            canvas.drawBitmap(symbolBitmap, null, dest, null)
        }

        val textStart = if (symbolBitmap != null) MARGIN + 70f else MARGIN + 12f
        val goldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(245, 197, 24)
            textSize = 14.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val cyanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(103, 232, 249)
            textSize = 9f
        }

        canvas.drawText(title, textStart, MARGIN + 22f, goldPaint)
        canvas.drawText(subtitle, textStart, MARGIN + 39f, whitePaint)
        canvas.drawText(meta, textStart, MARGIN + 55f, cyanPaint)

        return MARGIN + 82f
    }

    private fun drawContinuationHeader(
        canvas: Canvas,
        symbolBitmap: Bitmap?,
        exam: String,
        label: String,
        pageNumber: Int
    ): Float {
        if (symbolBitmap != null) {
            val dest = RectF(MARGIN, MARGIN, MARGIN + 26f, MARGIN + 26f)
            canvas.drawBitmap(symbolBitmap, null, dest, null)
        }
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 58, 138)
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(
            "Arsath Coaching Centre  •  $exam $label (Page $pageNumber)",
            MARGIN + if (symbolBitmap != null) 34f else 0f,
            MARGIN + 17f,
            p
        )
        return MARGIN + 34f
    }

    fun renderPdfPagesForPreview(file: File): List<Bitmap> {
        val result = mutableListOf<Bitmap>()
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                for (i in 0 until renderer.pageCount.coerceAtMost(8)) {
                    renderer.openPage(i).use { page ->
                        val scale = 2
                        val bmp = Bitmap.createBitmap(
                            page.width * scale,
                            page.height * scale,
                            Bitmap.Config.ARGB_8888
                        )
                        bmp.eraseColor(Color.WHITE)
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        result.add(bmp)
                    }
                }
            }
        }
        return result
    }

    fun createSharePdfIntent(context: Context, file: File, title: String): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
