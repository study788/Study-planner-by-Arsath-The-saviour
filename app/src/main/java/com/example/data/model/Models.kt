package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class ExamType(val code: String, val label: String) {
    JEE("JEE", "JEE Main & Adv"),
    NEET("NEET", "NEET UG Medical");

    fun thirdSubject(): String = if (this == JEE) "Mathematics" else "Biology"
    fun subjects(): List<String> = listOf("Physics", "Chemistry", thirdSubject())
}

enum class PlanType(val code: String, val title: String, val subtitle: String) {
    ONE_YEAR("1year", "1 Year Plan", "Intensive · May '26 – Feb '27"),
    TWO_YEAR("2year", "2 Year Plan", "Comprehensive · May '26 – Jan '28")
}

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val studentClass: String = "Class 12",
    val parentName: String = "",
    val contact: String = "",
    val exam: String = "JEE",
    val plan: String = "1year",
    val role: String = "student",
    val active: Boolean = true,
    val sessionsCount: Long = 0L,
    val joinDate: String = "",
    val darkMode: Boolean? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = buildMap {
        put("userId", userId)
        put("name", name)
        put("email", email)
        put("studentClass", studentClass)
        put("parentName", parentName)
        put("contact", contact)
        put("exam", exam)
        put("plan", plan)
        put("role", role)
        put("active", active)
        put("sessionsCount", sessionsCount)
        put("joinDate", joinDate)
        darkMode?.let { put("darkMode", it) }
        put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }
}

@IgnoreExtraProperties
data class TestAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val exam: String = "JEE",
    val testType: String = "daily",
    val title: String = "",
    val score: Long = 0L,
    val total: Long = 0L,
    val pct: Long = 0L,
    val grade: String = "B",
    val durationSeconds: Long = 0L,
    val questions: List<Map<String, Any>> = emptyList(),
    val dateLabel: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StudyGoal(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subject: String = "Physics",
    val priority: String = "med",
    val done: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StudyAlarm(
    @DocumentId val id: String = "",
    val userId: String = "",
    val time24: String = "07:00",
    val label: String = "Study Session",
    val active: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class HomeworkSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val dateKey: String = "",
    val exam: String = "JEE",
    val questionIds: List<String> = emptyList(),
    val answers: Map<String, Long> = emptyMap(),
    val completed: Boolean = false,
    val pdfExported: Boolean = false,
    val assignedAtMillis: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StudyProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val masteredFlashcardIds: List<String> = emptyList(),
    val missedQuestionIds: List<String> = emptyList(),
    val completedChapters: List<String> = emptyList(),
    val sessionNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AdminMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val senderUid: String = "",
    val senderRole: String = "student",
    val senderName: String = "",
    val message: String = "",
    val timeLabel: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CustomVideo(
    @DocumentId val id: String = "",
    val isPublic: Boolean = true,
    val exam: String = "JEE",
    val subject: String = "Physics",
    val chapter: String = "Kinematics",
    val title: String = "",
    val url: String = "",
    val rating: Double = 4.9,
    val authorUid: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CustomQuestion(
    @DocumentId val id: String = "",
    val isPublic: Boolean = true,
    val exam: String = "JEE",
    val subject: String = "Physics",
    val chapter: String = "Kinematics",
    val difficulty: String = "Tough",
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Long = 0L,
    val explanation: String = "",
    val authorUid: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ScheduledClass(
    @DocumentId val id: String = "",
    val isPublic: Boolean = true,
    val title: String = "",
    val exam: String = "ALL",
    val subject: String = "All Subjects",
    val scheduleTime: String = "",
    val meetUrl: String = "https://meet.google.com/new",
    val authorUid: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class QuestionItem(
    val id: String,
    val exam: String,
    val subject: String,
    val chapter: String,
    val difficulty: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class ChapterVideo(
    val id: String,
    val exam: String,
    val subject: String,
    val chapter: String,
    val title: String,
    val educator: String,
    val rating: Double,
    val duration: String,
    val url: String,
    val weightageNote: String
)

data class MindmapBranch(
    val title: String,
    val colorHex: Long,
    val points: List<String>
)

data class ChapterMindmap(
    val id: String,
    val exam: String,
    val subject: String,
    val chapter: String,
    val centralSummary: String,
    val branches: List<MindmapBranch>
)

data class FlashcardItem(
    val id: String,
    val exam: String,
    val subject: String,
    val chapter: String,
    val frontTitle: String,
    val frontPrompt: String,
    val backAnswer: String,
    val memoryHook: String
)

data class QuoteItem(
    val emoji: String,
    val quote: String,
    val author: String
)

data class PlannerRow(
    val month: String,
    val physics: String,
    val chemistry: String,
    val thirdSubject: String
)
