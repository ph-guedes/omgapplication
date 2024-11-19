package com.example.omgapplication.ui.adoption

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omgapplication.entities.Animal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AdoptionViewModel : ViewModel() {

    private val _animals = MutableLiveData<List<Animal>>()
    val animals: LiveData<List<Animal>> = _animals

    private val db = FirebaseFirestore.getInstance()

    fun fetchAnimalsByType(type: String) {
        db.collection("animals")
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                val animalList = result.documents.mapNotNull { doc ->
                    val animal = doc.toObject(Animal::class.java)
                    animal?.copy(id = doc.id)
                }
                _animals.value = animalList
            }
            .addOnFailureListener { exception ->
                Log.e("AdoptionViewModel", "Erro ao buscar animais por tipo", exception)
            }
    }
}
