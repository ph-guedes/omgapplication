package com.example.omgapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.omgapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        binding.loginButton.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    } else {
                        val exceptionMessage = when (it.exception) {
                            is FirebaseAuthInvalidCredentialsException -> "Credenciais inválidas"
                            is FirebaseAuthInvalidUserException -> "Usuário não encontrado"
                            else -> "Erro de autenticação: ${it.exception?.message}"
                        }
                        Toast.makeText(this, exceptionMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Os campos não podem ficar vazios", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerButton.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.forgotPassButton.setOnClickListener {
            Toast.makeText(this, "Ainda não possui essa funcionalidade", Toast.LENGTH_SHORT).show()
        }

    }
}
