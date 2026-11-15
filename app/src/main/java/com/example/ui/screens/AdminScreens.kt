package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.example.data.model.AdminMessage
import com.example.data.model.CustomVideo
import com.example.data.model.ScheduledClass
import com.example.data.model.UserProfile
import com.example.ui.components.SectionHeaderCard
import com.example.ui.components.StatTileCard
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent

@Composable
fun AdminConsoleScreen(
    students: List<UserProfile>,
    customVideos: List<CustomVideo>,
    scheduledClasses: List<ScheduledClass>,
    selectedStudentMessages: List<AdminMessage>,
    onObserveStudentChat: (String) -> Unit,
    onSendAdminReply: (String, String) -> Unit,
    onAddStudent: (name: String, cls: String, parent: String, contact: String, exam: String, plan: String) -> Unit,
    onToggleStudentActive: (UserProfile) -> Unit,
    onAddVideo: (exam: String, subject: String, chapter: String, title: String, url: String, rating: Double) -> Unit,
    onDeleteVideo: (String) -> Unit,
    onAddQuestion: (exam: String, subject: String, chapter: String, diff: String, qText: String, options: List<String>, correctIdx: Int, expl: String) -> Unit,
    onScheduleClass: (title: String, exam: String, subject: String, time: String, meetUrl: String) -> Unit
) {
    var adminTab by remember { mutableStateOf("students") } // students | videos | questions | meet

    // Student form
    var sName by remember { mutableStateOf("") }
    var sContact by remember { mutableStateOf("") }
    var sExam by remember { mutableStateOf("JEE") }
    var selectedChatStudent by remember { mutableStateOf<UserProfile?>(null) }
    var replyDraft by remember { mutableStateOf("") }

    // Video form
    var vExam by remember { mutableStateOf("JEE") }
    var vSubject by remember { mutableStateOf("Physics") }
    var vChapter by remember { mutableStateOf("Kinematics & Projectiles") }
    var vTitle by remember { mutableStateOf("") }
    var vUrl by remember { mutableStateOf("https://www.youtube.com/") }

    // Question form
    var qExam by remember { mutableStateOf("JEE") }
    var qSubject by remember { mutableStateOf("Physics") }
    var qChapter by remember { mutableStateOf("Rotational Motion") }
    var qText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctIdx by remember { mutableIntStateOf(0) }
    var qExpl by remember { mutableStateOf("") }

    // Meet form
    var cTitle by remember { mutableStateOf("Sunday Mega Doubt Clearing") }
    var cTime by remember { mutableStateOf("Sunday, 6:00 PM IST") }
    var cUrl by remember { mutableStateOf("https://meet.google.com/new") }

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
                    title = "Admin Dashboard · Arsath Coaching Centre",
                    subtitle = "Manage students, mentor chat, chapter video library, competitive question bank & Google Meets."
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatTileCard("👥", "${students.size}", "Total Students", GoldAccent, Modifier.weight(1f))
                    StatTileCard("🎬", "${customVideos.size}", "Custom Videos", ElectricCyan, Modifier.weight(1f))
                    StatTileCard("📡", "${scheduledClasses.size}", "Live Meets", EmeraldGreen, Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "students" to "Students",
                        "videos" to "Videos",
                        "questions" to "Questions",
                        "meet" to "Meet"
                    ).forEach { (k, lbl) ->
                        FilterChip(
                            selected = adminTab == k,
                            onClick = { adminTab = k },
                            label = { Text(lbl) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                when (adminTab) {
                    "students" -> {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Add Student to Coaching Directory", style = MaterialTheme.typography.titleMedium)
                                OutlinedTextField(value = sName, onValueChange = { sName = it }, label = { Text("Student Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = sContact, onValueChange = { sContact = it }, label = { Text("Phone / Contact") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(selected = sExam == "JEE", onClick = { sExam = "JEE" }, label = { Text("JEE") })
                                    FilterChip(selected = sExam == "NEET", onClick = { sExam = "NEET" }, label = { Text("NEET") })
                                }
                                Button(
                                    onClick = {
                                        if (sName.isNotBlank()) {
                                            onAddStudent(sName, "Class 12", "", sContact, sExam, "1year")
                                            sName = ""; sContact = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add Student")
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Text("Enroll Student")
                                }
                            }
                        }

                        students.forEach { stu ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, if (stu.active) MaterialTheme.colorScheme.outline else CrimsonRed)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${stu.name} (${stu.exam})", style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            text = if (stu.active) "ACTIVE" else "PAUSED",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (stu.active) EmeraldGreen else CrimsonRed
                                        )
                                    }
                                    Text("${stu.studentClass} • Sessions: ${stu.sessionsCount} • Contact: ${stu.contact}", style = MaterialTheme.typography.bodySmall)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(onClick = { onToggleStudentActive(stu) }) {
                                            Text(if (stu.active) "Deactivate" else "Activate")
                                        }
                                        Button(onClick = {
                                            selectedChatStudent = stu
                                            onObserveStudentChat(stu.userId)
                                        }) {
                                            Text("Chat Student")
                                        }
                                    }
                                }
                            }
                        }

                        selectedChatStudent?.let { stu ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Chat with ${stu.name}", style = MaterialTheme.typography.titleMedium)
                                    selectedStudentMessages.takeLast(6).forEach { m ->
                                        Text("${m.senderName}: ${m.message}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    OutlinedTextField(
                                        value = replyDraft,
                                        onValueChange = { replyDraft = it },
                                        label = { Text("Reply as Arsath Sir...") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Button(onClick = {
                                        if (replyDraft.isNotBlank()) {
                                            onSendAdminReply(stu.userId, replyDraft)
                                            replyDraft = ""
                                        }
                                    }) { Text("Send Admin Reply") }
                                }
                            }
                        }
                    }

                    "videos" -> {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Add Curated Video", style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(selected = vExam == "JEE", onClick = { vExam = "JEE" }, label = { Text("JEE") })
                                    FilterChip(selected = vExam == "NEET", onClick = { vExam = "NEET" }, label = { Text("NEET") })
                                }
                                OutlinedTextField(value = vSubject, onValueChange = { vSubject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = vChapter, onValueChange = { vChapter = it }, label = { Text("Chapter") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = vTitle, onValueChange = { vTitle = it }, label = { Text("Video Title") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = vUrl, onValueChange = { vUrl = it }, label = { Text("YouTube URL") }, modifier = Modifier.fillMaxWidth())
                                Button(
                                    onClick = {
                                        if (vTitle.isNotBlank() && vUrl.isNotBlank()) {
                                            onAddVideo(vExam, vSubject, vChapter, vTitle, vUrl, 4.9)
                                            vTitle = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                ) { Text("Publish Video") }
                            }
                        }

                        customVideos.forEach { vid ->
                            Card(shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("${vid.exam} · ${vid.subject}: ${vid.title}", style = MaterialTheme.typography.bodyMedium)
                                        Text(vid.url, style = MaterialTheme.typography.labelSmall)
                                    }
                                    IconButton(onClick = { onDeleteVideo(vid.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete Custom Video")
                                    }
                                }
                            }
                        }
                    }

                    "questions" -> {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Add Tough Competitive Question", style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(selected = qExam == "JEE", onClick = { qExam = "JEE" }, label = { Text("JEE") })
                                    FilterChip(selected = qExam == "NEET", onClick = { qExam = "NEET" }, label = { Text("NEET") })
                                }
                                OutlinedTextField(value = qSubject, onValueChange = { qSubject = it }, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = qChapter, onValueChange = { qChapter = it }, label = { Text("Chapter") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = qText, onValueChange = { qText = it }, label = { Text("Question Statement") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("Option A") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
                                Text("Correct Option", style = MaterialTheme.typography.labelLarge)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(0 to "A", 1 to "B", 2 to "C", 3 to "D").forEach { (i, lbl) ->
                                        FilterChip(selected = correctIdx == i, onClick = { correctIdx = i }, label = { Text(lbl) })
                                    }
                                }
                                OutlinedTextField(value = qExpl, onValueChange = { qExpl = it }, label = { Text("Step-by-Step Solution") }, modifier = Modifier.fillMaxWidth())
                                Button(
                                    onClick = {
                                        if (qText.isNotBlank() && optA.isNotBlank() && optB.isNotBlank() && optC.isNotBlank() && optD.isNotBlank()) {
                                            onAddQuestion(qExam, qSubject, qChapter, "Tough", qText, listOf(optA, optB, optC, optD), correctIdx, qExpl)
                                            qText = ""; optA = ""; optB = ""; optC = ""; optD = ""; qExpl = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                ) { Text("Add to Exam Question Vault") }
                            }
                        }
                    }

                    else -> {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Schedule Google Meet Class", style = MaterialTheme.typography.titleMedium)
                                OutlinedTextField(value = cTitle, onValueChange = { cTitle = it }, label = { Text("Class Title") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = cTime, onValueChange = { cTime = it }, label = { Text("Schedule Date & Time") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = cUrl, onValueChange = { cUrl = it }, label = { Text("Google Meet URL") }, modifier = Modifier.fillMaxWidth())
                                Button(
                                    onClick = {
                                        if (cTitle.isNotBlank()) {
                                            onScheduleClass(cTitle, "ALL", "All Subjects", cTime, cUrl)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("admin_schedule_class_btn")
                                ) { Text("Broadcast Live Class") }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
