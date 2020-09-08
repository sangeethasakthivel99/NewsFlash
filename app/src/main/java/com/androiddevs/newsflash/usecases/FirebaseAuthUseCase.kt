package com.androiddevs.newsflash.usecases

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseAuthUseCase(val context: Context) {

    companion object {
        const val TAG = "FirebaseAuthUseCase"
    }

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private fun authenticateUser(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e(TAG, "Google Sign In SuccessFul")
            } else {
                Log.e(TAG, "Google Sign In failed")
            }
        }
    }
}