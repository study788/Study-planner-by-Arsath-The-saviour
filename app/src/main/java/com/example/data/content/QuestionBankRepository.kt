package com.example.data.content

import com.example.data.model.CustomQuestion
import com.example.data.model.QuestionItem
import kotlin.math.abs

object QuestionBankRepository {

    val jeePhysics: List<QuestionItem> by lazy { PhysicsQuestionsGenerator.generate("JEE", 1050) }
    val jeeChemistry: List<QuestionItem> by lazy { ChemistryQuestionsGenerator.generate("JEE", 1050) }
    val jeeMathematics: List<QuestionItem> by lazy { MathQuestionsGenerator.generate(1050) }

    val neetPhysics: List<QuestionItem> by lazy { PhysicsQuestionsGenerator.generate("NEET", 1050) }
    val neetChemistry: List<QuestionItem> by lazy { ChemistryQuestionsGenerator.generate("NEET", 1050) }
    val neetBiology: List<QuestionItem> by lazy { BiologyQuestionsGenerator.generate(1050) }

    private val allBuiltinQuestions: List<QuestionItem> by lazy {
        jeePhysics + jeeChemistry + jeeMathematics + neetPhysics + neetChemistry + neetBiology
    }

    private val byIdMap: Map<String, QuestionItem> by lazy {
        allBuiltinQuestions.associateBy { it.id }
    }

    fun getQuestionsForExamAndSubject(
        exam: String,
        subject: String,
        customQuestions: List<CustomQuestion> = emptyList()
    ): List<QuestionItem> {
        val base = when (exam) {
            "JEE" -> when (subject) {
                "Physics" -> jeePhysics
                "Chemistry" -> jeeChemistry
                "Mathematics" -> jeeMathematics
                else -> emptyList()
            }
            "NEET" -> when (subject) {
                "Physics" -> neetPhysics
                "Chemistry" -> neetChemistry
                "Biology" -> neetBiology
                else -> emptyList()
            }
            else -> emptyList()
        }
        val mappedCustom = customQuestions
            .filter { it.exam == exam && it.subject.equals(subject, ignoreCase = true) && it.options.size >= 4 }
            .map { cq ->
                QuestionItem(
                    id = cq.id,
                    exam = cq.exam,
                    subject = cq.subject,
                    chapter = cq.chapter,
                    difficulty = cq.difficulty,
                    questionText = cq.questionText,
                    options = cq.options.take(4),
                    correctIndex = cq.correctIndex.toInt().coerceIn(0, 3),
                    explanation = cq.explanation.ifBlank { "Teacher-curated competitive problem." }
                )
            }
        return mappedCustom + base
    }

    fun getAllForExam(
        exam: String,
        customQuestions: List<CustomQuestion> = emptyList()
    ): List<QuestionItem> {
        val third = if (exam == "JEE") "Mathematics" else "Biology"
        return getQuestionsForExamAndSubject(exam, "Physics", customQuestions) +
            getQuestionsForExamAndSubject(exam, "Chemistry", customQuestions) +
            getQuestionsForExamAndSubject(exam, third, customQuestions)
    }

    fun findByIds(
        ids: List<String>,
        customQuestions: List<CustomQuestion> = emptyList()
    ): List<QuestionItem> {
        val customMap = customQuestions.associate { cq ->
            cq.id to QuestionItem(
                id = cq.id,
                exam = cq.exam,
                subject = cq.subject,
                chapter = cq.chapter,
                difficulty = cq.difficulty,
                questionText = cq.questionText,
                options = if (cq.options.size >= 4) cq.options.take(4) else listOf("A", "B", "C", "D"),
                correctIndex = cq.correctIndex.toInt().coerceIn(0, 3),
                explanation = cq.explanation
            )
        }
        return ids.mapNotNull { id -> customMap[id] ?: byIdMap[id] }
    }

    /**
     * Builds a deterministic daily set of 10 tough competitive homework questions
     * spread across all 3 exam subjects for the student's selected exam.
     */
    fun getDailyToughHomework(
        exam: String,
        dateKey: String,
        customQuestions: List<CustomQuestion> = emptyList(),
        count: Int = 10
    ): List<QuestionItem> {
        val third = if (exam == "JEE") "Mathematics" else "Biology"
        val subjects = listOf("Physics", "Chemistry", third)
        val seed = abs(dateKey.hashCode())
        val selected = mutableListOf<QuestionItem>()
        for (i in 0 until count) {
            val sub = subjects[i % subjects.size]
            val pool = getQuestionsForExamAndSubject(exam, sub, customQuestions)
                .filter { it.difficulty == "Tough" || it.difficulty == "Medium" }
            if (pool.isNotEmpty()) {
                val idx = (seed + i * 37) % pool.size
                val q = pool[idx]
                if (selected.none { it.id == q.id }) {
                    selected.add(q)
                }
            }
        }
        return selected
    }

    fun getTestQuestions(
        exam: String,
        testType: String,
        subjectFilter: String = "All",
        chapterFilter: String = "All",
        difficultyFilter: String = "All",
        count: Int = 15,
        customQuestions: List<CustomQuestion> = emptyList(),
        missedIds: List<String> = emptyList()
    ): List<QuestionItem> {
        if (testType == "improvement") {
            val missed = findByIds(missedIds, customQuestions).filter { it.exam == exam }
            if (missed.isNotEmpty()) {
                return missed.take(count)
            }
            // Fallback to toughest questions if student has zero wrong answers so far
            return getAllForExam(exam, customQuestions)
                .filter { it.difficulty == "Tough" }
                .shuffled()
                .take(count)
        }

        val pool = if (subjectFilter == "All") {
            getAllForExam(exam, customQuestions)
        } else {
            getQuestionsForExamAndSubject(exam, subjectFilter, customQuestions)
        }

        val filtered = pool.filter { q ->
            (chapterFilter == "All" || q.chapter == chapterFilter) &&
                (difficultyFilter == "All" || q.difficulty.equals(difficultyFilter, ignoreCase = true))
        }
        val finalPool = filtered.ifEmpty { pool }
        return finalPool.shuffled().take(count)
    }

    fun getCountsSummary(exam: String): Map<String, Int> {
        val third = if (exam == "JEE") "Mathematics" else "Biology"
        return mapOf(
            "Physics" to getQuestionsForExamAndSubject(exam, "Physics").size,
            "Chemistry" to getQuestionsForExamAndSubject(exam, "Chemistry").size,
            third to getQuestionsForExamAndSubject(exam, third).size
        )
    }
}
