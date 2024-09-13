package com.example.pokemontest.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokemontest.R
import com.example.pokemontest.database.PokemonEntity
import com.example.pokemontest.utils.PokemonUtils.getTextDrawable

class PokemonAdapter(
    private var pokemonList: List<PokemonEntity>,
    private val onImageClick: (String?) -> Unit,
    private val onItemClick: (PokemonEntity) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    fun updateData(newPokemonList: List<PokemonEntity>) {
        pokemonList = newPokemonList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position]
        holder.bind(pokemon)
    }

    override fun getItemCount() = pokemonList.size

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pokemonName: TextView = itemView.findViewById(R.id.pokemonNameTextView)
        private val pokemonImage: ImageView = itemView.findViewById(R.id.pokemonImageView)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteImageView)

        fun bind(pokemon: PokemonEntity) {
            pokemonName.text = pokemon.name.replaceFirstChar { it.uppercase() }

            val fallbackBitmap = getTextDrawable(pokemon.name, itemView.context)
            val fallbackDrawable = BitmapDrawable(itemView.context.resources, fallbackBitmap)

            Glide.with(itemView.context)
                .load(pokemon.sprite)
                .placeholder(R.drawable.ic_pokeball)
                .error(fallbackDrawable)
                .fallback(fallbackDrawable)
                .into(pokemonImage)

            pokemonImage.setOnClickListener {
                onImageClick(pokemon.sprite)
            }

            itemView.setOnClickListener {
                onItemClick(pokemon)
            }
        }
    }
}
