package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.content.ChapterVideosRepository
import com.example.data.content.FlashcardsData
import com.example.data.content.MindmapData
import com.example.data.content.QuestionBankRepository
import com.example.data.content.SyllabusChapters
import com.example.data.model.ExamType
import com.example.ui.components.SectionHeaderCard
import com.example.ui.theme.CrestPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RoyalBlue

private data class SubjectVisualMeta(
    val icon: ImageVector,
    val accent: Color,
    val tagline: String
)

private fun subjectMetaFor(subject: String): SubjectVisualMeta = when (subject) {
    "Physics" -> SubjectVisualMeta(
        icon = Icons.Filled.Bolt,
        accent = ElectricCyan,
        tagline = "Mechanics, Electrodynamics, Optics & Modern Physics"
    )
    "Chemistry" -> SubjectVisualMeta(
        icon = Icons.Filled.Science,
        accent = CrestPurple,
        tagline = "Physical Numericals, Inorganic Trends & Organic Mechanisms"
    )
    "Mathematics" -> SubjectVisualMeta(
        icon = Icons.Filled.Calculate,
        accent = GoldAccent,
        tagline = "Algebra, Coordinate Geometry, Calculus & Vectors"
    )
    else -> SubjectVisualMeta(
        icon = Icons.Filled.Biotech,
        accent = EmeraldGreen,
        tagline = "Botany, Zoology, Genetics, Physiology & Ecology (NCERT)"
    )
}

@Composable
fun SubjectSelectionScreen(
    examCode: String,
    completedChapters: List<String>,
    onSelectStream: (String) -> Unit,
    onToggleChapter: (String) -> Unit,
    onPracticeSubject: (stream: String, subject: String) -> Unit,
    onPracticeChapter: (stream: String, subject: String, chapter: String) -> Unit,
    onOpenSubjectTool: (subject: String, toolMode: String) -> Unit
) {
    var drilledSubject by rememberSaveable { mutableStateOf<String?>(null) }

    val examEnum = if (examCode == "NEET") ExamType.NEET else ExamType.JEE
    val validSubjects = examEnum.subjects()

    // Ensure drilledSubject remains valid when stream changes
    val activeDrillSubject = drilledSubject?.takeIf { it in validSubjects }

    if (activeDrillSubject != null) {
        BackHandler { drilledSubject = null }
    }

    AnimatedContent(
        targetState = activeDrillSubject,
        label = "subject_drilldown_transition"
    ) { targetSubject ->
        if (targetSubject == null) {
            StreamAndSubjectOverview(
                examCode = examCode,
                examEnum = examEnum,
                completedChapters = completedChapters,
                onSelectStream = { nextStream ->
                    onSelectStream(nextStream)
                    drilledSubject = null
                },
                onDrillDownSubject = { subj ->
                    drilledSubject = subj
                }
            )
        } else {
            SubjectDrillDownView(
                examCode = examCode,
                subject = targetSubject,
                completedChapters = completedChapters,
                onBackToSubjects = { drilledSubject = null },
                onToggleChapter = onToggleChapter,
                onPracticeSubject = { onPracticeSubject(examCode, targetSubject) },
                onPracticeChapter = { chap -> onPracticeChapter(examCode, targetSubject, chap) },
                onOpenSubjectTool = { tool -> onOpenSubjectTool(targetSubject, tool) }
            )
        }
    }
}

