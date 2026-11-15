package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebase.configureAppCheck
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.StudyPlannerTheme
import com.example.viewmodel.StudyPlannerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure Firebase App Check BEFORE enableEdgeToEdge, setContent, or ViewModels
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            val studyViewModel: StudyPlannerViewModel = viewModel(
                factory = StudyPlannerViewModel.Factory(applicationContext)
            )
            val userProfile by studyViewModel.userProfile.collectAsStateWithLifecycle()
            val darkOverride by studyViewModel.darkThemeOverride.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = darkOverride ?: userProfile?.darkMode ?: systemDark

            StudyPlannerTheme(darkTheme = isDark) {
                AppNavigation(
                    viewModel = studyViewModel,
                    isDarkTheme = isDark
                )
            }
        }
    }
}
