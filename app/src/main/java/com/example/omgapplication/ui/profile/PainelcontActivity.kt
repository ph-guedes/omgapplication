//package com.example.omgapplication.ui.profile
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.Spinner
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.bumptech.glide.Glide
//import com.example.omgapplication.R
//import com.example.omgapplication.databinding.ActivityPainelcontBinding
//
//class PainelcontActivity : AppCompatActivity(){
//
//    private lateinit var binding: ActivityPainelcontBinding
//    private val PICK_IMAGE_REQUEST = 1
//    private var imageUri: Uri? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_control_panel)
//        binding = ActivityPainelcontBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        supportActionBar?.hide()
//
//        val editTextNome = findViewById<EditText>(R.id.editTextNome)
//        val editTextRaca = findViewById<EditText>(R.id.editTextRaca)
//        val editTextIdade = findViewById<EditText>(R.id.editTextIdade)
//        val buttonEscolherFoto = findViewById<ImageView>(R.id.animalImage2)
//        val buttonCadastrar = findViewById<Button>(R.id.buttonCadastrar)
//
//        val spinner: Spinner = findViewById(R.id.ageSpinner)
//
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.age_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            spinner.adapter = adapter
//        }
//
//        buttonCadastrar.setOnClickListener {
//            val nome = editTextNome.text.toString()
//            val raca = editTextRaca.text.toString()
//            val idade = editTextIdade.text.toString()
//
//            if (nome.isEmpty() || raca.isEmpty() || idade.isEmpty()) {
//                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            Toast.makeText(this, "Animal cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
//
//            editTextNome.text.clear()
//            editTextRaca.text.clear()
//            editTextIdade.text.clear()
//        }
//
//        buttonEscolherFoto.setOnClickListener {
//            openGallery()
//        }
//    }
//
//    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, PICK_IMAGE_REQUEST)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            imageUri = data.data
//            Glide.with(this)
//                .load(imageUri)
//                .into(binding.animalImage2)
//        }
//    }
//}
