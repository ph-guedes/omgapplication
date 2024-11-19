package com.example.omgapplication.ui.adoption

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.omgapplication.R
import com.example.omgapplication.data.FirestoreData
import com.example.omgapplication.databinding.ActivityAnimalDetailsBinding
import com.google.firebase.auth.FirebaseAuth

class AnimalDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimalDetailsBinding
    private lateinit var firestoreData: FirestoreData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnimalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        firestoreData = FirestoreData()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val animalId = intent.getStringExtra("ANIMAL_ID") ?: "ID Desconhecido"

        var isLiked = false

        fun updateLikeButton() {
            if (isLiked) {
                binding.likeBtn.setImageResource(R.drawable.ic_heart)
            } else {
                binding.likeBtn.setImageResource(R.drawable.ic_heart_outlined)
            }
        }

        fun checkIfLiked() {
            if (userId != null) {
                firestoreData.isLikedByUser(userId, animalId) { isLikedAlready ->
                    isLiked = isLikedAlready
                    updateLikeButton()
                }
            }
        }

        checkIfLiked()

        binding.likeBtn.setOnClickListener {
            if (userId != null) {
                if (isLiked) {
                    firestoreData.removeLike(userId, animalId)
                } else {
                    firestoreData.addLike(userId, animalId)
                }
                isLiked = !isLiked
                updateLikeButton()
            }
        }

        val nome = intent.getStringExtra("NAME") ?: "Nome Desconhecido"
        val genero = intent.getStringExtra("GENDER") ?: "Gênero Desconhecido"
        val idade = intent.getStringExtra("AGE") ?: "Idade Desconhecida"
        val photo = intent.getStringExtra("PHOTO") ?: "Foto Desconhecida"
        val descriptions = intent.getStringArrayListExtra("DESCRIPTIONS") ?: arrayListOf()

        if (genero.equals("macho")) {
            binding.genderAndAge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#29ABE1"))
        } else {
            binding.genderAndAge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F68AAF"))
        }

        binding.nomeAnimal.text = nome
        binding.genderAndAge.text = "$genero, $idade"

        Glide.with(this)
            .load(photo)
            .placeholder(R.drawable.logo)
            .into(binding.animalImage)

        binding.description.text = descriptions.getOrNull(0) ?: ""
        binding.description2.text = descriptions.getOrNull(1) ?: ""
        binding.description3.text = descriptions.getOrNull(2) ?: ""
        binding.description4.text = descriptions.getOrNull(3) ?: ""
    }

}
