package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.content.ChapterVideosRepository
import com.example.data.content.FlashcardsData
import com.example.data.content.MindmapData
import com.example.data.model.CustomVideo
import com.example.data.model.ExamType
import com.example.ui.components.ExternalLinkButton
import com.example.ui.components.SectionHeaderCard
import com.example.ui.components.SubjectChipStrip
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAccent

@Composable
fun VideosAndStudyToolsScreen(
    examCode: String,
    customVideos: List<CustomVideo>,
    masteredFlashcardIds: List<String>,
    onToggleFlashcardMastered: (String) -> Unit,
    initialToolMode: String = "videos",
    initialSubject: String? = null
) {
    val examEnum = if (examCode == "NEET") ExamType.NEET else ExamType.JEE
    val subjects = examEnum.subjects()
    var subMode by remember { mutableStateOf(initialToolMode) } // videos | mindmaps | flashcards
    var selectedSubject by remember(examCode) {
        mutableStateOf(initialSubject?.takeIf { it in subjects } ?: subjects.first())
    }
    androidx.compose.runtime.LaunchedEffect(initialToolMode, initialSubject, examCode) {
        subMode = initialToolMode
        if (initialSubject != null && initialSubject in subjects) {
            selectedSubject = initialSubject
        }
    }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 880.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = subMode == "videos",
                    onClick = { subMode = "videos" },
                    label = { Text("Top Videos") },
                    leadingIcon = { Icon(Icons.Filled.PlayCircle, contentDescription = "Videos") },
                    modifier = Modifier.testTag("tool_tab_videos")
                )
                FilterChip(
                    selected = subMode == "mindmaps",
                    onClick = { subMode = "mindmaps" },
                    label = { Text("Mindmaps") },
                    leadingIcon = { Icon(Icons.Filled.AccountTree, contentDescription = "Mindmaps") },
                    modifier = Modifier.testTag("tool_tab_mindmaps")
                )
                FilterChip(
                    selected = subMode == "flashcards",
                    onClick = { subMode = "flashcards" },
                    label = { Text("Flashcards") },
                    leadingIcon = { Icon(Icons.Filled.Style, contentDescription = "Flashcards") },
                    modifier = Modifier.testTag("tool_tab_flashcards")
                )
            }

            SubjectChipStrip(
                subjects = subjects,
                selectedSubject = selectedSubject,
                onSelect = { selectedSubject = it }
            )

            when (subMode) {
                "videos" -> ChapterVideosTab(
                    examCode = examCode,
                    selectedSubject = selectedSubject,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    customVideos = customVideos
                )
                "mindmaps" -> MindmapsTab(
                    examCode = examCode,
                    selectedSubject = selectedSubject
                )
                else -> FlashcardsTab(
                    examCode = examCode,
                    selectedSubject = selectedSubject,
                    masteredIds = masteredFlashcardIds,
                    onToggleMastered = onToggleFlashcardMastered
                )
            }
        }
    }
}

