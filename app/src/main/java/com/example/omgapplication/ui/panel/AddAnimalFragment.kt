package com.example.omgapplication.ui.panel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.omgapplication.R
import com.example.omgapplication.data.FirestoreData
import com.example.omgapplication.databinding.FragmentAddAnimalBinding
import com.example.omgapplication.entities.Animal
import com.google.firebase.storage.FirebaseStorage

class AddAnimalFragment : Fragment() {

    private var _binding: FragmentAddAnimalBinding? = null
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var firestoreData: FirestoreData
    private lateinit var animal: Animal

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addAnimalViewModel = ViewModelProvider(this).get(AddAnimalViewModel::class.java)
        _binding = FragmentAddAnimalBinding.inflate(inflater, container, false)

        firestoreData = FirestoreData()

        setupAgeWatcher()

        val spinner2: Spinner = binding.spinnerGender
        this.context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner2.adapter = adapter
            }
        }

        binding.buttonCadastrar.setOnClickListener {
            addAnimal()
        }

        binding.animalImage2.setOnClickListener {
            openGallery()
        }

        binding.buttonDog.setOnClickListener {
            binding.buttonDog.isSelected = true
            binding.buttonCat.isSelected = false

            clearFields()
        }
        binding.buttonCat.setOnClickListener {
            binding.buttonDog.isSelected = false
            binding.buttonCat.isSelected = true

            clearFields()
        }

        binding.buttonDog.isSelected = true
        binding.buttonCat.isSelected = false

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data

            Glide.with(this)
                .load(imageUri)
                .into(binding.animalImage2)
        }
    }

    private fun uploadImageToStorage(documentId: String, callback: (String?) -> Unit) {
        if (imageUri != null) {
            val storageReference = FirebaseStorage.getInstance().reference
            val fileName = "animalImages/${documentId}_animalImage.png"
            val fileRef = storageReference.child(fileName)

            fileRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        val photoUrl = uri.toString()
                        callback(photoUrl)
                    }
                }
                .addOnFailureListener {
                    callback(null)
                }
        } else {
            callback(null)
        }
    }

    private fun setupAgeWatcher() {
        binding.editTextIdade.addTextChangedListener { editable ->
            val age = editable.toString().toDoubleOrNull()

            if (age != null) {
                runSpinners(age)
            }
        }
    }

    private fun runSpinners(age: Double) {
        val spinner: Spinner = binding.ageSpinner

        if (age > 1) {
            this.context?.let {
                ArrayAdapter.createFromResource(
                    it,
                    R.array.age_array,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
            }
        } else {
            this.context?.let {
                ArrayAdapter.createFromResource(
                    it,
                    R.array.age2_array,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
            }
        }
    }

    private fun clearFields() {
        imageUri = null
        binding.editTextNome.text.clear()
        binding.editTextIdade.text.clear()
        binding.description.text.clear()
        binding.description2.text.clear()
        binding.description3.text.clear()
        binding.description4.text.clear()
        binding.ageSpinner.setSelection(0)
        binding.spinnerGender.setSelection(0)
        binding.animalImage2.setImageDrawable(null)
    }


    private fun addAnimal() {
        val name = binding.editTextNome.text.toString().trim()
        val age = "${binding.editTextIdade.text} ${binding.ageSpinner.selectedItem.toString()}".trim()
        val gender = binding.spinnerGender.selectedItem.toString()

        val type = when {
            binding.buttonDog.isSelected -> "Cachorro"
            binding.buttonCat.isSelected -> "Gato"
            else -> "Outro"
        }

        val descriptions = listOf(
            binding.description.text.toString().trim(),
            binding.description2.text.toString().trim(),
            binding.description3.text.toString().trim(),
            binding.description4.text.toString().trim()
        )

        animal = Animal(null, name, age, gender, type, "", descriptions)

        firestoreData.addAnimal(animal).addOnSuccessListener { documentReference ->
            val documentId = documentReference.id

            uploadImageToStorage(documentId) { photoUrl ->
                if (photoUrl != null) {
                    animal.photo = photoUrl

                    firestoreData.updateAnimal(documentId, animal).addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Animal cadastrado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Falha ao atualizar a imagem do animal",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Falha ao carregar a imagem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


}
