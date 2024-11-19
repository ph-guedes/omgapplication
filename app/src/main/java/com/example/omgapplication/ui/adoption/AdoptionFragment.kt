package com.example.omgapplication.ui.adoption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.omgapplication.databinding.FragmentAdoptionBinding


class AdoptionFragment : Fragment() {

    private var _binding: FragmentAdoptionBinding? = null
    private val binding get() = _binding!!
    private lateinit var adoptionViewModel: AdoptionViewModel
    private lateinit var animalAdapter: AnimalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        adoptionViewModel = ViewModelProvider(this).get(AdoptionViewModel::class.java)
        _binding = FragmentAdoptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animalAdapter = AnimalAdapter(emptyList())
        binding.recyclerview.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerview.adapter = animalAdapter

        adoptionViewModel.animals.observe(viewLifecycleOwner) { animals ->
            animalAdapter.updateData(animals)
        }

        binding.buttonDog.setOnClickListener { listAnimalsByType("Cachorro") }
        binding.buttonCat.setOnClickListener { listAnimalsByType("Gato") }

        listAnimalsByType("Cachorro")
    }

    private fun listAnimalsByType(type: String) {
        binding.buttonDog.isSelected = type == "Cachorro"
        binding.buttonCat.isSelected = type == "Gato"

        adoptionViewModel.fetchAnimalsByType(type)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

