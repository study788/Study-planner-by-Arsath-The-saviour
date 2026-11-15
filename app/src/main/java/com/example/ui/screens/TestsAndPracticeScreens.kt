package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.content.QuestionBankRepository
import com.example.data.content.SyllabusChapters
import com.example.data.model.ExamType
import com.example.data.model.TestAttempt
import com.example.ui.components.SectionHeaderCard
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.viewmodel.ActiveQuizState

@Composable
fun TestsAndPracticeScreen(
    examCode: String,
    activeQuiz: ActiveQuizState?,
    attempts: List<TestAttempt>,
    missedQuestionIds: List<String>,
    onStartTest: (
        testType: String,
        title: String,
        subject: String,
        chapter: String,
        difficulty: String,
        count: Int,
        timedSeconds: Int
    ) -> Unit,
    onSelectQuizAnswer: (String, Int) -> Unit,
    onNavigateQuestion: (Int) -> Unit,
    onSubmitQuiz: () -> Unit,
    onCloseQuiz: () -> Unit,
    onExportReportPdf: () -> Unit
) {
    if (activeQuiz != null) {
        BackHandler { onCloseQuiz() }
        ActiveQuizRunnerScreen(
            quiz = activeQuiz,
            onSelectAnswer = onSelectQuizAnswer,
            onNavigateQuestion = onNavigateQuestion,
            onSubmitQuiz = onSubmitQuiz,
            onCloseQuiz = onCloseQuiz
        )
        return
    }

    val examEnum = if (examCode == "NEET") ExamType.NEET else ExamType.JEE
    val subjects = listOf("All") + examEnum.subjects()
    var customSubject by remember(examCode) { mutableStateOf("All") }
    var customChapter by remember(examCode, customSubject) { mutableStateOf("All") }
    var customDifficulty by remember { mutableStateOf("All") }
    var customCount by remember { mutableIntStateOf(15) }
    var customTimed by remember { mutableStateOf(true) }
    var expandedAttemptId by remember { mutableStateOf<String?>(null) }

    val availableChapters = remember(examCode, customSubject) {
        if (customSubject == "All") listOf("All") else listOf("All") + SyllabusChapters.chaptersFor(examCode, customSubject)
    }
    val qCounts = remember(examCode) { QuestionBankRepository.getCountsSummary(examCode) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier.widthIn(max = 880.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeaderCard(
                    title = "$examCode Exam & Practice Arena",
                    subtitle = "${qCounts.values.sum()}+ Competitive Questions (≥1,050/subject) • Every Exam Question & Answer Saved"
                )

                // Preset Exams
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Standard Exam Modes", style = MaterialTheme.typography.titleMedium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onStartTest("daily", "Daily $examCode Sprint (15 Qs)", "All", "All", "All", 15, 15 * 60)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("start_daily_test_btn")
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Start Daily Test")
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Daily (15Q)")
                            }
                            Button(
                                onClick = {
                                    onStartTest("weekly", "Weekly Sunday Test (30 Qs)", "All", "All", "Medium", 30, 35 * 60)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("start_weekly_test_btn")
                            ) { Text("Weekly (30Q)") }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    onStartTest("mock", "Full $examCode Grand Mock (45 Qs)", "All", "All", "All", 45, 60 * 60)
                                },
                                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                            ) { Text("Full Mock (45Q)") }

                            OutlinedButton(
                                onClick = {
                                    onStartTest("improvement", "Improvement Practice (Mistake Recovery)", "All", "All", "Tough", 15, 0)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("start_improvement_btn")
                            ) {
                                Icon(Icons.Filled.TrendingUp, contentDescription = "Improvement Practice")
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Improve (${missedQuestionIds.size})")
                            }
                        }
                    }
                }

                // Custom Practice Builder
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Build, contentDescription = "Custom Practice", tint = MaterialTheme.colorScheme.primary)
                            Text("Custom Practice Builder", style = MaterialTheme.typography.titleMedium)
                        }
                        Text("Subject", style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            subjects.forEach { s ->
                                FilterChip(
                                    selected = customSubject == s,
                                    onClick = { customSubject = s; customChapter = "All" },
                                    label = { Text(s) }
                                )
                            }
                        }

                        if (availableChapters.size > 1) {
                            Text("Chapter (Tap to cycle or keep All)", style = MaterialTheme.typography.labelLarge)
                            val curChapIdx = availableChapters.indexOf(customChapter).coerceAtLeast(0)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(
                                    onClick = { customChapter = availableChapters[(curChapIdx + 1) % availableChapters.size] }
                                ) { Text("Chapter: $customChapter") }
                                if (customChapter != "All") {
                                    OutlinedButton(onClick = { customChapter = "All" }) { Text("Reset") }
                                }
                            }
                        }

                        Text("Difficulty", style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("All", "Easy", "Medium", "Tough").forEach { d ->
                                FilterChip(
                                    selected = customDifficulty == d,
                                    onClick = { customDifficulty = d },
                                    label = { Text(d) }
                                )
                            }
                        }

                        Text("Question Count & Timing", style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(10, 15, 25, 45).forEach { cnt ->
                                FilterChip(
                                    selected = customCount == cnt,
                                    onClick = { customCount = cnt },
                                    label = { Text("$cnt Qs") }
                                )
                            }
                            FilterChip(
                                selected = customTimed,
                                onClick = { customTimed = !customTimed },
                                label = { Text(if (customTimed) "Timed" else "Untimed") }
                            )
                        }

                        Button(
                            onClick = {
                                val title = "Custom: $customSubject ($customDifficulty · $customCount Qs)"
                                val limit = if (customTimed) customCount * 90 else 0
                                onStartTest("custom", title, customSubject, customChapter, customDifficulty, customCount, limit)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("launch_custom_practice_btn")
                        ) { Text("Launch Custom Practice") }
                    }
                }

                // Report Card Header + PDF Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saved Exam Attempts (${attempts.size})", style = MaterialTheme.typography.titleLarge)
                    OutlinedButton(
                        onClick = onExportReportPdf,
                        modifier = Modifier.testTag("export_report_pdf_btn")
                    ) {
                        Icon(Icons.Filled.PictureAsPdf, contentDescription = "Export Report PDF")
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Report PDF")
                    }
                }

                if (attempts.isEmpty()) {
                    Text(
                        "No test attempts recorded yet. Every question & answer you submit will be saved here for exam review.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    attempts.forEach { att ->
                        val expanded = expandedAttemptId == att.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedAttemptId = if (expanded) null else att.id },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(att.title, style = MaterialTheme.typography.titleMedium)
                                        Text("${att.exam} • ${att.dateLabel} • Grade ${att.grade}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        text = "${att.score}/${att.total} (${att.pct}%)",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (att.pct >= 70) EmeraldGreen else GoldAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                        contentDescription = "Inspect Exam Questions"
                                    )
                                }
                                AnimatedVisibility(visible = expanded) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        att.questions.forEachIndexed { qIdx, qMap ->
                                            val stem = (qMap["questionText"] as? String).orEmpty()
                                            val chosen = (qMap["selectedOption"] as? String).orEmpty()
                                            val correct = (qMap["correctOption"] as? String).orEmpty()
                                            val explanation = (qMap["explanation"] as? String).orEmpty()
                                            val isOk = (qMap["isCorrect"] as? Boolean) ?: false
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = (if (isOk) EmeraldGreen else CrimsonRed).copy(alpha = 0.09f),
                                                border = BorderStroke(1.dp, (if (isOk) EmeraldGreen else CrimsonRed).copy(alpha = 0.4f))
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                    Text("Q${qIdx + 1}. $stem", style = MaterialTheme.typography.labelLarge)
                                                    Text("Your Answer: $chosen  |  Correct: $correct", style = MaterialTheme.typography.bodySmall, color = if (isOk) EmeraldGreen else CrimsonRed)
                                                    if (explanation.isNotBlank()) {
                                                        Text("Solution: $explanation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ActiveQuizRunnerScreen(
    quiz: ActiveQuizState,
    onSelectAnswer: (String, Int) -> Unit,
    onNavigateQuestion: (Int) -> Unit,
    onSubmitQuiz: () -> Unit,
    onCloseQuiz: () -> Unit
) {
    if (quiz.isFinished) {
        val attempt = quiz.savedAttempt
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, EmeraldGreen)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Test Submitted & Saved!", style = MaterialTheme.typography.headlineSmall, color = EmeraldGreen)
                        if (attempt != null) {
                            Text("${attempt.title} — ${attempt.score}/${attempt.total} (${attempt.pct}% · Grade ${attempt.grade})", style = MaterialTheme.typography.titleMedium)
                        }
                        Button(
                            onClick = onCloseQuiz,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) { Text("Back to Exam Center") }
                    }
                }
            }
            items(quiz.questions.size) { idx ->
                val q = quiz.questions[idx]
                val chosen = quiz.selectedAnswers[q.id]
                val isOk = chosen == q.correctIndex
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isOk) EmeraldGreen else CrimsonRed)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Q${idx + 1}. [${q.subject} · ${q.difficulty}] ${q.questionText}", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Yours: ${chosen?.let { q.options.getOrNull(it) } ?: "Skipped"}  |  Correct: ${q.options[q.correctIndex]}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOk) EmeraldGreen else CrimsonRed
                        )
                        Text("Explanation: ${q.explanation}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        return
    }

    val currentQ = quiz.questions[quiz.currentIndex]
    val selectedIdx = quiz.selectedAnswers[currentQ.id]
    val progress = (quiz.currentIndex + 1).toFloat() / quiz.questions.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(quiz.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            val mm = quiz.secondsElapsed / 60
            val ss = quiz.secondsElapsed % 60
            Text(String.format("%02d:%02d", mm, ss), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        Text(
            "Question ${quiz.currentIndex + 1} of ${quiz.questions.size} • ${currentQ.subject} • ${currentQ.chapter} (${currentQ.difficulty})",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(currentQ.questionText, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(currentQ.options.size) { optIdx ->
                    val chosen = selectedIdx == optIdx
                    val letter = listOf("A", "B", "C", "D")[optIdx]
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .clickable { onSelectAnswer(currentQ.id, optIdx) }
                            .testTag("quiz_option_$optIdx"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (chosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.5.dp, if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("$letter.", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(currentQ.options[optIdx], style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            if (chosen) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { onNavigateQuestion(quiz.currentIndex - 1) },
                enabled = quiz.currentIndex > 0,
                modifier = Modifier.weight(1f).heightIn(min = 48.dp)
            ) { Text("Prev") }

            if (quiz.currentIndex < quiz.questions.lastIndex) {
                Button(
                    onClick = { onNavigateQuestion(quiz.currentIndex + 1) },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                ) { Text("Next") }
            }
            Button(
                onClick = onSubmitQuiz,
                modifier = Modifier
                    .weight(1.2f)
                    .heightIn(min = 48.dp)
                    .testTag("submit_quiz_button")
            ) { Text("Submit Test") }
        }
    }
}
