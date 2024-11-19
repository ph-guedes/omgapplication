package com.example.omgapplication.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omgapplication.entities.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> get() = _userData

    fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val profilePhoto = document.getString("profilePhoto")

                        _userData.value = UserData(name, email, profilePhoto)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileViewModel", "Erro ao buscar usuário", exception)
                }
        }
    }

    fun updateProfilePhoto(url: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .update("profilePhoto", url)
            .addOnSuccessListener {
                Log.d("ProfileViewModel", "Foto atualizada com sucesso")
            }
            .addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Erro ao atualizar a foto de perfil", e)
            }
    }

}