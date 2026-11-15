package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AppSymbolCrest
import com.example.ui.screens.AdminConsoleScreen
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.DailyHomeworkScreen
import com.example.ui.screens.FocusTimerAndGoalsScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.OnboardingSetupScreen
import com.example.ui.screens.PdfPreviewDialog
import com.example.ui.screens.ProfileAndCreatorScreen
import com.example.ui.screens.PtmAndLiveClassScreen
import com.example.ui.screens.SignInScreen
import com.example.ui.screens.SubjectSelectionScreen
import com.example.ui.screens.SyllabusPlannerScreen
import com.example.ui.screens.TestsAndPracticeScreen
import com.example.ui.screens.VideosAndStudyToolsScreen
import com.example.viewmodel.StudyPlannerViewModel

object Routes {
    const val HOME = "home"
    const val SUBJECTS = "subjects"
    const val STUDY_TOOLS = "study_tools"
    const val TESTS = "tests"
    const val HOMEWORK = "homework"
    const val AI_TUTOR = "ai_tutor"
    const val PLANNER = "planner"
    const val TIMER = "timer"
    const val PTM = "ptm"
    const val PROFILE = "profile"
    const val ADMIN = "admin"
}

private data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    viewModel: StudyPlannerViewModel,
    isDarkTheme: Boolean
) {
    val context = LocalContext.current
    val firebaseUser by viewModel.firebaseUser.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val profileLoadedOnce by viewModel.profileLoadedOnce.collectAsStateWithLifecycle()
    val pdfPreview by viewModel.pdfPreview.collectAsStateWithLifecycle()

    if (firebaseUser == null) {
        SignInScreen(
            isLoading = authLoading,
            bannerMessage = statusBanner,
            onReturningLogin = { viewModel.signInWithGoogle(context, preferReturningSilent = true) },
            onGoogleSignIn = { viewModel.signInWithGoogle(context, preferReturningSilent = false) },
            onDismissBanner = { viewModel.clearBanner() }
        )
        return
    }

    if (!profileLoadedOnce) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppSymbolCrest(size = 76.dp)
                CircularProgressIndicator()
                Text("Restoring your cloud study vault...", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    if (userProfile == null) {
        OnboardingSetupScreen(
            firebaseUser = firebaseUser!!,
            onComplete = { name, cls, parent, contact, exam, plan, reqRole ->
                viewModel.completeOnboarding(name, cls, parent, contact, exam, plan, reqRole)
            },
            onSignOut = { viewModel.signOut(context) }
        )
        return
    }

    // Collect authenticated state streams
    val testAttempts by viewModel.testAttempts.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val homeworkSessions by viewModel.homeworkSessions.collectAsStateWithLifecycle()
    val studyProgress by viewModel.studyProgress.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val customVideos by viewModel.customVideos.collectAsStateWithLifecycle()
    val scheduledClasses by viewModel.scheduledClasses.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val adminStudentMsgs by viewModel.adminSelectedStudentMessages.collectAsStateWithLifecycle()
    val activeQuiz by viewModel.activeQuiz.collectAsStateWithLifecycle()
    val nowMillis by viewModel.nowMillis.collectAsStateWithLifecycle()
    val timerMode by viewModel.timerMode.collectAsStateWithLifecycle()
    val timerRunning by viewModel.timerRunning.collectAsStateWithLifecycle()
    val isBreakPhase by viewModel.isBreakPhase.collectAsStateWithLifecycle()
    val timerSeconds by viewModel.timerSeconds.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    val aiHistory by viewModel.aiChatHistory.collectAsStateWithLifecycle()

    val examCode = viewModel.effectiveExam()
    val isAdmin = viewModel.isAuthorizedAdmin()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    fun navigateTo(route: String) {
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(Routes.HOME) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    if (currentRoute != Routes.HOME) {
        BackHandler(enabled = activeQuiz == null && pdfPreview == null) {
            navigateTo(Routes.HOME)
        }
    }

    val primaryTabs = listOf(
        NavItem(Routes.HOME, "Home", Icons.Filled.Home),
        NavItem(Routes.STUDY_TOOLS, "Learn", Icons.Filled.MenuBook),
        NavItem(Routes.TESTS, "Tests", Icons.Filled.Quiz),
        NavItem(Routes.HOMEWORK, "PPT HW", Icons.Filled.Slideshow),
        NavItem(Routes.AI_TUTOR, "AI Tutor", Icons.Filled.AutoAwesome)
    )

    val allDestinations = buildList {
        addAll(primaryTabs)
        add(NavItem(Routes.SUBJECTS, "Subjects", Icons.Filled.School))
        add(NavItem(Routes.PLANNER, "Roadmap", Icons.Filled.CalendarMonth))
        add(NavItem(Routes.TIMER, "Timer", Icons.Filled.Timer))
        add(NavItem(Routes.PTM, "PTM & Live", Icons.Filled.VideoCall))
        add(NavItem(Routes.PROFILE, "Profile", Icons.Filled.Person))
        if (isAdmin) {
            add(NavItem(Routes.ADMIN, "Admin", Icons.Filled.AdminPanelSettings))
        }
    }

    var focusedToolMode by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("videos") }
    var focusedToolSubject by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    pdfPreview?.let { preview ->
        PdfPreviewDialog(preview = preview, onDismiss = { viewModel.dismissPdfPreview() })
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 640.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AppSymbolCrest(size = 36.dp)
                            Column {
                                Text(
                                    text = "Study Planner",
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${userProfile?.name ?: "Learner"} • $examCode • by Mohammed Arsath H",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.toggleTheme(!isDarkTheme) },
                            modifier = Modifier.testTag("topbar_theme_button")
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Toggle Light or Dark Theme"
                            )
                        }
                        IconButton(
                            onClick = { navigateTo(Routes.PROFILE) },
                            modifier = Modifier.testTag("topbar_profile_button")
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = "Open Profile & Creator Card")
                        }
                    }
                )
            },
            bottomBar = {
                if (!isWide) {
                    NavigationBar {
                        primaryTabs.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = { navigateTo(item.route) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label, maxLines = 1) },
                                modifier = Modifier.testTag("nav_${item.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWide) {
                    NavigationRail(modifier = Modifier.fillMaxHeight()) {
                        allDestinations.forEach { item ->
                            NavigationRailItem(
                                selected = currentRoute == item.route,
                                onClick = { navigateTo(item.route) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label, maxLines = 1) }
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                    if (!isWide) {
                        // Compact quick hub switcher for secondary tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allDestinations.filter { it !in primaryTabs }.forEach { item ->
                                FilterChip(
                                    selected = currentRoute == item.route,
                                    onClick = { navigateTo(item.route) },
                                    leadingIcon = {
                                        Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(16.dp))
                                    },
                                    label = { Text(item.label) },
                                    modifier = Modifier.testTag("hub_chip_${item.route}")
                                )
                            }
                        }
                    }

                    statusBanner?.let { msg ->
                        Snackbar(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            action = {
                                TextButton(onClick = { viewModel.clearBanner() }) {
                                    Text("OK")
                                }
                            }
                        ) {
                            Text(msg)
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Routes.HOME,
                        modifier = Modifier.weight(1f)
                    ) {
                        composable(Routes.HOME) {
                            HomeDashboardScreen(
                                profile = userProfile,
                                examCode = examCode,
                                testAttempts = testAttempts,
                                goals = goals,
                                alarms = alarms,
                                messages = messages,
                                masteredCardsCount = studyProgress.masteredFlashcardIds.size,
                                missedCount = studyProgress.missedQuestionIds.size,
                                onSwitchExam = { viewModel.switchExamTrack(it) },
                                onNavigateTab = { navigateTo(it) },
                                onAddAlarm = { t, lbl -> viewModel.addAlarm(t, lbl) },
                                onToggleAlarm = { viewModel.toggleAlarm(it) },
                                onDeleteAlarm = { viewModel.deleteAlarm(it) },
                                onSendMessage = { viewModel.sendStudentMessage(it) }
                            )
                        }
                        composable(Routes.SUBJECTS) {
                            SubjectSelectionScreen(
                                examCode = examCode,
                                completedChapters = studyProgress.completedChapters,
                                onSelectStream = { viewModel.switchExamTrack(it) },
                                onToggleChapter = { viewModel.toggleChapterCompleted(it) },
                                onPracticeSubject = { stream, subject ->
                                    viewModel.switchExamTrack(stream)
                                    viewModel.startTest(
                                        testType = "custom",
                                        title = "$stream $subject Drill (15 Qs)",
                                        subjectFilter = subject,
                                        chapterFilter = "All",
                                        difficultyFilter = "All",
                                        count = 15,
                                        timedSeconds = 15 * 60
                                    )
                                    navigateTo(Routes.TESTS)
                                },
                                onPracticeChapter = { stream, subject, chapter ->
                                    viewModel.switchExamTrack(stream)
                                    viewModel.startTest(
                                        testType = "custom",
                                        title = "$subject · $chapter (10 Qs)",
                                        subjectFilter = subject,
                                        chapterFilter = chapter,
                                        difficultyFilter = "All",
                                        count = 10,
                                        timedSeconds = 10 * 60
                                    )
                                    navigateTo(Routes.TESTS)
                                },
                                onOpenSubjectTool = { subject, toolMode ->
                                    focusedToolSubject = subject
                                    focusedToolMode = toolMode
                                    navigateTo(Routes.STUDY_TOOLS)
                                }
                            )
                        }
                        composable(Routes.PLANNER) {
                            SyllabusPlannerScreen(
                                examCode = examCode,
                                initialPlanCode = userProfile?.plan ?: "1year",
                                completedChapters = studyProgress.completedChapters,
                                onToggleChapter = { viewModel.toggleChapterCompleted(it) }
                            )
                        }
                        composable(Routes.STUDY_TOOLS) {
                            VideosAndStudyToolsScreen(
                                examCode = examCode,
                                customVideos = customVideos,
                                masteredFlashcardIds = studyProgress.masteredFlashcardIds,
                                onToggleFlashcardMastered = { viewModel.toggleFlashcardMastered(it) },
                                initialToolMode = focusedToolMode,
                                initialSubject = focusedToolSubject
                            )
                        }
                        composable(Routes.TESTS) {
                            TestsAndPracticeScreen(
                                examCode = examCode,
                                activeQuiz = activeQuiz,
                                attempts = testAttempts,
                                missedQuestionIds = studyProgress.missedQuestionIds,
                                onStartTest = { t, title, sub, chap, diff, cnt, limit ->
                                    viewModel.startTest(t, title, sub, chap, diff, cnt, limit)
                                },
                                onSelectQuizAnswer = { qId, opt -> viewModel.selectQuizAnswer(qId, opt) },
                                onNavigateQuestion = { viewModel.navigateQuizQuestion(it) },
                                onSubmitQuiz = { viewModel.submitActiveQuiz() },
                                onCloseQuiz = { viewModel.closeActiveQuiz() },
                                onExportReportPdf = { viewModel.generateAndPreviewProgressReportPdf() }
                            )
                        }
                        composable(Routes.HOMEWORK) {
                            val todaySession = homeworkSessions.firstOrNull {
                                it.dateKey == viewModel.todayDateKey() && it.exam == examCode
                            }
                            val remaining = if (todaySession == null || todaySession.assignedAtMillis <= 0L) {
                                StudyPlannerViewModel.HOMEWORK_ANSWER_DELAY_MS
                            } else {
                                (StudyPlannerViewModel.HOMEWORK_ANSWER_DELAY_MS - (nowMillis - todaySession.assignedAtMillis)).coerceAtLeast(0L)
                            }
                            DailyHomeworkScreen(
                                examCode = examCode,
                                session = todaySession,
                                questions = viewModel.getDailyHomeworkQuestions(),
                                remainingMs = remaining,
                                onEnsureInitialized = { viewModel.ensureTodayHomeworkInitialized() },
                                onSelectAnswer = { qId, opt -> viewModel.selectHomeworkAnswer(qId, opt) },
                                onGenerateAnswerPdf = { viewModel.generateAndPreviewHomeworkPdf() }
                            )
                        }
                        composable(Routes.TIMER) {
                            FocusTimerAndGoalsScreen(
                                examCode = examCode,
                                timerMode = timerMode,
                                timerSeconds = timerSeconds,
                                timerRunning = timerRunning,
                                isBreakPhase = isBreakPhase,
                                sessionsCount = userProfile?.sessionsCount ?: 0L,
                                initialNotes = studyProgress.sessionNotes,
                                goals = goals,
                                onSelectMode = { viewModel.selectTimerMode(it) },
                                onToggleRun = { viewModel.toggleTimerRunning() },
                                onResetTimer = { viewModel.resetTimer() },
                                onSaveNotes = { viewModel.saveSessionNotes(it) },
                                onAddGoal = { title, sub, prio -> viewModel.addGoal(title, sub, prio) },
                                onToggleGoal = { viewModel.toggleGoal(it) },
                                onDeleteGoal = { viewModel.deleteGoal(it) }
                            )
                        }
                        composable(Routes.AI_TUTOR) {
                            AiTutorScreen(
                                examCode = examCode,
                                aiState = aiState,
                                chatHistory = aiHistory,
                                onAskTutor = { viewModel.askAiTutor(it) }
                            )
                        }
                        composable(Routes.PTM) {
                            PtmAndLiveClassScreen(
                                examCode = examCode,
                                scheduledClasses = scheduledClasses,
                                messages = messages,
                                onSendMessage = { viewModel.sendStudentMessage(it) }
                            )
                        }
                        composable(Routes.PROFILE) {
                            ProfileAndCreatorScreen(
                                profile = userProfile,
                                userEmail = firebaseUser?.email.orEmpty(),
                                isDarkTheme = isDarkTheme,
                                examCode = examCode,
                                onToggleTheme = { viewModel.toggleTheme(it) },
                                onSwitchExam = { viewModel.switchExamTrack(it) },
                                onExportReportPdf = { viewModel.generateAndPreviewProgressReportPdf() },
                                onSignOut = { viewModel.signOut(context) }
                            )
                        }
                        composable(Routes.ADMIN) {
                            if (isAdmin) {
                                AdminConsoleScreen(
                                    students = allStudents,
                                    customVideos = customVideos,
                                    scheduledClasses = scheduledClasses,
                                    selectedStudentMessages = adminStudentMsgs,
                                    onObserveStudentChat = { viewModel.observeStudentMessagesAsAdmin(it) },
                                    onSendAdminReply = { uid, msg -> viewModel.sendAdminReplyToStudent(uid, msg) },
                                    onAddStudent = { n, cls, p, c, ex, pl -> viewModel.addStudentByAdmin(n, cls, p, c, ex, pl) },
                                    onToggleStudentActive = { viewModel.toggleStudentActiveByAdmin(it) },
                                    onAddVideo = { ex, sub, ch, t, u, r -> viewModel.addCustomVideoByAdmin(ex, sub, ch, t, u, r) },
                                    onDeleteVideo = { viewModel.deleteCustomVideoByAdmin(it) },
                                    onAddQuestion = { ex, sub, ch, d, qt, opts, ci, exp -> viewModel.addCustomQuestionByAdmin(ex, sub, ch, d, qt, opts, ci, exp) },
                                    onScheduleClass = { t, ex, sub, tm, url -> viewModel.scheduleClassByAdmin(t, ex, sub, tm, url) }
                                )
                            } else {
                                Text("Admin access is restricted to authorized Arsath Coaching Centre instructors.", modifier = Modifier.padding(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
