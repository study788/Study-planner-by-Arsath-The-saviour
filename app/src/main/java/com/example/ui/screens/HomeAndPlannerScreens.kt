package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.content.CurriculumData
import com.example.data.content.QuestionBankRepository
import com.example.data.model.AdminMessage
import com.example.data.model.ExamType
import com.example.data.model.PlanType
import com.example.data.model.StudyAlarm
import com.example.data.model.StudyGoal
import com.example.data.model.TestAttempt
import com.example.data.model.UserProfile
import com.example.ui.components.CreatorSignatureStrip
import com.example.ui.components.MottoQuoteBanner
import com.example.ui.components.SectionHeaderCard
import com.example.ui.components.StatTileCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import java.util.Calendar

@Composable
fun HomeDashboardScreen(
    profile: UserProfile?,
    examCode: String,
    testAttempts: List<TestAttempt>,
    goals: List<StudyGoal>,
    alarms: List<StudyAlarm>,
    messages: List<AdminMessage>,
    masteredCardsCount: Int,
    missedCount: Int,
    onSwitchExam: (String) -> Unit,
    onNavigateTab: (String) -> Unit,
    onAddAlarm: (String, String) -> Unit,
    onToggleAlarm: (StudyAlarm) -> Unit,
    onDeleteAlarm: (String) -> Unit,
    onSendMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val examEnum = if (examCode == "NEET") ExamType.NEET else ExamType.JEE
    val dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    val quote = CurriculumData.dailyQuote(dayOfMonth)
    val qCounts = remember(examCode) { QuestionBankRepository.getCountsSummary(examCode) }

    var alarmTime by remember { mutableStateOf("06:00") }
    var alarmLabel by remember { mutableStateOf("Morning Formula Revision") }
    var chatDraft by remember { mutableStateOf("") }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        onAddAlarm(alarmTime, alarmLabel)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier.widthIn(max = 860.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MottoQuoteBanner(emoji = quote.emoji, quote = quote.quote, author = quote.author)

                // Quick Exam Toggle & QBank Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = examCode == "JEE",
                        onClick = { onSwitchExam("JEE") },
                        label = { Text("JEE Track") },
                        modifier = Modifier.testTag("home_exam_jee")
                    )
                    FilterChip(
                        selected = examCode == "NEET",
                        onClick = { onSwitchExam("NEET") },
                        label = { Text("NEET Track") },
                        modifier = Modifier.testTag("home_exam_neet")
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${qCounts.values.sum()}+ Qs Loaded",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 4 Stat Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTileCard(
                        emoji = "🔥",
                        value = "${profile?.sessionsCount ?: 0}",
                        label = "Focus Blocks",
                        accent = GoldAccent,
                        modifier = Modifier.weight(1f)
                    )
                    val avgPct = if (testAttempts.isEmpty()) 0 else testAttempts.map { it.pct }.average().toInt()
                    StatTileCard(
                        emoji = "🎯",
                        value = "${avgPct}%",
                        label = "Test Avg (${testAttempts.size})",
                        accent = ElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val doneGoals = goals.count { it.done }
                    StatTileCard(
                        emoji = "✅",
                        value = "$doneGoals/${goals.size}",
                        label = "Weekly Goals",
                        accent = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatTileCard(
                        emoji = "🃏",
                        value = "$masteredCardsCount",
                        label = "Cards Mastered",
                        accent = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Quick Launch Action Strip
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Quick Launch", style = MaterialTheme.typography.titleMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onNavigateTab("homework") },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("quick_homework_btn")
                            ) {
                                Icon(Icons.Filled.Slideshow, contentDescription = "Daily Homework PPT")
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Daily PPT HW")
                            }
                            OutlinedButton(
                                onClick = { onNavigateTab("tests") },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("quick_tests_btn")
                            ) {
                                Icon(Icons.Filled.Quiz, contentDescription = "Practice & Tests")
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(if (missedCount > 0) "Improve ($missedCount)" else "Take Test")
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onNavigateTab("timer") },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Focus Timer")
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Start Focus")
                            }
                            OutlinedButton(
                                onClick = { onNavigateTab("ai_tutor") },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                            ) {
                                Text("Arsath AI Tutor")
                            }
                        }
                        OutlinedButton(
                            onClick = { onNavigateTab("subjects") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("quick_subjects_btn")
                        ) {
                            Text("Explore Streams & Subjects (JEE / NEET Drill-Down)")
                        }
                    }
                }

                // Question Bank Depth Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${examEnum.label} Question Vault",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        qCounts.forEach { (sub, cnt) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(sub, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "$cnt questions ready",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EmeraldGreen
                                )
                            }
                        }
                    }
                }

                // Study Alarms Section
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Alarm, contentDescription = "Study Alarms", tint = MaterialTheme.colorScheme.primary)
                            Text("Study Alarms", style = MaterialTheme.typography.titleMedium)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = alarmTime,
                                onValueChange = { alarmTime = it.take(5) },
                                label = { Text("Time (HH:MM)") },
                                singleLine = true,
                                modifier = Modifier.weight(0.4f)
                            )
                            OutlinedTextField(
                                value = alarmLabel,
                                onValueChange = { alarmLabel = it },
                                label = { Text("Label") },
                                singleLine = true,
                                modifier = Modifier.weight(0.6f)
                            )
                        }
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    onAddAlarm(alarmTime, alarmLabel)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("add_alarm_button")
                        ) {
                            Text("Set Study Alarm")
                        }
                        if (alarms.isEmpty()) {
                            Text(
                                "No study alarms scheduled yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            alarms.forEach { alarm ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = alarm.time24,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Text(
                                        text = alarm.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Switch(
                                        checked = alarm.active,
                                        onCheckedChange = { onToggleAlarm(alarm) }
                                    )
                                    IconButton(onClick = { onDeleteAlarm(alarm.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete Alarm")
                                    }
                                }
                            }
                        }
                    }
                }

                // Direct Teacher Message Box
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Message Arsath (Teacher)", style = MaterialTheme.typography.titleMedium)
                        val recentMsgs = messages.takeLast(4)
                        if (recentMsgs.isEmpty()) {
                            Text(
                                "Send a study doubt or update directly to Mohammed Arsath H.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            recentMsgs.forEach { msg ->
                                Text(
                                    text = "${msg.senderName}: ${msg.message}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (msg.senderRole == "admin") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = chatDraft,
                                onValueChange = { chatDraft = it },
                                label = { Text("Ask teacher...") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    if (chatDraft.isNotBlank()) {
                                        onSendMessage(chatDraft)
                                        chatDraft = ""
                                    }
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Teacher Message")
                            }
                        }
                    }
                }

                CreatorSignatureStrip()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SyllabusPlannerScreen(
    examCode: String,
    initialPlanCode: String,
    completedChapters: List<String>,
    onToggleChapter: (String) -> Unit
) {
    val examEnum = if (examCode == "NEET") ExamType.NEET else ExamType.JEE
    var planType by remember(initialPlanCode) {
        mutableStateOf(if (initialPlanCode == "2year") PlanType.TWO_YEAR else PlanType.ONE_YEAR)
    }
    val rows = remember(examEnum, planType) { CurriculumData.getPlanRows(examEnum, planType) }
    val resources = remember(examEnum) { CurriculumData.getEssentialResources(examEnum) }
    val schedule = remember(examEnum) { CurriculumData.getDailySchedule(examEnum) }

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
                modifier = Modifier.widthIn(max = 860.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeaderCard(
                    title = "${examEnum.label} Study Roadmap",
                    subtitle = "${planType.title} (${planType.subtitle}) — Tap any month card to mark mastered!"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = planType == PlanType.ONE_YEAR,
                        onClick = { planType = PlanType.ONE_YEAR },
                        label = { Text("1-Year Intensive") }
                    )
                    FilterChip(
                        selected = planType == PlanType.TWO_YEAR,
                        onClick = { planType = PlanType.TWO_YEAR },
                        label = { Text("2-Year Comprehensive") }
                    )
                }

                // Daily Schedule Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Ideal Daily Study Timetable", style = MaterialTheme.typography.titleMedium)
                        schedule.forEach { (slot, task, hex) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(hex), RoundedCornerShape(3.dp))
                                )
                                Text(
                                    text = slot,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(hex),
                                    modifier = Modifier.widthIn(min = 66.dp)
                                )
                                Text(text = task, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // Month-by-Month Planner Rows
                rows.forEach { row ->
                    val key = "${examCode}_${planType.code}_${row.month}"
                    val done = key in completedChapters
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleChapter(key) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (done) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (done) EmeraldGreen else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = row.month,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (done) "COMPLETED" else "TAP TO CHECK",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (done) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text("Physics: ${row.physics}", style = MaterialTheme.typography.bodySmall)
                            Text("Chemistry: ${row.chemistry}", style = MaterialTheme.typography.bodySmall)
                            Text("${examEnum.thirdSubject()}: ${row.thirdSubject}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Essential Resources Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Essential Preparation Toolkit", style = MaterialTheme.typography.titleMedium)
                        resources.forEach { (emoji, desc) ->
                            Text("$emoji  $desc", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
