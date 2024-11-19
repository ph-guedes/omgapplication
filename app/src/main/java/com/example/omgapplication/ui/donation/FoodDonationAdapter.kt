package com.example.omgapplication.ui.donation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.omgapplication.R
import com.example.omgapplication.entities.Locations

class FoodDonationAdapter(private val listLocations: List<Locations>, private val listener: OnLocationClickListener) :
    RecyclerView.Adapter<FoodDonationAdapter.FoodDonationViewHolder>() {

        inner class FoodDonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nomeLocal: TextView = itemView.findViewById(R.id.nome_local)
            val horario: TextView = itemView.findViewById(R.id.horario)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodDonationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.locations_item, parent, false)
        return FoodDonationViewHolder(view)
        }

    override fun onBindViewHolder(holder: FoodDonationViewHolder, position: Int) {
        val local = listLocations[position]
        holder.nomeLocal.text = local.nome
        holder.horario.text = local.horario

        holder.itemView.setOnClickListener {
            listener.onLocationClicked(local.latitude, local.longitude)
        }
    }

    interface OnLocationClickListener {
        fun onLocationClicked(latitude: Double, longitude: Double)
    }

    override fun getItemCount(): Int = listLocations.size
    }