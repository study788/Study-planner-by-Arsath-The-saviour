package com.example.data.content

import com.example.data.model.QuestionItem
import kotlin.math.abs

object QuestionBankHelper {

    /**
     * Builds a validated 4-option QuestionItem where the correct option and 3 distinct
     * distractors are deterministically shuffled based on the question index.
     */
    fun makeQuestion(
        id: String,
        exam: String,
        subject: String,
        chapter: String,
        difficulty: String,
        questionText: String,
        correctOption: String,
        distractor1: String,
        distractor2: String,
        distractor3: String,
        explanation: String,
        seed: Int
    ): QuestionItem {
        val raw = mutableListOf<String>()
        raw.add(correctOption)
        val fallbacks = listOf(distractor1, distractor2, distractor3, "None of these", "Cannot be determined")
        for (item in fallbacks) {
            if (raw.size >= 4) break
            if (item !in raw) raw.add(item)
        }
        var pad = 1
        while (raw.size < 4) {
            val candidate = "Option ${pad++}"
            if (candidate !in raw) raw.add(candidate)
        }

        // Deterministic permutation of [0, 1, 2, 3]
        val order = listOf(
            listOf(0, 1, 2, 3),
            listOf(1, 0, 2, 3),
            listOf(2, 1, 0, 3),
            listOf(3, 1, 2, 0),
            listOf(1, 2, 0, 3),
            listOf(2, 3, 1, 0),
            listOf(3, 0, 2, 1),
            listOf(0, 3, 1, 2)
        )[abs(seed) % 8]

        val shuffledOptions = order.map { raw[it] }
        val correctIdx = order.indexOf(0)

        return QuestionItem(
            id = id,
            exam = exam,
            subject = subject,
            chapter = chapter,
            difficulty = difficulty,
            questionText = questionText,
            options = shuffledOptions,
            correctIndex = correctIdx,
            explanation = explanation
        )
    }

    fun difficultyForIndex(idx: Int): String = when (idx % 5) {
        0 -> "Easy"
        1, 2 -> "Medium"
        else -> "Tough"
    }

    fun round2(value: Double): String {
        val rounded = kotlin.math.round(value * 100.0) / 100.0
        return if (rounded % 1.0 == 0.0) rounded.toLong().toString() else rounded.toString()
    }
}
