package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.TestAttempt
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class StudyRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveProfile_authenticatedOwner_persistsAndRestores() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = StudyFirestoreRepository(firestore)
        val profile = UserProfile(
            userId = uid,
            name = "Alice Aspirant",
            email = ALICE_EMAIL,
            studentClass = "Class 12",
            parentName = "Ramesh",
            contact = "9876543210",
            exam = "JEE",
            plan = "1year",
            role = "student",
            active = true,
            sessionsCount = 2L,
            joinDate = "15 Nov 2026"
        )

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) { repo.saveOrUpdateUserProfile(profile) }
        assertTrue(saveResult.isSuccess)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) { repo.getUserProfile(uid) }
        assertNotNull(fetched)
        assertEquals("Alice Aspirant", fetched?.name)
        assertEquals("JEE", fetched?.exam)
    }

    @Test
    fun saveAndObserveTestAttempt_authenticatedOwner_preservesQuestions() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repo = StudyFirestoreRepository(firestore)
        val attemptId = "att_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val attempt = TestAttempt(
            id = attemptId,
            userId = uid,
            exam = "NEET",
            testType = "daily",
            title = "Daily Bio Test",
            score = 4L,
            total = 5L,
            pct = 80L,
            grade = "A",
            durationSeconds = 410L,
            questions = listOf(
                mapOf(
                    "id" to "NEET_BIO_0001",
                    "subject" to "Biology",
                    "chapter" to "Cell Biology",
                    "questionText" to "Powerhouse of the cell?",
                    "selectedIndex" to 1L,
                    "correctIndex" to 1L
                )
            ),
            dateLabel = "15 Nov 2026"
        )

        val saveRes = withTimeout(DEFAULT_TIMEOUT_MS) { repo.saveTestAttempt(uid, attempt) }
        assertTrue(saveRes.isSuccess)

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeTestAttempts(uid).first { list -> list.any { it.id == attemptId } }
        }
        val matched = emitted.first { it.id == attemptId }
        assertEquals(80L, matched.pct)
        assertEquals(1, matched.questions.size)
    }

    @Test
    fun getProfile_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repo = StudyFirestoreRepository(firestore)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.saveOrUpdateUserProfile(
                UserProfile(
                    userId = aliceUid,
                    name = "Alice",
                    email = ALICE_EMAIL,
                    exam = "JEE",
                    plan = "1year",
                    joinDate = "15 Nov 2026"
                )
            )
        }

        signInTestUser(BOB_EMAIL)
        var captured: FirebaseFirestoreException? = null
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) { repo.getUserProfile(aliceUid) }
        } catch (e: Exception) {
            captured = e.unwrapFirestoreException()
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, captured?.code)
    }

    @Test
    fun observeProfile_unauthenticatedCaller_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repo = StudyFirestoreRepository(firestore)
        var captured: FirebaseFirestoreException? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) { repo.observeUserProfile("any_uid_123").first() }
        } catch (e: Exception) {
            captured = e.unwrapFirestoreException()
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, captured?.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_rule@test.com"
        const val BOB_EMAIL = "bob_rule@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
