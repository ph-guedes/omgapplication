package com.example.omgapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.omgapplication.data.FirestoreData
import com.example.omgapplication.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        window.navigationBarColor = android.graphics.Color.WHITE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    window.decorView.systemUiVisibility or
                            android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    )
        }

        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        binding.registerRButton.setOnClickListener {
            val name = binding.inputRName.text.toString()
            val email = binding.inputREmail.text.toString()
            val password = binding.inputRPass.text.toString()
            val repeatPass = binding.inputRepeatPass.text.toString()

            if (validateInputs(name, email, password, repeatPass)) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        val defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/vizinho-amigo-firebase.firebasestorage.app/o/unnamed2.jpg?alt=media&token=3b5c3348-8a27-4764-8e47-2bf95eefc2e5"
                        addUserToFirestore(userId, name, email, defaultImageUrl)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        handleRegistrationError(task.exception)
                    }
                }
            }
        }

        binding.loginRButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun validateInputs(name: String, email: String, password: String, repeatPass: String): Boolean {
        return when {
            name.isEmpty() -> {
                Toast.makeText(this, "O campo Nome não pode estar vazio.", Toast.LENGTH_SHORT).show()
                false
            }
            !isValidName(name) -> {
                Toast.makeText(this, "O nome deve ter pelo menos 2 caracteres e conter apenas letras.", Toast.LENGTH_SHORT).show()
                false
            }
            email.isEmpty() -> {
                Toast.makeText(this, "O campo E-mail não pode estar vazio.", Toast.LENGTH_SHORT).show()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show()
                false
            }
            password.isEmpty() || repeatPass.isEmpty() -> {
                Toast.makeText(this, "Os campos de senha não podem estar vazios.", Toast.LENGTH_SHORT).show()
                false
            }
            password != repeatPass -> {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun isValidName(name: String): Boolean {
        return name.length >= 2 && name.matches(Regex("^[a-zA-Z\\s]*$"))
    }

    private fun handleRegistrationError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException -> "A senha deve ter pelo menos 6 caracteres"
            is FirebaseAuthInvalidCredentialsException -> "O e-mail é inválido"
            is FirebaseAuthUserCollisionException -> "Este e-mail já está em uso"
            else -> "Erro ao registrar. Tente novamente."
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun addUserToFirestore(userId: String, name: String, email: String, profilePhoto: String) {
        val firestoreData = FirestoreData()
        firestoreData.addUser(userId, name, email, profilePhoto)
    }
}
