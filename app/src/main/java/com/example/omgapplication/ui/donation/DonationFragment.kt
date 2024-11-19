package com.example.omgapplication.ui.donation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.omgapplication.R

import com.example.omgapplication.databinding.FragmentDonationBinding

class DonationFragment : Fragment() {

    private var _binding: FragmentDonationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val donationViewModel =
            ViewModelProvider(this).get(DonationViewModel::class.java)

        _binding = FragmentDonationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonFoodDonation.setOnClickListener {
            val intent = Intent(requireContext(), FoodDonationActivity::class.java)
            startActivity(intent)

            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        binding.buttonMoneyDonation.setOnClickListener {
            val intentMoney = Intent(requireContext(), MoneyDonationActivity::class.java)
            startActivity(intentMoney)

            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
