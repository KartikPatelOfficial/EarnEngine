package com.deucate.earnengine.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.deucate.earnengine.HomeActivity
import com.deucate.earnengine.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) {
            startHomeActivity()
        }
        setContentView(R.layout.activity_login)

        loginLoginButton.setOnClickListener {
            val email = loginEmailAddress.text.toString()
            val password = loginPassword.text.toString()

            if (email.isEmpty()) {
                loginEmailAddress.error = "Please enter email address."
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                loginPassword.error = "Please enter password."
                return@setOnClickListener
            }

            loginWithEmailAndPassword(email, password)
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun loginWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                startHomeActivity()
            } else {
                AlertDialog.Builder(this).setTitle("Error")
                    .setMessage(it.exception!!.localizedMessage).show()
            }
        }
    }
}