@Composable
private fun StreamAndSubjectOverview(
    examCode: String,
    examEnum: ExamType,
    completedChapters: List<String>,
    onSelectStream: (String) -> Unit,
    onDrillDownSubject: (String) -> Unit
) {
    val qCounts = remember(examCode) { QuestionBankRepository.getCountsSummary(examCode) }
    val subjects = examEnum.subjects()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.widthIn(max = 880.dp).fillMaxWidth()) {
                SectionHeaderCard(
                    title = "Stream & Subject Selection",
                    subtitle = "Step 1: Pick JEE or NEET stream • Step 2: Drill down into Physics, Chemistry, or ${examEnum.thirdSubject()}"
                )
            }
        }

        item {
            Column(
                modifier = Modifier.widthIn(max = 880.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "1. Select Entrance Stream",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val sideBySide = maxWidth >= 520.dp
                    if (sideBySide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StreamCard(
                                title = "JEE Stream",
                                subtitle = "Main & Advanced Engineering",
                                subjectsSummary = "Physics • Chemistry • Mathematics",
                                totalQuestions = QuestionBankRepository.getCountsSummary("JEE").values.sum(),
                                totalChapters = SyllabusChapters.jeePhysics.size +
                                    SyllabusChapters.jeeChemistry.size +
                                    SyllabusChapters.jeeMathematics.size,
                                selected = examCode == "JEE",
                                accent = RoyalBlue,
                                tag = "stream_card_jee",
                                onClick = { onSelectStream("JEE") },
                                modifier = Modifier.weight(1f)
                            )
                            StreamCard(
                                title = "NEET Stream",
                                subtitle = "UG Medical Entrance",
                                subjectsSummary = "Physics • Chemistry • Biology",
                                totalQuestions = QuestionBankRepository.getCountsSummary("NEET").values.sum(),
                                totalChapters = SyllabusChapters.neetPhysics.size +
                                    SyllabusChapters.neetChemistry.size +
                                    SyllabusChapters.neetBiology.size,
                                selected = examCode == "NEET",
                                accent = EmeraldGreen,
                                tag = "stream_card_neet",
                                onClick = { onSelectStream("NEET") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            StreamCard(
                                title = "JEE Stream",
                                subtitle = "Main & Advanced Engineering",
                                subjectsSummary = "Physics • Chemistry • Mathematics",
                                totalQuestions = QuestionBankRepository.getCountsSummary("JEE").values.sum(),
                                totalChapters = SyllabusChapters.jeePhysics.size +
                                    SyllabusChapters.jeeChemistry.size +
                                    SyllabusChapters.jeeMathematics.size,
                                selected = examCode == "JEE",
                                accent = RoyalBlue,
                                tag = "stream_card_jee",
                                onClick = { onSelectStream("JEE") }
                            )
                            StreamCard(
                                title = "NEET Stream",
                                subtitle = "UG Medical Entrance",
                                subjectsSummary = "Physics • Chemistry • Biology",
                                totalQuestions = QuestionBankRepository.getCountsSummary("NEET").values.sum(),
                                totalChapters = SyllabusChapters.neetPhysics.size +
                                    SyllabusChapters.neetChemistry.size +
                                    SyllabusChapters.neetBiology.size,
                                selected = examCode == "NEET",
                                accent = EmeraldGreen,
                                tag = "stream_card_neet",
                                onClick = { onSelectStream("NEET") }
                            )
                        }
                    }
                }
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 880.dp).fillMaxWidth()) {
                Text(
                    text = "2. Drill Down Into $examCode Subjects",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        itemsIndexed(subjects) { _, subject ->
            val meta = subjectMetaFor(subject)
            val chapters = SyllabusChapters.chaptersFor(examCode, subject)
            val doneCount = chapters.count { ch -> "${examCode}_${subject}_ch_$ch" in completedChapters }
            val qCount = qCounts[subject] ?: 0
            val videoCount = ChapterVideosRepository.getVideosForExamAndSubject(examCode, subject).size

            Box(modifier = Modifier.widthIn(max = 880.dp).fillMaxWidth()) {
                SubjectOverviewCard(
                    examCode = examCode,
                    subject = subject,
                    meta = meta,
                    chapters = chapters,
                    doneCount = doneCount,
                    questionCount = qCount,
                    videoCount = videoCount,
                    onClick = { onDrillDownSubject(subject) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun StreamCard(
    title: String,
    subtitle: String,
    subjectsSummary: String,
    totalQuestions: Int,
    totalChapters: Int,
    selected: Boolean,
    accent: Color,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 128.dp)
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) accent.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) accent else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = title,
                        tint = accent
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = if (selected) accent else MaterialTheme.colorScheme.onSurface
                    )
                }
                if (selected) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accent
                    ) {
                        Text(
                            text = "SELECTED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subjectsSummary,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$totalChapters Chapters • $totalQuestions+ Competitive Questions",
                style = MaterialTheme.typography.labelSmall,
                color = accent
            )
        }
    }
}

