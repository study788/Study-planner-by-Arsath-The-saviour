package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.components.AppSymbolCrest
import com.example.ui.theme.GoldAccent

@Composable
fun ProfileAndCreatorScreen(
    profile: UserProfile?,
    userEmail: String,
    isDarkTheme: Boolean,
    examCode: String,
    onToggleTheme: (Boolean) -> Unit,
    onSwitchExam: (String) -> Unit,
    onExportReportPdf: () -> Unit,
    onSignOut: () -> Unit
) {
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
                // Creative App Symbol & Creator Spotlight Card ("Mohammed Arsath H")
                Card(
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, GoldAccent),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF06090F))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFF281C4D),
                                        Color(0xFF0D1321),
                                        Color(0xFF06090F)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_symbol),
                                contentDescription = "Study Planner Official Emblem",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(175.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.Verified, contentDescription = "Verified Creator", tint = GoldAccent)
                                Text(
                                    text = "CREATOR & FOUNDER SPOTLIGHT",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Mohammed Arsath H",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Arsath Coaching Centre • \"Plans are nothing, but planning is everything\"",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = Color(0xFF67E8F9),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Account & Sync Card
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AppSymbolCrest(size = 52.dp)
                            Column {
                                Text(profile?.name ?: "Learner", style = MaterialTheme.typography.titleLarge)
                                Text(userEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "${profile?.studentClass ?: "Class 12"} • Joined ${profile?.joinDate ?: "Today"} • Synced",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.DarkMode, contentDescription = "Theme Mode")
                                Text("Dark Obsidian Theme", style = MaterialTheme.typography.bodyMedium)
                            }
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = onToggleTheme,
                                modifier = Modifier.testTag("theme_toggle_switch")
                            )
                        }

                        Text("Active Target Exam", style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = examCode == "JEE", onClick = { onSwitchExam("JEE") }, label = { Text("JEE Main & Adv") })
                            FilterChip(selected = examCode == "NEET", onClick = { onSwitchExam("NEET") }, label = { Text("NEET UG") })
                        }

                        Button(
                            onClick = onExportReportPdf,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.PictureAsPdf, contentDescription = "Download Report Card")
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Generate Full Progress Report PDF")
                        }

                        OutlinedButton(
                            onClick = onSignOut,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .testTag("sign_out_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sign Out")
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Sign Out (Data Stays Saved in Cloud)")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
