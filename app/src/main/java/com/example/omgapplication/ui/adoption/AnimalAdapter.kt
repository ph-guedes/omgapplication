package com.example.omgapplication.ui.adoption

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.omgapplication.R
import com.example.omgapplication.entities.Animal

class AnimalAdapter(private var listaDeAnimais: List<Animal>) :
    RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    inner class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeAnimal: TextView = itemView.findViewById(R.id.nome_animal)
        val imagemAnimal: ImageView = itemView.findViewById(R.id.image_animal)
        val generoeidade: TextView = itemView.findViewById(R.id.genderAndAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.animal_item, parent, false)
        return AnimalViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = listaDeAnimais[position]

        if (animal.gender.equals("macho", ignoreCase = true)) {
            holder.generoeidade.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#29ABE1"))
        } else {
            holder.generoeidade.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F68AAF"))
        }

        holder.nomeAnimal.text = animal.name
        holder.generoeidade.text = "${animal.gender}, ${animal.age}"

        val imageUrl = animal.photo
        if (imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .into(holder.imagemAnimal)
        } else {
            holder.imagemAnimal.setImageResource(R.drawable.logo)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AnimalDetailsActivity::class.java).apply {
                putExtra("ANIMAL_ID", animal.id)
                putExtra("NAME", animal.name)
                putExtra("GENDER", animal.gender)
                putExtra("AGE", animal.age)
                putExtra("PHOTO", animal.photo)
                putExtra("DESCRIPTIONS", ArrayList(animal.descriptions))
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listaDeAnimais.size

    fun updateData(novaListaDeAnimais: List<Animal>) {
        listaDeAnimais = novaListaDeAnimais
        notifyDataSetChanged()
    }
}
