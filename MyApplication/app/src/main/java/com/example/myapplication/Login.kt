package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializa FirebaseAuth
        auth = FirebaseAuth.getInstance()

        val formEmail = findViewById<EditText>(R.id.formEmail)
        val formContra = findViewById<EditText>(R.id.formContra)
        val btnCrear = findViewById<Button>(R.id.btnCrear)
        val btnRegistro = findViewById<Button>(R.id.btnRegistro)

        btnCrear.setOnClickListener {
            val email = formEmail.text.toString().trim()
            val contra = formContra.text.toString()

            // Validaciones
            if (email.isEmpty() || contra.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Inicia sesión con Firebase
            auth.signInWithEmailAndPassword(email, contra)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MiPuesto::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnRegistro.setOnClickListener {
            // Ir a la pantalla de registro
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }
    }
}
