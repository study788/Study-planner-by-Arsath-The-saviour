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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.CreatorSignatureStrip
import com.example.viewmodel.StudyPlannerViewModel
import com.google.firebase.auth.FirebaseUser

@Composable
fun SignInScreen(
    isLoading: Boolean,
    bannerMessage: String?,
    onReturningLogin: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onDismissBanner: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .safeDrawingPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Showcase featuring the uploaded App Symbol
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_symbol),
                        contentDescription = "Study Planner by Arsath Emblem",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "ARSATH COACHING CENTRE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Study Planner · JEE & NEET",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "6,300+ JEE & NEET Questions • Chapter Videos • Mindmaps • Flashcards • Tough Daily Homework PPT & 2-Hr Answer PDF • Arsath AI Tutor",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (bannerMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = bannerMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = onDismissBanner) {
                            Text("OK")
                        }
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(12.dp))
            } else {
                Button(
                    onClick = onReturningLogin,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("returning_login_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Login,
                        contentDescription = "Returning Sign In"
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Returning Learner? One-Tap Cloud Restore")
                }

                OutlinedButton(
                    onClick = onGoogleSignIn,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .testTag("google_signin_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Continue with Google"
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Sign In / Create Account with Google")
                }
            }

            CreatorSignatureStrip()
        }
    }
}

@Composable
fun OnboardingSetupScreen(
    firebaseUser: FirebaseUser,
    onComplete: (
        name: String,
        studentClass: String,
        parentName: String,
        contact: String,
        exam: String,
        plan: String,
        requestedRole: String
    ) -> Unit,
    onSignOut: () -> Unit
) {
    val isAdminAccount = firebaseUser.email.equals(StudyPlannerViewModel.ADMIN_EMAIL, ignoreCase = true)
    var name by remember { mutableStateOf(firebaseUser.displayName ?: "") }
    var studentClass by remember { mutableStateOf("Class 12") }
    var parentName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var exam by remember { mutableStateOf("JEE") }
    var plan by remember { mutableStateOf("1year") }
    var role by remember { mutableStateOf(if (isAdminAccount) "admin" else "student") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CreatorSignatureStrip()

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Complete Your Study Profile",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Signed in as ${firebaseUser.email ?: firebaseUser.uid}. Your tests, flashcards, homework, and schedule sync automatically across logins.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isAdminAccount) {
                        Text("Account Mode", style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = role == "student",
                                onClick = { role = "student" },
                                label = { Text("Student") },
                                leadingIcon = { Icon(Icons.Filled.School, contentDescription = "Student Role") }
                            )
                            FilterChip(
                                selected = role == "admin",
                                onClick = { role = "admin" },
                                label = { Text("Admin · Arsath") },
                                leadingIcon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin Role") }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name_input")
                    )

                    Text("Class / Batch", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Class 11", "Class 12", "Dropper").forEach { cls ->
                            FilterChip(
                                selected = studentClass == cls,
                                onClick = { studentClass = cls },
                                label = { Text(cls) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = parentName,
                            onValueChange = { parentName = it },
                            label = { Text("Parent Name (Opt)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = contact,
                            onValueChange = { contact = it },
                            label = { Text("Phone / Parent Contact") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Target Entrance Exam", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = exam == "JEE",
                            onClick = { exam = "JEE" },
                            label = { Text("JEE (Phy · Chem · Math)") },
                            modifier = Modifier.testTag("exam_chip_jee")
                        )
                        FilterChip(
                            selected = exam == "NEET",
                            onClick = { exam = "NEET" },
                            label = { Text("NEET (Phy · Chem · Bio)") },
                            modifier = Modifier.testTag("exam_chip_neet")
                        )
                    }

                    Text("Study Roadmap Horizon", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = plan == "1year",
                            onClick = { plan = "1year" },
                            label = { Text("1 Year (May '26 – Feb '27)") }
                        )
                        FilterChip(
                            selected = plan == "2year",
                            onClick = { plan = "2year" },
                            label = { Text("2 Year (May '26 – Jan '28)") }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            onComplete(name, studentClass, parentName, contact, exam, plan, role)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .testTag("save_onboarding_button")
                    ) {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = "Save Profile")
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Launch My Study Dashboard")
                    }

                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Switch Google Account")
                    }
                }
            }
        }
    }
}
