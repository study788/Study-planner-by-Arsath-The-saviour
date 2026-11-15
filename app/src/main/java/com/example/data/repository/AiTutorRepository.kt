package com.example.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "AiTutorRepository"

sealed interface AiTutorUiState {
    data object Idle : AiTutorUiState
    data object Loading : AiTutorUiState
    data class Streaming(val text: String) : AiTutorUiState
    data class Success(val text: String) : AiTutorUiState
    data class Error(val message: String) : AiTutorUiState
}

data class AiChatTurn(
    val isUser: Boolean,
    val text: String,
    val timestamp: String = ""
)

class AiTutorRepository {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "You are Arsath AI Mentor at Arsath Coaching Centre (created by Mohammed Arsath H). " +
                        "You coach Indian JEE (Main & Advanced) and NEET UG students clearly, encouragingly, " +
                        "and rigorously with step-by-step derivations, key formulas, common pitfalls, and NCERT insights."
                )
            }
        )
    }

    fun askTutorStream(
        question: String,
        exam: String,
        studentName: String
    ): Flow<AiTutorUiState> = flow {
        emit(AiTutorUiState.Loading)
        val prompt = """
            Student: $studentName | Target Exam: $exam
            Doubt / Study Request: $question
            
            Provide a structured explanation with:
            1. Core Concept / Formula
            2. Step-by-step Solution or Strategy
            3. Exam Trap to Avoid
        """.trimIndent()

        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                model.generateContentStream(prompt).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(AiTutorUiState.Streaming(accumulated.toString()))
                    }
                }
            }
            if (accumulated.isNotBlank()) {
                emit(AiTutorUiState.Success(accumulated.toString()))
            } else {
                emit(AiTutorUiState.Error("Arsath AI Mentor returned an empty response. Please rephrase your question."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI Tutor request timed out", e)
            emit(AiTutorUiState.Error("Request timed out after 60 seconds. Please check connection and retry."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI Tutor inference failed", e)
            emit(AiTutorUiState.Error(e.localizedMessage ?: "AI Mentor request failed."))
        }
    }
}
