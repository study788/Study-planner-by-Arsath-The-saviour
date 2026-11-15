package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.ui.screens.SubjectSelectionScreen
import com.example.ui.theme.StudyPlannerTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w411dp-h915dp-xxhdpi")
class SubjectSelectionUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun switchBetweenJeeAndNeetStreams_andDrillDownIntoSubject() {
        var currentStream by mutableStateOf("JEE")
        var practicedSubject: Pair<String, String>? = null

        composeTestRule.setContent {
            StudyPlannerTheme {
                SubjectSelectionScreen(
                    examCode = currentStream,
                    completedChapters = emptyList(),
                    onSelectStream = { currentStream = it },
                    onToggleChapter = {},
                    onPracticeSubject = { stream, subject -> practicedSubject = stream to subject },
                    onPracticeChapter = { _, _, _ -> },
                    onOpenSubjectTool = { _, _ -> }
                )
            }
        }

        // Verify JEE stream subjects shown initially
        composeTestRule.onNodeWithTag("stream_card_jee").assertIsDisplayed()
        composeTestRule.onNodeWithTag("stream_card_neet").assertIsDisplayed()
        composeTestRule.onNodeWithTag("subject_card_physics").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("subject_card_chemistry").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("subject_card_mathematics").performScrollTo().assertIsDisplayed()

        // Switch stream to NEET and verify Biology appears
        composeTestRule.onNodeWithTag("stream_card_neet").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertEquals("NEET", currentStream)
        composeTestRule.onNodeWithTag("subject_card_biology").performScrollTo().assertIsDisplayed()

        // Drill down into Biology
        composeTestRule.onNodeWithTag("subject_card_biology").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("back_to_subjects_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("subject_practice_btn").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chapter-by-Chapter Drill Down", substring = true).assertIsDisplayed()

        // Trigger subject practice
        composeTestRule.onNodeWithTag("subject_practice_btn").performClick()
        assertEquals("NEET" to "Biology", practicedSubject)
    }
}
