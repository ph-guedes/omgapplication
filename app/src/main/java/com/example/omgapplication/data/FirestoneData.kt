package com.example.omgapplication.data

import android.util.Log
import com.example.omgapplication.entities.Animal
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

class FirestoreData {
    private val db = FirebaseFirestore.getInstance()

    fun addLike(userId: String, animalId: String) {
        val bookmarkData = hashMapOf(
            "animalId" to animalId,
            "userId" to userId,
            "registrationDate" to Timestamp.now()
        )

        db.collection("likes")
            .add(bookmarkData)
            .addOnSuccessListener { documentReference ->
                Log.d("FirestoreData", "Like adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreData", "Erro ao adicionar favorito: ", e)
            }
    }

    fun isLikedByUser(userId: String, animalId: String, callback: (Boolean) -> Unit) {
        db.collection("likes")
            .whereEqualTo("userId", userId)
            .whereEqualTo("animalId", animalId)
            .get()
            .addOnSuccessListener { result ->
                callback(result.documents.isNotEmpty())
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreData", "Erro ao verificar like", exception)
                callback(false)
            }
    }

    fun removeLike(userId: String, animalId: String) {
        db.collection("likes")
            .whereEqualTo("userId", userId)
            .whereEqualTo("animalId", animalId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("FirestoreData", "Like removido com sucesso.")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("FirestoreData", "Erro ao remover like", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreData", "Erro ao buscar like para remover", exception)
            }
    }

    fun addReport(photoUrl: String, latitude: Double, longitude: Double, description: String?, userId: String) {
        val reportData = hashMapOf(
            "photo" to photoUrl,
            "localization" to GeoPoint(latitude, longitude),
            "description" to description,
            "reportDate" to Timestamp.now(),
            "userId" to userId
        )

        db.collection("reports")
            .add(reportData)
            .addOnSuccessListener { documentReference ->
                Log.d("FirestoreData", "Relato adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreData", "Erro ao adicionar relato: ", e)
            }
    }

    fun addUser(id: String, name: String, email: String, profilePhoto: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "profilePhoto" to profilePhoto,
            "registrationDate" to Timestamp.now()
        )

        db.collection("users")
            .document(id)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Usuário adicionado com ID: $id")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao adicionar usuário", e)
            }
    }

    fun addAnimal(animal: Animal): Task<DocumentReference> {
        val animalData = hashMapOf(
            "name" to animal.name,
            "age" to animal.age,
            "gender" to animal.gender,
            "type" to animal.type,
            "photo" to animal.photo,
            "descriptions" to animal.descriptions.take(4),
            "registrationDate" to Timestamp.now()
        )

        return db.collection("animals")
            .add(animalData)
            .addOnSuccessListener { documentReference ->
                Log.d("FirestoreData", "Animal adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreData", "Erro ao adicionar animal", e)
            }
    }

    fun updateAnimal(documentId: String, updatedAnimal: Animal): Task<Void> {
        val animalData = hashMapOf<String, Any>(
            "name" to updatedAnimal.name,
            "age" to updatedAnimal.age,
            "gender" to updatedAnimal.gender,
            "type" to updatedAnimal.type,
            "photo" to updatedAnimal.photo,
            "descriptions" to updatedAnimal.descriptions.take(4),
            "registrationDate" to Timestamp.now()
        )

        return db.collection("animals")
            .document(documentId)
            .update(animalData)
            .addOnSuccessListener {
                Log.d("FirestoreData", "Animal atualizado com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreData", "Erro ao atualizar animal", e)
            }
    }
}