@Composable
private fun SubjectOverviewCard(
    examCode: String,
    subject: String,
    meta: SubjectVisualMeta,
    chapters: List<String>,
    doneCount: Int,
    questionCount: Int,
    videoCount: Int,
    onClick: () -> Unit
) {
    val progress = if (chapters.isEmpty()) 0f else doneCount.toFloat() / chapters.size.toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("subject_card_${subject.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, meta.accent.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(meta.accent.copy(alpha = 0.25f), meta.accent.copy(alpha = 0.08f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = meta.icon,
                        contentDescription = "$examCode $subject Icon",
                        tint = meta.accent,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$examCode · $subject",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = meta.tagline,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Drill Down into $subject",
                    tint = meta.accent
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${chapters.size} Chapters • $questionCount Qs • $videoCount Videos",
                    style = MaterialTheme.typography.labelMedium,
                    color = meta.accent
                )
                Text(
                    text = "$doneCount/${chapters.size} Done",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                color = meta.accent,
                trackColor = meta.accent.copy(alpha = 0.16f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            // Preview top 3 chapters
            Text(
                text = "Includes: ${chapters.take(3).joinToString(" • ")}...",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SubjectDrillDownView(
    examCode: String,
    subject: String,
    completedChapters: List<String>,
    onBackToSubjects: () -> Unit,
    onToggleChapter: (String) -> Unit,
    onPracticeSubject: () -> Unit,
    onPracticeChapter: (String) -> Unit,
    onOpenSubjectTool: (String) -> Unit
) {
    val meta = subjectMetaFor(subject)
    val chapters = remember(examCode, subject) { SyllabusChapters.chaptersFor(examCode, subject) }
    val qCount = remember(examCode, subject) {
        QuestionBankRepository.getQuestionsForExamAndSubject(examCode, subject).size
    }
    val videosCount = remember(examCode, subject) {
        ChapterVideosRepository.getVideosForExamAndSubject(examCode, subject).size
    }
    val mindmapsCount = remember(examCode, subject) {
        MindmapData.getMindmapsFor(examCode, subject).size
    }
    val flashcardsCount = remember(examCode, subject) {
        FlashcardsData.getFlashcards(examCode, subject).size
    }
    val doneCount = chapters.count { ch -> "${examCode}_${subject}_ch_$ch" in completedChapters }

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
                // Back breadcrumb bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onBackToSubjects,
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("back_to_subjects_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Streams & Subjects"
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("All $examCode Subjects")
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = meta.accent.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = "$examCode STREAM",
                            style = MaterialTheme.typography.labelMedium,
                            color = meta.accent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Hero Subject Detail Banner
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, meta.accent)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(meta.accent.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = meta.icon,
                                    contentDescription = subject,
                                    tint = meta.accent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$subject ($examCode)",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = meta.tagline,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "$doneCount / ${chapters.size} Chapters Completed • $qCount Questions • $videosCount Videos • $mindmapsCount Mindmaps • $flashcardsCount Flashcards",
                            style = MaterialTheme.typography.labelMedium,
                            color = meta.accent
                        )

                        LinearProgressIndicator(
                            progress = { if (chapters.isEmpty()) 0f else doneCount.toFloat() / chapters.size.toFloat() },
                            color = meta.accent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        // Quick Drill-Down Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onPracticeSubject,
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("subject_practice_btn")
                            ) {
                                Icon(Icons.Filled.Quiz, contentDescription = "Practice Subject Test")
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Test (15 Qs)")
                            }
                            OutlinedButton(
                                onClick = { onOpenSubjectTool("videos") },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("subject_videos_btn")
                            ) {
                                Icon(Icons.Filled.PlayCircle, contentDescription = "Watch Subject Videos")
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Videos")
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onOpenSubjectTool("mindmaps") },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("subject_mindmaps_btn")
                            ) {
                                Icon(Icons.Filled.AccountTree, contentDescription = "Open Mindmaps")
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Mindmaps")
                            }
                            OutlinedButton(
                                onClick = { onOpenSubjectTool("flashcards") },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .testTag("subject_flashcards_btn")
                            ) {
                                Icon(Icons.Filled.Style, contentDescription = "Open Flashcards")
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Flashcards")
                            }
                        }
                    }
                }

                Text(
                    text = "Chapter-by-Chapter Drill Down (${chapters.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        itemsIndexed(chapters) { idx, chapter ->
            val chapterKey = "${examCode}_${subject}_ch_$chapter"
            val isCompleted = chapterKey in completedChapters

            Card(
                modifier = Modifier
                    .widthIn(max = 880.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) {
                        meta.accent.copy(alpha = 0.08f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    if (isCompleted) meta.accent else MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onToggleChapter(chapterKey) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("chapter_toggle_$idx")
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = if (isCompleted) "Chapter Completed" else "Mark Chapter Completed",
                            tint = if (isCompleted) meta.accent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Chapter ${idx + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = meta.accent
                        )
                        Text(
                            text = chapter,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    OutlinedButton(
                        onClick = { onPracticeChapter(chapter) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("chapter_practice_btn_$idx")
                    ) {
                        Text("Practice 10Q")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
