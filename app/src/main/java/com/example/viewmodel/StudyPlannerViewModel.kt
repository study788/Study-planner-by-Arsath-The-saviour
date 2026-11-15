package com.example.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.content.AnswerPdfGenerator
import com.example.data.content.FlashcardsData
import com.example.data.content.MindmapData
import com.example.data.content.QuestionBankRepository
import com.example.data.model.AdminMessage
import com.example.data.model.CustomQuestion
import com.example.data.model.CustomVideo
import com.example.data.model.ExamType
import com.example.data.model.HomeworkSession
import com.example.data.model.QuestionItem
import com.example.data.model.ScheduledClass
import com.example.data.model.StudyAlarm
import com.example.data.model.StudyGoal
import com.example.data.model.StudyProgress
import com.example.data.model.TestAttempt
import com.example.data.model.UserProfile
import com.example.data.repository.AiChatTurn
import com.example.data.repository.AiTutorRepository
import com.example.data.repository.AiTutorUiState
import com.example.data.repository.AuthRepository
import com.example.data.repository.StudyFirestoreRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TimerMode(val label: String, val focusSeconds: Int, val breakSeconds: Int) {
    POMODORO("Pomodoro 25/5", 25 * 60, 5 * 60),
    DEEP_WORK("Deep Work 50/10", 50 * 60, 10 * 60),
    FLOW("Flow 90/15", 90 * 60, 15 * 60),
    STOPWATCH("Stopwatch", 0, 0)
}

data class ActiveQuizState(
    val testType: String,
    val title: String,
    val exam: String,
    val questions: List<QuestionItem>,
    val currentIndex: Int = 0,
    val selectedAnswers: Map<String, Int> = emptyMap(),
    val secondsElapsed: Int = 0,
    val timeLimitSeconds: Int = 0, // 0 = untimed
    val isFinished: Boolean = false,
    val savedAttempt: TestAttempt? = null
)

data class PdfPreviewState(
    val title: String,
    val file: File,
    val pageBitmaps: List<Bitmap>
)