@Composable
private fun ChapterVideosTab(
    examCode: String,
    selectedSubject: String,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    customVideos: List<CustomVideo>
) {
    val foundationPlaylists = remember(examCode) {
        ChapterVideosRepository.getPlaylistsForExam(examCode)
    }
    val chapterVideos = remember(examCode, selectedSubject, customVideos, searchQuery) {
        val base = ChapterVideosRepository.getVideosForExamAndSubject(examCode, selectedSubject)
        val extra = customVideos.filter { it.exam == examCode && it.subject == selectedSubject }.map { cv ->
            com.example.data.model.ChapterVideo(
                id = cv.id,
                exam = cv.exam,
                subject = cv.subject,
                chapter = cv.chapter,
                title = cv.title,
                educator = "Arsath Coaching Curated",
                rating = cv.rating,
                duration = "Featured",
                url = cv.url,
                weightageNote = "Teacher Pick"
            )
        }
        (extra + base).filter {
            searchQuery.isBlank() ||
                it.chapter.contains(searchQuery, ignoreCase = true) ||
                it.title.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SectionHeaderCard(
                title = "Top-Rated Chapter Lectures ($examCode)",
                subtitle = "Core foundation YouTube playlists + top rated lectures for every single $selectedSubject chapter."
            )
        }
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Filter by chapter or educator...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Text(
                "Foundation Playlists (${foundationPlaylists.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(foundationPlaylists, key = { it.id }) { pl ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${pl.subject}: ${pl.title}", style = MaterialTheme.typography.titleMedium)
                    Text("${pl.weightageNote} • ★ ${pl.rating}", style = MaterialTheme.typography.bodySmall)
                    ExternalLinkButton(label = "Open YouTube Playlist", url = pl.url)
                }
            }
        }
        item {
            Text(
                "$selectedSubject Chapter-by-Chapter Rated Videos (${chapterVideos.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(chapterVideos, key = { it.id }) { vid ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = vid.chapter,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Filled.Star, contentDescription = "Rating", tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Text("${vid.rating}", style = MaterialTheme.typography.labelMedium, color = GoldAccent)
                        }
                    }
                    Text(vid.title, style = MaterialTheme.typography.titleMedium)
                    Text("${vid.educator} • ${vid.duration} • ${vid.weightageNote}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ExternalLinkButton(label = "Watch Top-Rated Video", url = vid.url)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun MindmapsTab(
    examCode: String,
    selectedSubject: String
) {
    val mindmaps = remember(examCode, selectedSubject) {
        MindmapData.getMindmapsFor(examCode, selectedSubject)
    }
    var expandedId by remember(examCode, selectedSubject) { mutableStateOf(mindmaps.firstOrNull()?.id) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SectionHeaderCard(
                title = "$selectedSubject Concept Mindmaps",
                subtitle = "Expand any chapter node to visualize core laws, formulas, traps, and $examCode PYQ links."
            )
        }
        items(mindmaps, key = { it.id }) { mm ->
            val isExpanded = expandedId == mm.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedId = if (isExpanded) null else mm.id },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(mm.chapter, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = "Expand Mindmap"
                        )
                    }
                    Text(mm.centralSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    AnimatedVisibility(visible = isExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            mm.branches.forEach { branch ->
                                val branchColor = Color(branch.colorHex)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = branchColor.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, branchColor.copy(alpha = 0.45f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(branch.title, style = MaterialTheme.typography.labelLarge, color = branchColor)
                                        branch.points.forEach { pt ->
                                            Text("• $pt", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun FlashcardsTab(
    examCode: String,
    selectedSubject: String,
    masteredIds: List<String>,
    onToggleMastered: (String) -> Unit
) {
    val cards = remember(examCode, selectedSubject) {
        FlashcardsData.getFlashcards(examCode, selectedSubject)
    }
    var cardIdx by remember(examCode, selectedSubject) { mutableIntStateOf(0) }
    var flipped by remember(examCode, selectedSubject, cardIdx) { mutableStateOf(false) }

    val safeIdx = cardIdx.coerceIn(0, cards.lastIndex.coerceAtLeast(0))
    val current = cards.getOrNull(safeIdx)
    val masteredInSubject = cards.count { it.id in masteredIds }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeaderCard(
                title = "$selectedSubject Active Recall Flashcards",
                subtitle = "$masteredInSubject / ${cards.size} cards mastered • Tap card to flip front/back"
            )
        }
        if (current != null) {
            item {
                val isMastered = current.id in masteredIds
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 240.dp)
                        .clickable { flipped = !flipped }
                        .testTag("flashcard_deck_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (flipped) {
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (isMastered) EmeraldGreen else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "CARD ${safeIdx + 1} OF ${cards.size} • ${current.chapter}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (flipped) "ANSWER SIDE" else "PROMPT SIDE",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (flipped) EmeraldGreen else GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (!flipped) {
                            Text(current.frontTitle, style = MaterialTheme.typography.headlineSmall)
                            Text(current.frontPrompt, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Tap card anywhere to reveal answer & memory hook",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text("Answer / Formula:", style = MaterialTheme.typography.labelLarge, color = EmeraldGreen)
                            Text(current.backAnswer, style = MaterialTheme.typography.titleLarge)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                            ) {
                                Text(
                                    text = "Memory Hook: ${current.memoryHook}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
            item {
                val isMastered = current.id in masteredIds
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { cardIdx = if (safeIdx > 0) safeIdx - 1 else cards.lastIndex },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    ) { Text("Prev") }
                    Button(
                        onClick = { onToggleMastered(current.id) },
                        modifier = Modifier.weight(1.4f).heightIn(min = 48.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Mark Mastered")
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(if (isMastered) "Mastered" else "Mark Got It")
                    }
                    OutlinedButton(
                        onClick = { cardIdx = (safeIdx + 1) % cards.size },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    ) { Text("Next") }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
