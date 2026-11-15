package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.model.ExamType
import com.example.data.model.StudyGoal
import com.example.ui.components.SectionHeaderCard
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.viewmodel.TimerMode

@Composable
fun FocusTimerAndGoalsScreen(
    examCode: String,
    timerMode: TimerMode,
    timerSeconds: Int,
    timerRunning: Boolean,
    isBreakPhase: Boolean,
    sessionsCount: Long,
    initialNotes: String,
    goals: List<StudyGoal>,
    onSelectMode: (TimerMode) -> Unit,
    onToggleRun: () -> Unit,
    onResetTimer: () -> Unit,
    onSaveNotes: (String) -> Unit,
    onAddGoal: (title: String, subject: String, priority: String) -> Unit,
    onToggleGoal: (StudyGoal) -> Unit,
    onDeleteGoal: (String) -> Unit
) {
    val examEnum = if (examCode == "NEET") ExamType.NEET else ExamType.JEE
    var notesDraft by remember(initialNotes) { mutableStateOf(initialNotes) }
    var goalTitle by remember { mutableStateOf("") }
    var goalSubject by remember(examCode) { mutableStateOf("Physics") }
    var goalPriority by remember { mutableStateOf("high") }

    val totalSec = if (timerMode == TimerMode.STOPWATCH) 3600 else {
        if (isBreakPhase) timerMode.breakSeconds else timerMode.focusSeconds
    }.coerceAtLeast(1)
    val sweepFraction = if (timerMode == TimerMode.STOPWATCH) {
        (timerSeconds % 60) / 60f
    } else {
        timerSeconds.toFloat() / totalSec.toFloat()
    }.coerceIn(0f, 1f)

    val mins = timerSeconds / 60
    val secs = timerSeconds % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)
    val ringColor = if (isBreakPhase) EmeraldGreen else MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

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
                SectionHeaderCard(
                    title = "Focus Timer & Weekly Goals",
                    subtitle = "$sessionsCount deep work blocks logged • Auto-saved to cloud"
                )

                // Timer Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TimerMode.entries.forEach { mode ->
                                FilterChip(
                                    selected = timerMode == mode,
                                    onClick = { onSelectMode(mode) },
                                    label = { Text(mode.label.substringBefore(" ")) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.size(210.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                                drawArc(
                                    color = trackColor,
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = ringColor,
                                    startAngle = -90f,
                                    sweepAngle = 360f * sweepFraction,
                                    useCenter = false,
                                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isBreakPhase) "RECHARGE BREAK" else timerMode.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ringColor
                                )
                                Text(
                                    text = timeFormatted,
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = onToggleRun,
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("timer_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (timerRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (timerRunning) "Pause Timer" else "Start Timer"
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(if (timerRunning) "Pause" else "Start Focus")
                            }
                            OutlinedButton(
                                onClick = onResetTimer,
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset Timer")
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Reset")
                            }
                        }

                        OutlinedTextField(
                            value = notesDraft,
                            onValueChange = { notesDraft = it },
                            label = { Text("Session Notes / Formulas Practiced") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = { onSaveNotes(notesDraft) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = "Save Notes")
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("Save Notes to Cloud")
                        }
                    }
                }

                // Weekly Goals Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val doneCount = goals.count { it.done }
                        val ratio = if (goals.isEmpty()) 0f else doneCount.toFloat() / goals.size
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Weekly Study Goals", style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = "$doneCount / ${goals.size} Done",
                                style = MaterialTheme.typography.labelLarge,
                                color = EmeraldGreen
                            )
                        }
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )

                        OutlinedTextField(
                            value = goalTitle,
                            onValueChange = { goalTitle = it },
                            label = { Text("Add Weekly Goal (e.g., Solve 100 Rotation Qs)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_title_input")
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            examEnum.subjects().forEach { sub ->
                                FilterChip(
                                    selected = goalSubject == sub,
                                    onClick = { goalSubject = sub },
                                    label = { Text(sub) }
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("high" to "High", "med" to "Medium", "low" to "Low").forEach { (k, label) ->
                                FilterChip(
                                    selected = goalPriority == k,
                                    onClick = { goalPriority = k },
                                    label = { Text(label) }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (goalTitle.isNotBlank()) {
                                    onAddGoal(goalTitle, goalSubject, goalPriority)
                                    goalTitle = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("add_goal_button")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Goal")
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("Add Weekly Target")
                        }

                        if (goals.isEmpty()) {
                            Text(
                                "No goals yet. Add your weekly target above!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            goals.forEach { goal ->
                                val prioColor = when (goal.priority) {
                                    "high" -> CrimsonRed
                                    "med" -> GoldAccent
                                    else -> ElectricCyan
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = goal.done,
                                        onCheckedChange = { onToggleGoal(goal) }
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = goal.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            textDecoration = if (goal.done) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                        Text(
                                            text = "${goal.subject} • ${goal.priority.uppercase()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = prioColor
                                        )
                                    }
                                    IconButton(onClick = { onDeleteGoal(goal.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete Goal")
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
