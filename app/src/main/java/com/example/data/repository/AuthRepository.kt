package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthRepository"

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        trySend(firebaseAuth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

class AuthRepository(
    private val auth: FirebaseAuth = Firebase.auth
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun observeAuthState(): Flow<FirebaseUser?> = auth.authStateFlow()

    /**
     * Attempts silent / returning-account Google sign-in first via GetGoogleIdOption
     * (filterByAuthorizedAccounts = true), and falls back to the interactive
     * GetSignInWithGoogleOption picker if no previously authorized account was selected.
     */
    suspend fun signInWithGoogle(
        activityContext: Context,
        preferReturningSilent: Boolean = false
    ): Result<FirebaseUser> {
        return try {
            val serverClientId = activityContext.getString(R.string.default_web_client_id)
            val credentialManager = CredentialManager.create(activityContext)

            val idToken = if (preferReturningSilent) {
                try {
                    val returningOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(true)
                        .setAutoSelectEnabled(true)
                        .setServerClientId(serverClientId)
                        .build()
                    val returningReq = GetCredentialRequest.Builder()
                        .addCredentialOption(returningOption)
                        .build()
                    val result = credentialManager.getCredential(activityContext, returningReq)
                    extractGoogleIdToken(result.credential)
                } catch (e: NoCredentialException) {
                    Log.d(TAG, "No previously authorized account; launching interactive Google Sign-In.")
                    requestInteractiveGoogleToken(activityContext, credentialManager, serverClientId)
                }
            } else {
                requestInteractiveGoogleToken(activityContext, credentialManager, serverClientId)
            }

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            val user = authResult.user
                ?: return Result.failure(IllegalStateException("Google Sign-In succeeded but user is null."))
            Result.success(user)
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Sign-in cancelled. Tap Continue with Google when ready.", e))
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In failed", e)
            Result.failure(e)
        }
    }

    private suspend fun requestInteractiveGoogleToken(
        activityContext: Context,
        credentialManager: CredentialManager,
        serverClientId: String
    ): String {
        val option = GetSignInWithGoogleOption.Builder(serverClientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
        val response = credentialManager.getCredential(activityContext, request)
        return extractGoogleIdToken(response.credential)
    }

    private fun extractGoogleIdToken(credential: androidx.credentials.Credential): String {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
            return googleCred.idToken
        }
        throw IllegalStateException("Unsupported credential type: ${credential.type}")
    }

    suspend fun signOut(context: Context) {
        auth.signOut()
        runCatching {
            CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
        }
    }
}
