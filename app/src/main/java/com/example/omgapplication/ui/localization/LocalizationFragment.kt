package com.example.omgapplication.ui.localization

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.omgapplication.R
import com.example.omgapplication.data.FirestoreData
import com.example.omgapplication.databinding.FragmentLocalizationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocalizationFragment : Fragment() {

    private var _binding: FragmentLocalizationBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialog: ProgressDialog
    private lateinit var firestoreData: FirestoreData
    private lateinit var storageReference: StorageReference
    private var photoUri: Uri? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var getLocationLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val localizationViewModel = ViewModelProvider(this).get(LocalizationViewModel::class.java)
        _binding = FragmentLocalizationBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        storageReference = FirebaseStorage.getInstance().reference.child("reportImages")
        setupPermissionLaunchers()
        firestoreData = FirestoreData()
        progressDialog = ProgressDialog(requireContext(), R.style.CustomProgressDialog).apply {
            setMessage("Enviando relato...")
            setCancelable(false)
        }

        savedInstanceState?.getString("photoUri")?.let {
            photoUri = Uri.parse(it)
            loadImageIntoPlaceholder()
        }

        binding.imageFrame.setOnClickListener {
            checkCameraPermission()
        }

        binding.buttonMap.setOnClickListener {
            checkLocationPermission()
        }

        binding.buttonSubmit.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            photoUri?.let { uri ->
                progressDialog.show()
                uploadImageToStorage(uri) { photoUrl ->
                    progressDialog.dismiss()
                    if (photoUrl != null && latitude != null && longitude != null) {
                        firestoreData.addReport(photoUrl, latitude!!, longitude!!, binding.editTextDescription.text.toString(), userId)
                        Toast.makeText(requireContext(), "Relato enviado com sucesso!", Toast.LENGTH_SHORT).show()

                        clearFields()
                    } else {
                        Toast.makeText(requireContext(), "Dados incompletos para enviar o relato.", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                Toast.makeText(requireContext(), "Capture uma foto primeiro.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun setupPermissionLaunchers() {
        requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openCamera() else showPermissionDeniedDialog()
        }

        requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) getCurrentLocation() else showPermissionDeniedDialog()
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                loadImageIntoPlaceholder()
            } else {
                Toast.makeText(requireContext(), "Falha ao capturar foto", Toast.LENGTH_SHORT).show()
            }
        }

        getLocationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.let {
                    latitude = it.getDoubleExtra("latitude", 0.0)
                    longitude = it.getDoubleExtra("longitude", 0.0)
                    binding.buttonMap.isEnabled = false
                    binding.buttonMap.background = ContextCompat.getDrawable(requireContext(), R.drawable.location_selector)
                }
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> openCamera()
            else -> requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val imageFile = createImageFile()
        photoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", imageFile)

        takePictureLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> getCurrentLocation()
            else -> requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setTitle("Permissão Negada")
            .setMessage("A permissão é necessária. Você deseja ir para as configurações do aplicativo?")
            .setPositiveButton("Sim") { _, _ -> openAppSettings() }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${requireActivity().packageName}")
        }
        startActivity(intent)
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        val intent = Intent(requireContext(), MapActivity::class.java).apply {
                            putExtra("latitude", latitude)
                            putExtra("longitude", longitude)
                        }
                        getLocationLauncher.launch(intent)
                    } else {
                        Toast.makeText(requireContext(), "Localização não encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Falha ao obter a localização", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Permissão de localização não concedida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImageIntoPlaceholder() {
        photoUri?.let {
            Glide.with(this)
                .load(it)
                .into(binding.imagePlaceholder)
        }
    }

    private fun uploadImageToStorage(uri: Uri, onUploadComplete: (String?) -> Unit) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${timestamp}_reportImage.jpg"
        val imageRef = storageReference.child(fileName)

        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onUploadComplete(downloadUri.toString())
                }.addOnFailureListener { exception ->
                    Log.e("Upload", "Falha no upload: ${exception.message}")
                    onUploadComplete(null)
                    Toast.makeText(requireContext(), "Falha ao obter URL da imagem", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                onUploadComplete(null)
                Toast.makeText(requireContext(), "Falha ao enviar imagem: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        photoUri = null
        latitude = null
        longitude = null

        binding.editTextDescription.text.clear()

        binding.buttonMap.isEnabled = true
        binding.buttonMap.background = ContextCompat.getDrawable(requireContext(), R.drawable.location_button_bg)

        binding.imagePlaceholder.setImageDrawable(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("photoUri", photoUri?.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
