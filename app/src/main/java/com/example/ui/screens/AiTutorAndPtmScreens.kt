package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AdminMessage
import com.example.data.model.ScheduledClass
import com.example.data.repository.AiChatTurn
import com.example.data.repository.AiTutorUiState
import com.example.ui.components.SectionHeaderCard
import com.example.ui.theme.EmeraldGreen

@Composable
fun AiTutorScreen(
    examCode: String,
    aiState: AiTutorUiState,
    chatHistory: List<AiChatTurn>,
    onAskTutor: (String) -> Unit
) {
    var promptDraft by remember { mutableStateOf("") }
    val starterPrompts = listOf(
        "Explain Rotational Inertia theorems for $examCode with examples",
        "Summarize GOC inductive & resonance priority rules",
        if (examCode == "NEET") "Compare C3 vs C4 photosynthesis pathways for NEET" else "Teach Integration by King's Property with a trick",
        "Make a 7-day revision plan for weak chapters"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 880.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            SectionHeaderCard(
                title = "Arsath AI Mentor (Gemini)",
                subtitle = "24x7 interactive $examCode doubt solver, derivations, NCERT checks & revision coach."
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (chatHistory.isEmpty() && aiState is AiTutorUiState.Idle) {
                    item {
                        Text("Try asking:", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(6.dp))
                        starterPrompts.forEach { sample ->
                            AssistChip(
                                onClick = { onAskTutor(sample) },
                                leadingIcon = { Icon(Icons.Filled.AutoAwesome, contentDescription = "Starter Prompt") },
                                label = { Text(sample) },
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }

                items(chatHistory) { turn ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (turn.isUser) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (turn.isUser) "You • ${turn.timestamp}" else "Arsath AI Mentor • ${turn.timestamp}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = turn.text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                when (aiState) {
                    is AiTutorUiState.Loading -> item {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Text("Thinking step-by-step...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    is AiTutorUiState.Streaming -> item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Arsath AI Mentor (typing...)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(aiState.text, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    is AiTutorUiState.Error -> item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = aiState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    else -> Unit
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = promptDraft,
                    onValueChange = { promptDraft = it },
                    label = { Text("Ask any $examCode question...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_prompt_input")
                )
                IconButton(
                    onClick = {
                        if (promptDraft.isNotBlank()) {
                            onAskTutor(promptDraft)
                            promptDraft = ""
                        }
                    },
                    enabled = aiState !is AiTutorUiState.Loading && aiState !is AiTutorUiState.Streaming,
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("ai_send_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Question to AI Mentor")
                }
            }
        }
    }
}

@Composable
fun PtmAndLiveClassScreen(
    examCode: String,
    scheduledClasses: List<ScheduledClass>,
    messages: List<AdminMessage>,
    onSendMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var chatDraft by remember { mutableStateOf("") }

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
                // Teacher Hero Card with teacher_arsath photo
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.teacher_arsath),
                            contentDescription = "Mentor Mohammed Arsath H",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ARSATH COACHING CENTRE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text("Mohammed Arsath H", style = MaterialTheme.typography.titleLarge)
                            Text("Lead Mentor • JEE & NEET Strategy, PTM & Weekly Review", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Weekly PTM Launch
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Weekly Parent-Teacher Meeting (PTM)", style = MaterialTheme.typography.titleMedium)
                        Text("Join every Sunday 6:00 PM IST for live score review & study plan adjustments.", style = MaterialTheme.typography.bodySmall)
                        Button(
                            onClick = {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://meet.google.com/new")))
                                }
                            },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.VideoCall, contentDescription = "Launch Google Meet")
                            Spacer(modifier = Modifier.size(6.dp))
                            Text("Join Live PTM Google Meet")
                        }
                    }
                }

                // Live Scheduled Classes
                Text("Scheduled Live Doubt Classes (${scheduledClasses.size})", style = MaterialTheme.typography.titleMedium)
                if (scheduledClasses.isEmpty()) {
                    Text("No live classes posted right now. Check back soon or message Sir below.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    scheduledClasses.forEach { sc ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("${sc.title} (${sc.exam} · ${sc.subject})", style = MaterialTheme.typography.titleMedium)
                                Text("Time: ${sc.scheduleTime}", style = MaterialTheme.typography.bodySmall)
                                Button(
                                    onClick = {
                                        runCatching {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(sc.meetUrl)))
                                        }
                                    }
                                ) { Text("Enter Meet Room") }
                            }
                        }
                    }
                }

                // Direct Chat History with Arsath
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Direct Mentor Messages", style = MaterialTheme.typography.titleMedium)
                        if (messages.isEmpty()) {
                            Text("Start a direct conversation with Mohammed Arsath H.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            messages.forEach { msg ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (msg.senderRole == "admin") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("${msg.senderName} • ${msg.timeLabel}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        Text(msg.message, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
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
                                label = { Text("Write to mentor ($examCode)...") },
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
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Message")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
