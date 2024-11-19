package com.example.omgapplication.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.omgapplication.LoginActivity
import com.example.omgapplication.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide
import com.example.omgapplication.R
import com.example.omgapplication.ui.panel.ControlPanelActivity
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null
    private val PICK_IMAGE_REQUEST = 1
    private var isFirstLoad = true
    private var imageUri: Uri? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        binding.profileImage.setOnClickListener {
            openGallery()
        }

        binding.logoutBtn.setOnClickListener {
            logout()
        }

        binding.controlPanel.setOnClickListener {
            val panelIntent = Intent(requireContext(), ControlPanelActivity::class.java)
            startActivity(panelIntent)

            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        if (auth.currentUser?.uid.toString() == "cLkNnNZB2obdPq9TUfsw00pFzgu1" ) {
            binding.controlPanel.visibility = View.VISIBLE
        } else {
            binding.controlPanel.visibility = View.GONE
        }

        profileViewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            userData?.let {
                binding.name.text = it.name
                binding.email.text = it.email
                it.profilePhoto?.let { url ->
                    Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.unnamed2)
                        .into(binding.profileImage)
                }
                if (isFirstLoad) {
                    binding.progressBar.visibility = View.GONE
                    isFirstLoad = false
                }
            }
        })

        profileViewModel.loadUserData()

        if (isFirstLoad) {
            binding.progressBar.visibility = View.VISIBLE
        }

        return root
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
            uploadImageToFirebase()
        }
    }

    private fun uploadImageToFirebase() {
        imageUri?.let { uri ->
            val userId = auth.currentUser?.uid ?: return
            val storageReference = FirebaseStorage.getInstance().reference
            val profileRef = storageReference.child("profileImages/${userId}_profileImage.png")

            val uploadTask = profileRef.putFile(uri)
            uploadTask.addOnSuccessListener {
                profileRef.downloadUrl.addOnSuccessListener { url ->
                    profileViewModel.updateProfilePhoto(url.toString())
                    Glide.with(this)
                        .load(url)
                        .into(binding.profileImage)
                    Toast.makeText(requireContext(), "Foto de perfil atualizada", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Falha ao atualizar foto de perfil", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.let { window ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.let { window ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
        _binding = null
    }

    private fun logout() {
        auth.signOut()
        startActivity(Intent(activity, LoginActivity::class.java))
        activity?.finish()
    }
}
