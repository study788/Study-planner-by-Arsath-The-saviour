package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AdminMessage
import com.example.data.model.CustomQuestion
import com.example.data.model.CustomVideo
import com.example.data.model.HomeworkSession
import com.example.data.model.ScheduledClass
import com.example.data.model.StudyAlarm
import com.example.data.model.StudyGoal
import com.example.data.model.StudyProgress
import com.example.data.model.TestAttempt
import com.example.data.model.UserProfile
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StudyFirestoreRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    // ── User Profile ──

    fun observeUserProfile(userId: String): Flow<UserProfile?> =
        db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java, ServerTimestampBehavior.ESTIMATE)
                } else {
                    null
                }
            }

    suspend fun getUserProfile(userId: String): UserProfile? {
        val docRef = db.collection("users").document(userId)
        return try {
            val snap = docRef.get().await()
            if (snap.exists()) snap.toObject(UserProfile::class.java, ServerTimestampBehavior.ESTIMATE) else null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun saveOrUpdateUserProfile(profile: UserProfile): Result<Unit> {
        val docRef = db.collection("users").document(profile.userId)
        return try {
            val existing = docRef.get().await()
            if (!existing.exists()) {
                docRef.set(profile.toCreateMap()).await()
            } else {
                val updates = buildMap<String, Any> {
                    put("name", profile.name)
                    put("studentClass", profile.studentClass)
                    put("parentName", profile.parentName)
                    put("contact", profile.contact)
                    put("exam", profile.exam)
                    put("plan", profile.plan)
                    put("sessionsCount", profile.sessionsCount)
                    profile.darkMode?.let { put("darkMode", it) }
                    put("updatedAt", FieldValue.serverTimestamp())
                }
                docRef.update(updates).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun incrementSessionsCount(userId: String, newCount: Long): Result<Unit> {
        val docRef = db.collection("users").document(userId)
        return try {
            docRef.update(
                mapOf(
                    "sessionsCount" to newCount,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateDarkMode(userId: String, darkMode: Boolean): Result<Unit> {
        val docRef = db.collection("users").document(userId)
        return try {
            docRef.update(
                mapOf(
                    "darkMode" to darkMode,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // ── Admin Student Management ──

    fun observeAllStudents(): Flow<List<UserProfile>> =
        db.collection("users")
            .snapshots()
            .map { snap -> snap.toObjects(UserProfile::class.java, ServerTimestampBehavior.ESTIMATE) }

    suspend fun createStudentByAdmin(student: UserProfile): Result<String> {
        val docId = student.userId.ifBlank { "stu_${UUID.randomUUID().toString().replace("-", "").take(12)}" }
        val payload = student.copy(userId = docId, role = "student")
        val docRef = db.collection("users").document(docId)
        return try {
            docRef.set(payload.toCreateMap()).await()
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun setStudentActive(userId: String, active: Boolean): Result<Unit> {
        val docRef = db.collection("users").document(userId)
        return try {
            docRef.update(
                mapOf(
                    "active" to active,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // ── Test Attempts (Preserving Every Exam-Side Question) ──

    fun observeTestAttempts(userId: String): Flow<List<TestAttempt>> =
        db.collection("users").document(userId)
            .collection("test_attempts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snap -> snap.toObjects(TestAttempt::class.java, ServerTimestampBehavior.ESTIMATE) }

    suspend fun saveTestAttempt(userId: String, attempt: TestAttempt): Result<String> {
        val docId = attempt.id.ifBlank { "att_${UUID.randomUUID().toString().replace("-", "").take(16)}" }
        val docRef = db.collection("users").document(userId)
            .collection("test_attempts").document(docId)
        val payload = mapOf(
            "userId" to userId,
            "exam" to attempt.exam,
            "testType" to attempt.testType,
            "title" to attempt.title.take(120),
            "score" to attempt.score,
            "total" to attempt.total,
            "pct" to attempt.pct,
            "grade" to attempt.grade,
            "durationSeconds" to attempt.durationSeconds,
            "questions" to attempt.questions.take(120),
            "dateLabel" to attempt.dateLabel,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    // ── Weekly Goals ──

    fun observeGoals(userId: String): Flow<List<StudyGoal>> =
        db.collection("users").document(userId)
            .collection("goals")
            .snapshots()
            .map { snap -> snap.toObjects(StudyGoal::class.java, ServerTimestampBehavior.ESTIMATE) }

    suspend fun addGoal(userId: String, title: String, subject: String, priority: String): Result<String> {
        val docId = "goal_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val docRef = db.collection("users").document(userId).collection("goals").document(docId)
        val payload = mapOf(
            "userId" to userId,
            "title" to title.take(160),
            "subject" to subject.take(40),
            "priority" to priority,
            "done" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun toggleGoal(userId: String, goalId: String, done: Boolean): Result<Unit> {
        val docRef = db.collection("users").document(userId).collection("goals").document(goalId)
        return try {
            docRef.update(
                mapOf(
                    "done" to done,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteGoal(userId: String, goalId: String): Result<Unit> {
        val docRef = db.collection("users").document(userId).collection("goals").document(goalId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // ── Study Alarms ──

    fun observeAlarms(userId: String): Flow<List<StudyAlarm>> =
        db.collection("users").document(userId)
            .collection("alarms")
            .snapshots()
            .map { snap -> snap.toObjects(StudyAlarm::class.java, ServerTimestampBehavior.ESTIMATE) }

    suspend fun addAlarm(userId: String, time24: String, label: String): Result<String> {
        val docId = "alm_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val docRef = db.collection("users").document(userId).collection("alarms").document(docId)
        val payload = mapOf(
            "userId" to userId,
            "time24" to time24.take(5).padEnd(5, '0'),
            "label" to label.ifBlank { "Study Session" }.take(100),
            "active" to true,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun toggleAlarm(userId: String, alarmId: String, active: Boolean): Result<Unit> {
        val docRef = db.collection("users").document(userId).collection("alarms").document(alarmId)
        return try {
            docRef.update(mapOf("active" to active, "updatedAt" to FieldValue.serverTimestamp())).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteAlarm(userId: String, alarmId: String): Result<Unit> {
        val docRef = db.collection("users").document(userId).collection("alarms").document(alarmId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // ── Daily Competitive Homework & 2-Hour Answer PDF Unlock ──

    fun observeHomeworkSessions(userId: String): Flow<List<HomeworkSession>> =
        db.collection("users").document(userId)
            .collection("homework_sessions")
            .snapshots()
            .map { snap -> snap.toObjects(HomeworkSession::class.java, ServerTimestampBehavior.ESTIMATE) }

    suspend fun getOrCreateHomeworkSession(
        userId: String,
        dateKey: String,
        exam: String,
        questionIds: List<String>
    ): Result<HomeworkSession> {
        val docId = "hw_${dateKey.replace("-", "")}_${exam}"
        val docRef = db.collection("users").document(userId)
            .collection("homework_sessions").document(docId)
        return try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                val existing = snap.toObject(HomeworkSession::class.java, ServerTimestampBehavior.ESTIMATE)
                if (existing != null) return Result.success(existing)
            }
            val nowMillis = System.currentTimeMillis()
            val payload = mapOf(
                "userId" to userId,
                "dateKey" to dateKey,
                "exam" to exam,
                "questionIds" to questionIds.take(20),
                "answers" to emptyMap<String, Long>(),
                "completed" to false,
                "pdfExported" to false,
                "assignedAtMillis" to nowMillis,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            val fresh = docRef.get().await().toObject(HomeworkSession::class.java, ServerTimestampBehavior.ESTIMATE)
                ?: HomeworkSession(
                    id = docId,
                    userId = userId,
                    dateKey = dateKey,
                    exam = exam,
                    questionIds = questionIds,
                    assignedAtMillis = nowMillis
                )
            Result.success(fresh)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateHomeworkProgress(
        userId: String,
        homeworkId: String,
        answers: Map<String, Long>,
        completed: Boolean,
        pdfExported: Boolean
    ): Result<Unit> {
        val docRef = db.collection("users").document(userId)
            .collection("homework_sessions").document(homeworkId)
        return try {
            docRef.update(
                mapOf(
                    "answers" to answers,
                    "completed" to completed,
                    "pdfExported" to pdfExported,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // ── Study Progress (Flashcards, Missed Questions, Completed Chapters, Notes) ──

    fun observeStudyProgress(userId: String): Flow<StudyProgress> =
        db.collection("users").document(userId)
            .collection("study_progress").document("main")
            .snapshots()
            .map { snap ->
                if (snap.exists()) {
                    snap.toObject(StudyProgress::class.java, ServerTimestampBehavior.ESTIMATE)
                        ?: StudyProgress(id = "main", userId = userId)
                } else {
                    StudyProgress(id = "main", userId = userId)
                }
            }

    suspend fun saveStudyProgress(userId: String, progress: StudyProgress): Result<Unit> {
        val docRef = db.collection("users").document(userId)
            .collection("study_progress").document("main")
        return try {
            val snap = docRef.get().await()
            if (!snap.exists()) {
                docRef.set(
                    mapOf(
                        "userId" to userId,
                        "masteredFlashcardIds" to progress.masteredFlashcardIds.distinct().take(500),
                        "missedQuestionIds" to progress.missedQuestionIds.distinct().take(500),
                        "completedChapters" to progress.completedChapters.distinct().take(200),
                        "sessionNotes" to progress.sessionNotes.take(5000),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.update(
                    mapOf(
                        "masteredFlashcardIds" to progress.masteredFlashcardIds.distinct().take(500),
                        "missedQuestionIds" to progress.missedQuestionIds.distinct().take(500),
                        "completedChapters" to progress.completedChapters.distinct().take(200),
                        "sessionNotes" to progress.sessionNotes.take(5000),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    // ── Admin <-> Student Messages ──

    fun observeAdminMessages(userId: String): Flow<List<AdminMessage>> =
        db.collection("users").document(userId)
            .collection("admin_messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { snap -> snap.toObjects(AdminMessage::class.java, ServerTimestampBehavior.ESTIMATE) }

    suspend fun sendAdminMessage(
        targetStudentUid: String,
        senderUid: String,
        senderRole: String,
        senderName: String,
        message: String,
        timeLabel: String
    ): Result<String> {
        val docId = "msg_${UUID.randomUUID().toString().replace("-", "").take(14)}"
        val docRef = db.collection("users").document(targetStudentUid)
            .collection("admin_messages").document(docId)
        val payload = mapOf(
            "userId" to targetStudentUid,
            "senderUid" to senderUid,
            "senderRole" to senderRole,
            "senderName" to senderName.take(80),
            "message" to message.take(1000),
            "timeLabel" to timeLabel.take(30),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    // ── Shared Public Content Managed by Admin ──

    fun observeCustomVideos(): Flow<List<CustomVideo>> =
        db.collection("custom_videos")
            .whereEqualTo("isPublic", true)
            .snapshots()
            .map { snap -> snap.toObjects(CustomVideo::class.java, ServerTimestampBehavior.ESTIMATE) }

    suspend fun addCustomVideo(video: CustomVideo): Result<String> {
        val docId = video.id.ifBlank { "vid_${UUID.randomUUID().toString().replace("-", "").take(12)}" }
        val docRef = db.collection("custom_videos").document(docId)
        val payload = mapOf(
            "isPublic" to true,
            "exam" to video.exam,
            "subject" to video.subject.take(40),
            "chapter" to video.chapter.take(100),
            "title" to video.title.take(160),
            "url" to video.url.take(300),
            "rating" to video.rating.coerceIn(0.0, 5.0),
            "authorUid" to video.authorUid,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteCustomVideo(videoId: String): Result<Unit> {
        val docRef = db.collection("custom_videos").document(videoId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeCustomQuestions(): Flow<List<CustomQuestion>> =
        db.collection("custom_questions")
            .whereEqualTo("isPublic", true)
            .snapshots()
            .map { snap -> snap.toObjects(CustomQuestion::class.java, ServerTimestampBehavior.ESTIMATE) }

    suspend fun addCustomQuestion(q: CustomQuestion): Result<String> {
        val docId = q.id.ifBlank { "cq_${UUID.randomUUID().toString().replace("-", "").take(12)}" }
        val docRef = db.collection("custom_questions").document(docId)
        val payload = mapOf(
            "isPublic" to true,
            "exam" to q.exam,
            "subject" to q.subject.take(40),
            "chapter" to q.chapter.take(100),
            "difficulty" to q.difficulty,
            "questionText" to q.questionText.take(1200),
            "options" to q.options.take(4),
            "correctIndex" to q.correctIndex.coerceIn(0L, 3L),
            "explanation" to q.explanation.take(1200),
            "authorUid" to q.authorUid,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeScheduledClasses(): Flow<List<ScheduledClass>> =
        db.collection("scheduled_classes")
            .whereEqualTo("isPublic", true)
            .snapshots()
            .map { snap -> snap.toObjects(ScheduledClass::class.java, ServerTimestampBehavior.ESTIMATE) }

    suspend fun addScheduledClass(sc: ScheduledClass): Result<String> {
        val docId = sc.id.ifBlank { "cls_${UUID.randomUUID().toString().replace("-", "").take(12)}" }
        val docRef = db.collection("scheduled_classes").document(docId)
        val payload = mapOf(
            "isPublic" to true,
            "title" to sc.title.take(120),
            "exam" to sc.exam,
            "subject" to sc.subject.take(40),
            "scheduleTime" to sc.scheduleTime.take(60),
            "meetUrl" to sc.meetUrl.take(300),
            "authorUid" to sc.authorUid,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }
}
