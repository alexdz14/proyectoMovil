package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()

        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtCel = findViewById<EditText>(R.id.txtCel)
        val txtContra = findViewById<EditText>(R.id.txtContra)
        val txtContra2 = findViewById<EditText>(R.id.txtContra2)
        val btnCrear = findViewById<Button>(R.id.btnCrear)

        btnCrear.setOnClickListener {
            val nombre = txtNombre.text.toString().trim()
            val email = txtEmail.text.toString().trim()
            val celular = txtCel.text.toString().trim()
            val contra = txtContra.text.toString()
            val contra2 = txtContra2.text.toString()

            if (nombre.isEmpty() || email.isEmpty() || celular.isEmpty() || contra.isEmpty() || contra2.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!celular.matches(Regex("^[0-9]{10,15}$"))) {
                Toast.makeText(this, "Celular inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contra.length < 8) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contra != contra2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear usuario en Firebase Authentication
            auth.createUserWithEmailAndPassword(email, contra)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        // Crear objeto con los datos del usuario
                        val userMap = mapOf(
                            "nombre" to nombre,
                            "email" to email,
                            "celular" to celular
                        )

                        // Guardar en Firebase Realtime Database
                        if (userId != null) {
                            FirebaseDatabase.getInstance().reference
                                .child("Usuarios")
                                .child(userId)
                                .setValue(userMap)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, Login::class.java)
                                        startActivity(intent)
                                        finish()                                    } else {
                                        Toast.makeText(this, "Error al guardar datos: ${dbTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }

                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
