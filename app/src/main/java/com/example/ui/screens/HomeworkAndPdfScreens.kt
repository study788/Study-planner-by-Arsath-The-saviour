package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.content.AnswerPdfGenerator
import com.example.data.model.HomeworkSession
import com.example.data.model.QuestionItem
import com.example.ui.theme.EmeraldGreen
import com.example.viewmodel.PdfPreviewState

private val pptSlidePalettes: List<Pair<Color, Color>> = listOf(
    Color(0xFF1E3A8A) to Color(0xFF3B82F6), // Royal Sapphire
    Color(0xFF5B21B6) to Color(0xFF8B5CF6), // Deep Amethyst
    Color(0xFF0F766E) to Color(0xFF14B8A6), // Emerald Teal
    Color(0xFF9A3412) to Color(0xFFF97316), // Burnt Sunset
    Color(0xFF9D174D) to Color(0xFFEC4899), // Berry Magenta
    Color(0xFF1E40AF) to Color(0xFF06B6D4), // Midnight Cyan
    Color(0xFF065F46) to Color(0xFF10B981), // Forest Mint
    Color(0xFF7C2D12) to Color(0xFFF59E0B), // Terracotta Amber
    Color(0xFF3730A3) to Color(0xFF6366F1), // Cosmic Indigo
    Color(0xFF881337) to Color(0xFFF43F5E)  // Ruby Rose
)

@Composable
fun DailyHomeworkScreen(
    examCode: String,
    session: HomeworkSession?,
    questions: List<QuestionItem>,
    remainingMs: Long,
    onEnsureInitialized: () -> Unit,
    onSelectAnswer: (String, Int) -> Unit,
    onGenerateAnswerPdf: () -> Unit
) {
    LaunchedEffect(examCode) {
        onEnsureInitialized()
    }

    var slideIdx by remember(examCode) { mutableIntStateOf(0) }
    val safeIdx = slideIdx.coerceIn(0, questions.lastIndex.coerceAtLeast(0))
    val currentQ = questions.getOrNull(safeIdx)
    val isUnlocked = remainingMs <= 0L

    val totalSec = (remainingMs / 1000L).coerceAtLeast(0L)
    val hrs = totalSec / 3600
    val mins = (totalSec % 3600) / 60
    val secs = totalSec % 60
    val countdownLabel = String.format("%02d:%02d:%02d", hrs, mins, secs)

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
                // PPT Header with Institute Name, App Symbol & Creator
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1321)),
                    border = BorderStroke(1.5.dp, Color(0xFFF5C518))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_symbol),
                            contentDescription = "Arsath Coaching Institute Symbol",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ARSATH COACHING CENTRE",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFFF5C518),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Daily Tough Competitive Homework PPT ($examCode)",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Prepared by Mohammed Arsath H • Every Slide Unique Color",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF67E8F9)
                            )
                        }
                    }
                }

                // 2-Hour Answer PDF Unlock Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) {
                            EmeraldGreen.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (isUnlocked) EmeraldGreen else Color(0xFFF5C518)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Filled.PictureAsPdf else Icons.Filled.Lock,
                                contentDescription = "2-Hour Answer PDF Status",
                                tint = if (isUnlocked) EmeraldGreen else Color(0xFFF5C518)
                            )
                            Text(
                                text = if (isUnlocked) {
                                    "2 Hours Completed — Official Colored Answer PDF Unlocked!"
                                } else {
                                    "Answer PDF Unlocks in $countdownLabel (2-Hr Rule)"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isUnlocked) EmeraldGreen else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Solve all ${questions.size} tough competitive slides first. After 2 hours from assignment, download the Answer PDF with distinct colors per Q&A, dotted question dividers, page margins, and institute crest.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onGenerateAnswerPdf,
                            enabled = isUnlocked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("homework_answer_pdf_button")
                        ) {
                            Icon(Icons.Filled.PictureAsPdf, contentDescription = "Open Answer PDF")
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(if (isUnlocked) "Open & Share Official Answer PDF" else "Locked Until 2-Hour Timer Finishes ($countdownLabel)")
                        }
                    }
                }

                // Slide Number Selector Strip (Color-coded dots)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    questions.forEachIndexed { idx, q ->
                        val pal = pptSlidePalettes[idx % pptSlidePalettes.size]
                        val answered = session?.answers?.containsKey(q.id) == true
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clickable { slideIdx = idx },
                            shape = RoundedCornerShape(10.dp),
                            color = if (safeIdx == idx) pal.first else pal.first.copy(alpha = 0.25f),
                            border = BorderStroke(
                                if (safeIdx == idx) 2.dp else 1.dp,
                                if (answered) Color(0xFFF5C518) else pal.second
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (safeIdx == idx) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Active PPT Slide (Each Question Has Its Own Distinct Vibrant Color)
                if (currentQ != null) {
                    val pal = pptSlidePalettes[safeIdx % pptSlidePalettes.size]
                    val chosen = session?.answers?.get(currentQ.id)?.toInt()
                    val slideBrush = Brush.linearGradient(listOf(pal.first, pal.second))

                    Card(
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("homework_ppt_slide_$safeIdx")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(slideBrush)
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Filled.Slideshow, contentDescription = "PPT Slide", tint = Color.White)
                                    Text(
                                        text = "SLIDE ${safeIdx + 1} / ${questions.size} • ${currentQ.subject.uppercase()}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.35f)
                                ) {
                                    Text(
                                        text = currentQ.difficulty,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFF5C518),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = currentQ.chapter,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )

                            Text(
                                text = currentQ.questionText,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )

                            currentQ.options.forEachIndexed { optIdx, optText ->
                                val isPicked = chosen == optIdx
                                val letter = listOf("A", "B", "C", "D")[optIdx]
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 50.dp)
                                        .clickable { onSelectAnswer(currentQ.id, optIdx) }
                                        .testTag("hw_slide_opt_$optIdx"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isPicked) Color.White else Color.Black.copy(alpha = 0.25f),
                                    border = BorderStroke(1.5.dp, if (isPicked) Color(0xFFF5C518) else Color.White.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "$letter.",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isPicked) pal.first else Color(0xFFF5C518)
                                        )
                                        Text(
                                            text = optText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isPicked) Color.Black else Color.White,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isPicked) {
                                            Icon(Icons.Filled.CheckCircle, contentDescription = "Selected Answer", tint = pal.first)
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Arsath Coaching Centre",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "Attempted: ${session?.answers?.size ?: 0}/${questions.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFF5C518)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { slideIdx = if (safeIdx > 0) safeIdx - 1 else questions.lastIndex },
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) { Text("Prev Slide") }
                        Button(
                            onClick = { slideIdx = (safeIdx + 1) % questions.size },
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) { Text("Next Slide") }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PdfPreviewDialog(
    preview: PdfPreviewState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = preview.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            val shareIntent = AnswerPdfGenerator.createSharePdfIntent(context, preview.file, preview.title)
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Official PDF"))
                        },
                        modifier = Modifier.testTag("share_pdf_button")
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = "Share PDF")
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Share PDF")
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close PDF Preview")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(preview.pageBitmaps) { idx, bmp ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Page ${idx + 1}", style = MaterialTheme.typography.labelSmall)
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "PDF Page ${idx + 1}",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