class StudyPlannerViewModel(
    private val appContext: Context,
    private val authRepo: AuthRepository = AuthRepository(),
    private val firestoreRepo: StudyFirestoreRepository = StudyFirestoreRepository(appContext),
    private val aiTutorRepo: AiTutorRepository = AiTutorRepository()
) : ViewModel() {

    companion object {
        const val ADMIN_EMAIL = "banusait2005@gmail.com"
        const val HOMEWORK_ANSWER_DELAY_MS = 2 * 60 * 60 * 1000L // Exactly 2 hours
        private const val NOTIFICATION_CHANNEL_ID = "arsath_study_alerts"
    }

    private val _firebaseUser = MutableStateFlow<FirebaseUser?>(authRepo.currentUser)
    val firebaseUser: StateFlow<FirebaseUser?> = _firebaseUser.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _profileLoadedOnce = MutableStateFlow(false)
    val profileLoadedOnce: StateFlow<Boolean> = _profileLoadedOnce.asStateFlow()

    private val _selectedExamOverride = MutableStateFlow<String?>(null)
    val selectedExamOverride: StateFlow<String?> = _selectedExamOverride.asStateFlow()
    private val _darkThemeOverride = MutableStateFlow<Boolean?>(null)
    val darkThemeOverride: StateFlow<Boolean?> = _darkThemeOverride.asStateFlow()

    private val _testAttempts = MutableStateFlow<List<TestAttempt>>(emptyList())
    val testAttempts: StateFlow<List<TestAttempt>> = _testAttempts.asStateFlow()

    private val _goals = MutableStateFlow<List<StudyGoal>>(emptyList())
    val goals: StateFlow<List<StudyGoal>> = _goals.asStateFlow()

    private val _alarms = MutableStateFlow<List<StudyAlarm>>(emptyList())
    val alarms: StateFlow<List<StudyAlarm>> = _alarms.asStateFlow()

    private val _homeworkSessions = MutableStateFlow<List<HomeworkSession>>(emptyList())
    val homeworkSessions: StateFlow<List<HomeworkSession>> = _homeworkSessions.asStateFlow()

    private val _studyProgress = MutableStateFlow(StudyProgress())
    val studyProgress: StateFlow<StudyProgress> = _studyProgress.asStateFlow()

    private val _messages = MutableStateFlow<List<AdminMessage>>(emptyList())
    val messages: StateFlow<List<AdminMessage>> = _messages.asStateFlow()

    private val _customVideos = MutableStateFlow<List<CustomVideo>>(emptyList())
    val customVideos: StateFlow<List<CustomVideo>> = _customVideos.asStateFlow()

    private val _customQuestions = MutableStateFlow<List<CustomQuestion>>(emptyList())
    val customQuestions: StateFlow<List<CustomQuestion>> = _customQuestions.asStateFlow()

    private val _scheduledClasses = MutableStateFlow<List<ScheduledClass>>(emptyList())
    val scheduledClasses: StateFlow<List<ScheduledClass>> = _scheduledClasses.asStateFlow()

    private val _allStudents = MutableStateFlow<List<UserProfile>>(emptyList())
    val allStudents: StateFlow<List<UserProfile>> = _allStudents.asStateFlow()

    private val _adminSelectedStudentMessages = MutableStateFlow<List<AdminMessage>>(emptyList())
    val adminSelectedStudentMessages: StateFlow<List<AdminMessage>> = _adminSelectedStudentMessages.asStateFlow()

    private val _activeQuiz = MutableStateFlow<ActiveQuizState?>(null)
    val activeQuiz: StateFlow<ActiveQuizState?> = _activeQuiz.asStateFlow()

    private val _pdfPreview = MutableStateFlow<PdfPreviewState?>(null)
    val pdfPreview: StateFlow<PdfPreviewState?> = _pdfPreview.asStateFlow()

    private val _nowMillis = MutableStateFlow(System.currentTimeMillis())
    val nowMillis: StateFlow<Long> = _nowMillis.asStateFlow()

    // Timer state
    private val _timerMode = MutableStateFlow(TimerMode.POMODORO)
    val timerMode: StateFlow<TimerMode> = _timerMode.asStateFlow()

    private val _timerRunning = MutableStateFlow(false)
    val timerRunning: StateFlow<Boolean> = _timerRunning.asStateFlow()

    private val _isBreakPhase = MutableStateFlow(false)
    val isBreakPhase: StateFlow<Boolean> = _isBreakPhase.asStateFlow()

    private val _timerSeconds = MutableStateFlow(TimerMode.POMODORO.focusSeconds)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    // AI Tutor state
    private val _aiState = MutableStateFlow<AiTutorUiState>(AiTutorUiState.Idle)
    val aiState: StateFlow<AiTutorUiState> = _aiState.asStateFlow()

    private val _aiChatHistory = MutableStateFlow<List<AiChatTurn>>(emptyList())
    val aiChatHistory: StateFlow<List<AiChatTurn>> = _aiChatHistory.asStateFlow()

    private var userListenersJob: Job? = null
    private var adminStudentsJob: Job? = null
    private var adminChatJob: Job? = null
    private var timerJob: Job? = null
    private var quizTimerJob: Job? = null
    private var clockJob: Job? = null

    init {
        viewModelScope.launch {
            authRepo.observeAuthState().collect { user ->
                _firebaseUser.value = user
                if (user != null) {
                    attachUserListeners(user)
                } else {
                    clearUserState()
                }
            }
        }
        startClockLoop()
    }

    fun todayDateKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun effectiveExam(): String = _selectedExamOverride.value ?: _userProfile.value?.exam ?: "JEE"

    fun effectiveDarkTheme(systemDark: Boolean): Boolean =
        _darkThemeOverride.value ?: _userProfile.value?.darkMode ?: systemDark

    fun isAuthorizedAdmin(): Boolean {
        val user = _firebaseUser.value ?: return false
        val emailMatch = user.email.equals(ADMIN_EMAIL, ignoreCase = true)
        val roleMatch = _userProfile.value?.role == "admin"
        return emailMatch || roleMatch
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun showBanner(msg: String) {
        _statusBanner.value = msg
    }

    fun signInWithGoogle(activityContext: Context, preferReturningSilent: Boolean) {
        viewModelScope.launch {
            _authLoading.value = true
            val result = authRepo.signInWithGoogle(activityContext, preferReturningSilent)
            _authLoading.value = false
            result.onFailure { err ->
                _statusBanner.value = err.localizedMessage ?: "Google Sign-In failed."
            }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            authRepo.signOut(context)
            clearUserState()
        }
    }

    private fun attachUserListeners(user: FirebaseUser) {
        userListenersJob?.cancel()
        _profileLoadedOnce.value = false
        userListenersJob = viewModelScope.launch {
            launch {
                firestoreRepo.observeUserProfile(user.uid)
                    .catch { e -> _statusBanner.value = e.localizedMessage }
                    .collect { profile ->
                        _userProfile.value = profile
                        _profileLoadedOnce.value = true
                        if (profile?.role == "admin" || user.email.equals(ADMIN_EMAIL, ignoreCase = true)) {
                            attachAdminStudentsListener()
                        }
                    }
            }
            launch {
                firestoreRepo.observeTestAttempts(user.uid)
                    .catch { e -> _statusBanner.value = e.localizedMessage }
                    .collect { _testAttempts.value = it }
            }
            launch {
                firestoreRepo.observeGoals(user.uid)
                    .catch { }
                    .collect { _goals.value = it }
            }
            launch {
                firestoreRepo.observeAlarms(user.uid)
                    .catch { }
                    .collect { _alarms.value = it }
            }
            launch {
                firestoreRepo.observeHomeworkSessions(user.uid)
                    .catch { }
                    .collect { _homeworkSessions.value = it }
            }
            launch {
                firestoreRepo.observeStudyProgress(user.uid)
                    .catch { }
                    .collect { _studyProgress.value = it }
            }
            launch {
                firestoreRepo.observeAdminMessages(user.uid)
                    .catch { }
                    .collect { _messages.value = it }
            }
            launch {
                firestoreRepo.observeCustomVideos()
                    .catch { }
                    .collect { _customVideos.value = it }
            }
            launch {
                firestoreRepo.observeCustomQuestions()
                    .catch { }
                    .collect { _customQuestions.value = it }
            }
            launch {
                firestoreRepo.observeScheduledClasses()
                    .catch { }
                    .collect { _scheduledClasses.value = it }
            }
        }
    }

    private fun attachAdminStudentsListener() {
        if (adminStudentsJob?.isActive == true) return
        adminStudentsJob = viewModelScope.launch {
            firestoreRepo.observeAllStudents()
                .catch { }
                .collect { _allStudents.value = it }
        }
    }

    private fun clearUserState() {
        userListenersJob?.cancel()
        adminStudentsJob?.cancel()
        adminChatJob?.cancel()
        _userProfile.value = null
        _profileLoadedOnce.value = false
        _testAttempts.value = emptyList()
        _goals.value = emptyList()
        _alarms.value = emptyList()
        _homeworkSessions.value = emptyList()
        _studyProgress.value = StudyProgress()
        _messages.value = emptyList()
        _allStudents.value = emptyList()
        _activeQuiz.value = null
        _pdfPreview.value = null
        _aiChatHistory.value = emptyList()
        _aiState.value = AiTutorUiState.Idle
    }

    fun completeOnboarding(
        name: String,
        studentClass: String,
        parentName: String,
        contact: String,
        exam: String,
        plan: String,
        requestedRole: String
    ) {
        val user = _firebaseUser.value ?: return
        viewModelScope.launch {
            val resolvedRole = if (
                requestedRole == "admin" &&
                user.email.equals(ADMIN_EMAIL, ignoreCase = true)
            ) "admin" else "student"

            val existing = _userProfile.value
            val profile = UserProfile(
                id = user.uid,
                userId = user.uid,
                name = name.trim().ifBlank { user.displayName ?: "Student" },
                email = user.email.orEmpty(),
                studentClass = studentClass,
                parentName = parentName.trim(),
                contact = contact.trim(),
                exam = exam,
                plan = plan,
                role = existing?.role ?: resolvedRole,
                active = true,
                sessionsCount = existing?.sessionsCount ?: 0L,
                joinDate = existing?.joinDate?.ifBlank { todayDateKey() } ?: todayDateKey(),
                darkMode = _darkThemeOverride.value ?: existing?.darkMode
            )
            firestoreRepo.saveOrUpdateUserProfile(profile)
                .onSuccess {
                    _selectedExamOverride.value = exam
                    _statusBanner.value = "Welcome back, ${profile.name}! Account & progress synced."
                }
                .onFailure {
                    _statusBanner.value = it.localizedMessage ?: "Failed to save profile."
                }
        }
    }

    fun switchExamTrack(exam: String) {
        _selectedExamOverride.value = exam
        val profile = _userProfile.value ?: return
        viewModelScope.launch {
            firestoreRepo.saveOrUpdateUserProfile(profile.copy(exam = exam))
        }
    }

    fun toggleTheme(isDark: Boolean) {
        _darkThemeOverride.value = isDark
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            firestoreRepo.updateDarkMode(uid, isDark)
        }
    }

    // ── Timer & Session Notes ──

    fun selectTimerMode(mode: TimerMode) {
        pauseTimer()
        _timerMode.value = mode
        _isBreakPhase.value = false
        _timerSeconds.value = mode.focusSeconds
    }

    fun toggleTimerRunning() {
        if (_timerRunning.value) {
            pauseTimer()
        } else {
            _timerRunning.value = true
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_timerRunning.value) {
                    delay(1000L)
                    if (_timerMode.value == TimerMode.STOPWATCH) {
                        _timerSeconds.value += 1
                    } else {
                        val rem = _timerSeconds.value - 1
                        if (rem <= 0) {
                            onTimerCycleFinished()
                        } else {
                            _timerSeconds.value = rem
                        }
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        _timerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _isBreakPhase.value = false
        _timerSeconds.value = _timerMode.value.focusSeconds
    }

    private fun onTimerCycleFinished() {
        val mode = _timerMode.value
        if (!_isBreakPhase.value) {
            _isBreakPhase.value = true
            _timerSeconds.value = mode.breakSeconds
            val uid = _firebaseUser.value?.uid
            val newCount = (_userProfile.value?.sessionsCount ?: 0L) + 1L
            if (uid != null) {
                viewModelScope.launch { firestoreRepo.incrementSessionsCount(uid, newCount) }
            }
            sendLocalNotification("Focus Session Complete!", "Great work! Take a ${mode.breakSeconds / 60}-min recharge break.")
            _statusBanner.value = "Session logged! Starting ${mode.breakSeconds / 60}-minute break."
        } else {
            pauseTimer()
            _isBreakPhase.value = false
            _timerSeconds.value = mode.focusSeconds
            sendLocalNotification("Break Over", "Ready for your next deep focus block!")
        }
    }

    fun saveSessionNotes(notes: String) {
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            val updated = _studyProgress.value.copy(userId = uid, sessionNotes = notes)
            firestoreRepo.saveStudyProgress(uid, updated)
                .onSuccess { _statusBanner.value = "Study notes saved to cloud!" }
        }
    }

    // ── Goals & Alarms ──

    fun addGoal(title: String, subject: String, priority: String) {
        val uid = _firebaseUser.value?.uid ?: return
        if (title.isBlank()) return
        viewModelScope.launch {
            firestoreRepo.addGoal(uid, title.trim(), subject, priority)
        }
    }

    fun toggleGoal(goal: StudyGoal) {
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch { firestoreRepo.toggleGoal(uid, goal.id, !goal.done) }
    }

    fun deleteGoal(goalId: String) {
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch { firestoreRepo.deleteGoal(uid, goalId) }
    }

    fun addAlarm(time24: String, label: String) {
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            firestoreRepo.addAlarm(uid, time24, label)
                .onSuccess { _statusBanner.value = "Study alarm scheduled for $time24." }
        }
    }

    fun toggleAlarm(alarm: StudyAlarm) {
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch { firestoreRepo.toggleAlarm(uid, alarm.id, !alarm.active) }
    }

    fun deleteAlarm(alarmId: String) {
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch { firestoreRepo.deleteAlarm(uid, alarmId) }
    }

    // ── Flashcards & Chapter Mindmap Completion ──

    fun toggleFlashcardMastered(cardId: String) {
        val uid = _firebaseUser.value?.uid ?: return
        val current = _studyProgress.value
        val nextSet = if (cardId in current.masteredFlashcardIds) {
            current.masteredFlashcardIds - cardId
        } else {
            current.masteredFlashcardIds + cardId
        }
        viewModelScope.launch {
            firestoreRepo.saveStudyProgress(uid, current.copy(userId = uid, masteredFlashcardIds = nextSet))
        }
    }

    fun toggleChapterCompleted(chapterKey: String) {
        val uid = _firebaseUser.value?.uid ?: return
        val current = _studyProgress.value
        val next = if (chapterKey in current.completedChapters) {
            current.completedChapters - chapterKey
        } else {
            current.completedChapters + chapterKey
        }
        viewModelScope.launch {
            firestoreRepo.saveStudyProgress(uid, current.copy(userId = uid, completedChapters = next))
        }
    }

    // ── Daily Homework (PPT Slides + 2-Hour Delayed Answer PDF) ──

    fun getCurrentHomeworkSession(): HomeworkSession? {
        val dateKey = todayDateKey()
        val exam = effectiveExam()
        return _homeworkSessions.value.firstOrNull { it.dateKey == dateKey && it.exam == exam }
    }

    fun getDailyHomeworkQuestions(): List<QuestionItem> {
        val session = getCurrentHomeworkSession()
        val custom = _customQuestions.value
        return if (session != null && session.questionIds.isNotEmpty()) {
            QuestionBankRepository.findByIds(session.questionIds, custom)
        } else {
            QuestionBankRepository.getDailyToughHomework(effectiveExam(), todayDateKey(), custom, 10)
        }
    }

    fun ensureTodayHomeworkInitialized() {
        val uid = _firebaseUser.value?.uid ?: return
        if (getCurrentHomeworkSession() != null) return
        val exam = effectiveExam()
        val dateKey = todayDateKey()
        val questions = QuestionBankRepository.getDailyToughHomework(exam, dateKey, _customQuestions.value, 10)
        viewModelScope.launch {
            firestoreRepo.getOrCreateHomeworkSession(uid, dateKey, exam, questions.map { it.id })
        }
    }

    fun selectHomeworkAnswer(questionId: String, optionIndex: Int) {
        val uid = _firebaseUser.value?.uid ?: return
        val session = getCurrentHomeworkSession() ?: return
        val nextAnswers = session.answers.toMutableMap().apply { put(questionId, optionIndex.toLong()) }
        val completed = nextAnswers.size >= session.questionIds.size
        viewModelScope.launch {
            firestoreRepo.updateHomeworkProgress(uid, session.id, nextAnswers, completed, session.pdfExported)
            // Log missed questions into Improvement queue
            val qItem = QuestionBankRepository.findByIds(listOf(questionId), _customQuestions.value).firstOrNull()
            if (qItem != null && optionIndex != qItem.correctIndex) {
                val cur = _studyProgress.value
                if (questionId !in cur.missedQuestionIds) {
                    firestoreRepo.saveStudyProgress(
                        uid,
                        cur.copy(userId = uid, missedQuestionIds = (cur.missedQuestionIds + questionId).takeLast(300))
                    )
                }
            }
        }
    }

    fun homeworkUnlockRemainingMs(session: HomeworkSession?): Long {
        if (session == null || session.assignedAtMillis <= 0L) return HOMEWORK_ANSWER_DELAY_MS
        val elapsed = _nowMillis.value - session.assignedAtMillis
        return (HOMEWORK_ANSWER_DELAY_MS - elapsed).coerceAtLeast(0L)
    }

    fun generateAndPreviewHomeworkPdf() {
        val session = getCurrentHomeworkSession() ?: return
        if (homeworkUnlockRemainingMs(session) > 0L) {
            _statusBanner.value = "Answer PDF unlocks strictly after the 2-hour solving window."
            return
        }
        val uid = _firebaseUser.value?.uid ?: return
        val exam = effectiveExam()
        val studentName = _userProfile.value?.name ?: "Student"
        val questions = getDailyHomeworkQuestions()

        viewModelScope.launch {
            val preview = withContext(Dispatchers.IO) {
                val file = AnswerPdfGenerator.generateHomeworkAnswerPdf(
                    appContext, exam, session.dateKey, studentName, questions
                )
                val pages = AnswerPdfGenerator.renderPdfPagesForPreview(file)
                PdfPreviewState("$exam Answer Key · ${session.dateKey}", file, pages)
            }
            _pdfPreview.value = preview
            firestoreRepo.updateHomeworkProgress(uid, session.id, session.answers, session.completed, true)
        }
    }

    fun generateAndPreviewProgressReportPdf() {
        val profile = _userProfile.value
        val studentName = profile?.name ?: "Student"
        val exam = effectiveExam()
        val planLabel = profile?.plan ?: "1year"
        val attempts = _testAttempts.value
        viewModelScope.launch {
            val preview = withContext(Dispatchers.IO) {
                val file = AnswerPdfGenerator.generateProgressReportPdf(
                    appContext, studentName, exam, planLabel, attempts
                )
                val pages = AnswerPdfGenerator.renderPdfPagesForPreview(file)
                PdfPreviewState("Official Progress Report · $studentName", file, pages)
            }
            _pdfPreview.value = preview
        }
    }

    fun dismissPdfPreview() {
        _pdfPreview.value = null
    }

    // ── Tests, Custom Practice, Improvement Practice ──

    fun startTest(
        testType: String,
        title: String,
        subjectFilter: String = "All",
        chapterFilter: String = "All",
        difficultyFilter: String = "All",
        count: Int = 15,
        timedSeconds: Int = 0
    ) {
        val exam = effectiveExam()
        val questions = QuestionBankRepository.getTestQuestions(
            exam = exam,
            testType = testType,
            subjectFilter = subjectFilter,
            chapterFilter = chapterFilter,
            difficultyFilter = difficultyFilter,
            count = count,
            customQuestions = _customQuestions.value,
            missedIds = _studyProgress.value.missedQuestionIds
        )
        if (questions.isEmpty()) {
            _statusBanner.value = "No matching questions available for this filter."
            return
        }
        _activeQuiz.value = ActiveQuizState(
            testType = testType,
            title = title,
            exam = exam,
            questions = questions,
            timeLimitSeconds = timedSeconds
        )
        startQuizTimer()
    }

    private fun startQuizTimer() {
        quizTimerJob?.cancel()
        quizTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val cur = _activeQuiz.value ?: break
                if (cur.isFinished) break
                val nextElapsed = cur.secondsElapsed + 1
                _activeQuiz.value = cur.copy(secondsElapsed = nextElapsed)
                if (cur.timeLimitSeconds > 0 && nextElapsed >= cur.timeLimitSeconds) {
                    submitActiveQuiz()
                    break
                }
            }
        }
    }

    fun selectQuizAnswer(questionId: String, optionIndex: Int) {
        val cur = _activeQuiz.value ?: return
        if (cur.isFinished) return
        val next = cur.selectedAnswers.toMutableMap().apply { put(questionId, optionIndex) }
        _activeQuiz.value = cur.copy(selectedAnswers = next)
    }

    fun navigateQuizQuestion(index: Int) {
        val cur = _activeQuiz.value ?: return
        _activeQuiz.value = cur.copy(currentIndex = index.coerceIn(0, cur.questions.lastIndex))
    }

    fun submitActiveQuiz() {
        val cur = _activeQuiz.value ?: return
        if (cur.isFinished) return
        quizTimerJob?.cancel()

        val uid = _firebaseUser.value?.uid ?: return
        var score = 0L
        val newlyMissed = mutableListOf<String>()
        val serializedQuestions = cur.questions.mapIndexed { idx, q ->
            val selectedIdx = cur.selectedAnswers[q.id]
            val isCorrect = selectedIdx == q.correctIndex
            if (isCorrect) {
                score++
            } else {
                newlyMissed.add(q.id)
            }
            mapOf(
                "index" to idx,
                "id" to q.id,
                "subject" to q.subject,
                "chapter" to q.chapter,
                "difficulty" to q.difficulty,
                "questionText" to q.questionText,
                "options" to q.options,
                "correctIndex" to q.correctIndex,
                "correctOption" to (q.options.getOrNull(q.correctIndex) ?: ""),
                "selectedIndex" to (selectedIdx ?: -1),
                "selectedOption" to (if (selectedIdx != null) q.options.getOrNull(selectedIdx) ?: "" else "Skipped"),
                "isCorrect" to isCorrect,
                "explanation" to q.explanation
            )
        }
        val total = cur.questions.size.toLong().coerceAtLeast(1L)
        val pct = (score * 100L) / total
        val grade = when {
            pct >= 90 -> "A+"
            pct >= 75 -> "A"
            pct >= 60 -> "B"
            pct >= 45 -> "C"
            else -> "D"
        }
        val attempt = TestAttempt(
            userId = uid,
            exam = cur.exam,
            testType = cur.testType,
            title = cur.title,
            score = score,
            total = total,
            pct = pct,
            grade = grade,
            durationSeconds = cur.secondsElapsed.toLong(),
            questions = serializedQuestions,
            dateLabel = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date())
        )
        _activeQuiz.value = cur.copy(isFinished = true, savedAttempt = attempt)

        viewModelScope.launch {
            firestoreRepo.saveTestAttempt(uid, attempt)
            if (newlyMissed.isNotEmpty() || cur.testType == "improvement") {
                val curProgress = _studyProgress.value
                val resolvedMissed = if (cur.testType == "improvement") {
                    // Remove correctly answered items from missed list!
                    val masteredInRetry = cur.questions.filter { cur.selectedAnswers[it.id] == it.correctIndex }.map { it.id }.toSet()
                    (curProgress.missedQuestionIds - masteredInRetry + newlyMissed).distinct().takeLast(300)
                } else {
                    (curProgress.missedQuestionIds + newlyMissed).distinct().takeLast(300)
                }
                firestoreRepo.saveStudyProgress(uid, curProgress.copy(userId = uid, missedQuestionIds = resolvedMissed))
            }
        }
    }

    fun closeActiveQuiz() {
        quizTimerJob?.cancel()
        _activeQuiz.value = null
    }

    // ── Messaging, Admin Actions & AI Tutor ──

    fun sendStudentMessage(text: String) {
        val user = _firebaseUser.value ?: return
        if (text.isBlank()) return
        val name = _userProfile.value?.name ?: user.displayName ?: "Student"
        val timeLabel = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date())
        viewModelScope.launch {
            firestoreRepo.sendAdminMessage(user.uid, user.uid, "student", name, text.trim(), timeLabel)
        }
    }

    fun observeStudentMessagesAsAdmin(studentUid: String) {
        adminChatJob?.cancel()
        adminChatJob = viewModelScope.launch {
            firestoreRepo.observeAdminMessages(studentUid)
                .catch { }
                .collect { _adminSelectedStudentMessages.value = it }
        }
    }

    fun sendAdminReplyToStudent(studentUid: String, text: String) {
        val adminUid = _firebaseUser.value?.uid ?: return
        if (text.isBlank()) return
        val timeLabel = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date())
        viewModelScope.launch {
            firestoreRepo.sendAdminMessage(studentUid, adminUid, "admin", "Arsath (Teacher)", text.trim(), timeLabel)
        }
    }

    fun addStudentByAdmin(name: String, studentClass: String, parentName: String, contact: String, exam: String, plan: String) {
        viewModelScope.launch {
            val student = UserProfile(
                name = name.trim(),
                studentClass = studentClass,
                parentName = parentName.trim(),
                contact = contact.trim(),
                exam = exam,
                plan = plan,
                role = "student",
                active = true,
                joinDate = todayDateKey()
            )
            firestoreRepo.createStudentByAdmin(student)
                .onSuccess { _statusBanner.value = "Student $name registered in directory." }
                .onFailure { _statusBanner.value = it.localizedMessage }
        }
    }

    fun toggleStudentActiveByAdmin(student: UserProfile) {
        viewModelScope.launch {
            firestoreRepo.setStudentActive(student.userId, !student.active)
        }
    }

    fun addCustomVideoByAdmin(exam: String, subject: String, chapter: String, title: String, url: String, rating: Double) {
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            val video = CustomVideo(
                exam = exam,
                subject = subject,
                chapter = chapter,
                title = title.trim(),
                url = url.trim(),
                rating = rating,
                authorUid = uid
            )
            firestoreRepo.addCustomVideo(video)
                .onSuccess { _statusBanner.value = "Chapter video added & synced." }
                .onFailure { _statusBanner.value = it.localizedMessage }
        }
    }

    fun deleteCustomVideoByAdmin(videoId: String) {
        viewModelScope.launch {
            firestoreRepo.deleteCustomVideo(videoId)
        }
    }

    fun addCustomQuestionByAdmin(
        exam: String,
        subject: String,
        chapter: String,
        difficulty: String,
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String
    ) {
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            val cq = CustomQuestion(
                exam = exam,
                subject = subject,
                chapter = chapter,
                difficulty = difficulty,
                questionText = questionText.trim(),
                options = options.map { it.trim() },
                correctIndex = correctIndex.toLong(),
                explanation = explanation.trim(),
                authorUid = uid
            )
            firestoreRepo.addCustomQuestion(cq)
                .onSuccess { _statusBanner.value = "Question added to $exam $subject bank." }
                .onFailure { _statusBanner.value = it.localizedMessage }
        }
    }

    fun scheduleClassByAdmin(title: String, exam: String, subject: String, scheduleTime: String, meetUrl: String) {
        val uid = _firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            val sc = ScheduledClass(
                title = title.trim(),
                exam = exam,
                subject = subject,
                scheduleTime = scheduleTime.trim(),
                meetUrl = meetUrl.trim().ifBlank { "https://meet.google.com/new" },
                authorUid = uid
            )
            firestoreRepo.addScheduledClass(sc)
                .onSuccess { _statusBanner.value = "Live class scheduled!" }
                .onFailure { _statusBanner.value = it.localizedMessage }
        }
    }

    fun askAiTutor(question: String) {
        if (question.isBlank()) return
        val exam = effectiveExam()
        val name = _userProfile.value?.name ?: "Aspirant"
        val timeLabel = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
        _aiChatHistory.value = _aiChatHistory.value + AiChatTurn(isUser = true, text = question.trim(), timestamp = timeLabel)

        viewModelScope.launch {
            try {
                aiTutorRepo.askTutorStream(question.trim(), exam, name).collect { state ->
                    _aiState.value = state
                    if (state is AiTutorUiState.Success) {
                        val replyTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
                        _aiChatHistory.value = _aiChatHistory.value + AiChatTurn(
                            isUser = false,
                            text = state.text,
                            timestamp = replyTime
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            }
        }
    }

    private fun startClockLoop() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _nowMillis.value = System.currentTimeMillis()
            }
        }
    }

    private fun sendLocalNotification(title: String, message: String) {
        runCatching {
            val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val ch = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Study Planner Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                )
                nm.createNotificationChannel(ch)
            }
            val notif = NotificationCompat.Builder(appContext, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .build()
            nm.notify(System.currentTimeMillis().toInt(), notif)
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StudyPlannerViewModel(context.applicationContext) as T
        }
    }
}
