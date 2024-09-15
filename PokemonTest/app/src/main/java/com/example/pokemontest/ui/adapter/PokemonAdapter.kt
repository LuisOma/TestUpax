package com.example.pokemontest.ui.adapter

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokemontest.R
import com.example.pokemontest.database.PokemonEntity
import com.example.pokemontest.utils.PokemonUtils.getTextDrawable
import com.example.pokemontest.utils.PokemonUtils.parseTypes

class PokemonAdapter(
    private val onImageClick: (String?) -> Unit,
    private val onItemClick: (PokemonEntity) -> Unit,
    private val onFavoriteClick: (PokemonEntity) -> Unit
) : ListAdapter<PokemonEntity, PokemonAdapter.PokemonViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PokemonEntity>() {
            override fun areItemsTheSame(oldItem: PokemonEntity, newItem: PokemonEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PokemonEntity, newItem: PokemonEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = getItem(position)
        holder.bind(pokemon)
    }

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pokemonName: TextView = itemView.findViewById(R.id.pokemonNameTextView)
        private val pokemonImage: ImageView = itemView.findViewById(R.id.pokemonImageView)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favoriteImageView)
        private val pokemonTypeImageView: ImageView = itemView.findViewById(R.id.pokemonTypeImageView)
        private val pokemonNumberTextView: TextView = itemView.findViewById(R.id.pokemonNumberTextView)

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

            updateFavoriteIcon(pokemon.isFavorite)

            favoriteIcon.setOnClickListener {
                onFavoriteClick(pokemon)
            }

            pokemonImage.setOnClickListener {
                onImageClick(pokemon.sprite)
            }

            itemView.setOnClickListener {
                onItemClick(pokemon)
            }

            val pokemonTypesJson: String? = pokemon.types
            val pokemonTypes = parseTypes(pokemonTypesJson)
            pokemonTypes.firstOrNull()?.let { firstType ->
                when (firstType) {
                    "fire" -> {
                        pokemonTypeImageView.setImageResource(R.drawable.rectangular_fire)
                    }
                    "water" -> {
                        pokemonTypeImageView.setImageResource(R.drawable.rectangular_water)
                    }
                    "grass" -> {
                        pokemonTypeImageView.setImageResource(R.drawable.rectangular_grass)
                    }
                    "electric" -> {
                        pokemonTypeImageView.setImageResource(R.drawable.rectangular_electric)
                    }
                    else -> {
                        pokemonTypeImageView.setImageResource(R.drawable.rectangular_normal)
                    }
                }
            }
            val pokemonNumber = pokemon.id.toString().padStart(3, '0')
            pokemonNumberTextView.text = "N° $pokemonNumber"
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            if (isFavorite) {
                favoriteIcon.setImageResource(R.drawable.ic_favorite)
            } else {
                favoriteIcon.setImageResource(R.drawable.ic_favorite_outline)
            }
        }
    }
}
