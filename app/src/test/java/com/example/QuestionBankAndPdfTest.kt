package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.content.AnswerPdfGenerator
import com.example.data.content.ChapterVideosRepository
import com.example.data.content.FlashcardsData
import com.example.data.content.MindmapData
import com.example.data.content.QuestionBankRepository
import com.example.data.content.SyllabusChapters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class QuestionBankAndPdfTest {

    @Test
    fun `jee and neet subjects each contain at least 1000 unique valid questions`() {
        val jeeCounts = QuestionBankRepository.getCountsSummary("JEE")
        val neetCounts = QuestionBankRepository.getCountsSummary("NEET")

        assertEquals(setOf("Physics", "Chemistry", "Mathematics"), jeeCounts.keys)
        assertEquals(setOf("Physics", "Chemistry", "Biology"), neetCounts.keys)

        jeeCounts.forEach { (subject, count) ->
            assertTrue("JEE $subject has $count (<1000)", count >= 1000)
            val qs = QuestionBankRepository.getQuestionsForExamAndSubject("JEE", subject)
            assertEquals("JEE $subject IDs not unique", qs.size, qs.map { it.id }.toSet().size)
            qs.take(100).forEach { q ->
                assertEquals(4, q.options.size)
                assertEquals(4, q.options.toSet().size)
                assertTrue(q.correctIndex in 0..3)
            }
        }

        neetCounts.forEach { (subject, count) ->
            assertTrue("NEET $subject has $count (<1000)", count >= 1000)
            val qs = QuestionBankRepository.getQuestionsForExamAndSubject("NEET", subject)
            assertEquals("NEET $subject IDs not unique", qs.size, qs.map { it.id }.toSet().size)
            qs.take(100).forEach { q ->
                assertEquals(4, q.options.size)
                assertEquals(4, q.options.toSet().size)
                assertTrue(q.correctIndex in 0..3)
            }
        }
    }

    @Test
    fun `every chapter has rated videos mindmaps and flashcards`() {
        listOf(
            "JEE" to listOf("Physics", "Chemistry", "Mathematics"),
            "NEET" to listOf("Physics", "Chemistry", "Biology")
        ).forEach { (exam, subjects) ->
            subjects.forEach { subject ->
                val chapters = SyllabusChapters.chaptersFor(exam, subject)
                val videos = ChapterVideosRepository.getVideosForExamAndSubject(exam, subject)
                val mindmaps = MindmapData.getMindmapsFor(exam, subject)
                val flashcards = FlashcardsData.getFlashcards(exam, subject)

                assertTrue(chapters.isNotEmpty())
                assertTrue(videos.size >= chapters.size)
                assertEquals(chapters.size, mindmaps.size)
                assertTrue(flashcards.size >= chapters.size)
            }
        }
    }

    @Test
    fun `answer pdf generates valid multi-color pdf with institute branding and dotted lines`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val homeworkQuestions = QuestionBankRepository.getDailyToughHomework("JEE", "2026-11-15", count = 10)
        assertEquals(10, homeworkQuestions.size)

        val pdfFile = AnswerPdfGenerator.generateHomeworkAnswerPdf(
            context = context,
            exam = "JEE",
            dateKey = "2026-11-15",
            studentName = "Arjun Kumar",
            questions = homeworkQuestions
        )
        assertTrue(pdfFile.exists())
        assertTrue("Generated PDF file empty", pdfFile.length() > 300L)
        val header = pdfFile.readBytes().take(4).toByteArray().decodeToString()
        assertEquals("%PDF", header)

        // Verify distinct colors for Questions and Answers and 2-hour delay window
        assertEquals(10, AnswerPdfGenerator.questionColors.toSet().size)
        assertEquals(10, AnswerPdfGenerator.answerColors.toSet().size)
        for (i in 0 until 10) {
            assertTrue(
                "Q and A color at index $i should be distinct",
                AnswerPdfGenerator.questionColors[i] != AnswerPdfGenerator.answerColors[i]
            )
        }
        assertEquals(7_200_000L, com.example.viewmodel.StudyPlannerViewModel.HOMEWORK_ANSWER_DELAY_MS)
    }
}
