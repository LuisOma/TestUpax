package com.example.pokemontest.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.pokemontest.R
import com.example.pokemontest.database.PokemonEntity
import com.example.pokemontest.databinding.FragmentDetailBinding
import com.example.pokemontest.repository.PokemonRepository
import com.example.pokemontest.ui.viewmodel.PokemonViewModel
import com.example.pokemontest.ui.viewmodel.PokemonViewModelFactory
import com.example.pokemontest.utils.PokemonUtils.dpToPx
import com.example.pokemontest.utils.PokemonUtils.formatQuantity
import com.example.pokemontest.utils.PokemonUtils.getTypeBackground
import com.example.pokemontest.utils.PokemonUtils.parseTypes

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private lateinit var pokemonViewModel: PokemonViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail, container, false
        )

        val repository = PokemonRepository(requireContext())
        val factory = PokemonViewModelFactory(repository, requireContext())
        pokemonViewModel = ViewModelProvider(this, factory)[PokemonViewModel::class.java]

        val pokemon = arguments?.getParcelable<PokemonEntity>("pokemon")
        Log.d("DetailFragment", "Valor de isFavorite: ${pokemon?.isFavorite}")

        pokemon?.let {
            val pokemonTypesJson: String? = pokemon.types
            val pokemonTypes = parseTypes(pokemonTypesJson)
            pokemonTypes.firstOrNull()?.let { firstType ->
                when (firstType) {
                    "fire" -> {
                        binding.headerImageView.setImageResource(R.drawable.ic_header_fire)
                    }
                    "water" -> {
                        binding.headerImageView.setImageResource(R.drawable.ic_header_water)
                    }
                    "grass" -> {
                        binding.headerImageView.setImageResource(R.drawable.ic_header_grass)
                    }
                    "electric" -> {
                        binding.headerImageView.setImageResource(R.drawable.ic_header_electric)
                    }
                    else -> {
                        binding.headerImageView.setImageResource(R.drawable.ic_header_electric)
                    }
                }
            }

            val pokemonNumber = pokemon.id.toString().padStart(3, '0')
            binding.pokemonDetailNumberTextView.text = "N° $pokemonNumber"

            Glide.with(this)
                .load(pokemon.sprite)
                .error(R.drawable.ic_pokeball)
                .fallback(R.drawable.ic_pokeball)
                .into(binding.pokemonDetailImageView)

            binding.pokemonDetailNameTextView.text = pokemon.name.replaceFirstChar { it.uppercase() }
            binding.pokemonDetailHeightValueTextView.text = formatQuantity(pokemon.height, "height")
            binding.pokemonDetailWeightValueTextView.text = formatQuantity(pokemon.weight, "weight")

            val types = it.types
            val typesList = parseTypes(types)
            binding.pokemonDetailTypeValueTextView.removeAllViews()
            typesList.forEach { type ->
                val typeTextView = TextView(context).apply {
                    text = type.capitalize()
                    setBackgroundResource(getTypeBackground(type.lowercase()))
                    setPadding(8.dpToPx(), 4.dpToPx(), 8.dpToPx(), 4.dpToPx())
                    setTextColor(Color.WHITE)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(4.dpToPx(), 4.dpToPx(), 4.dpToPx(), 4.dpToPx())
                    }
                }
                binding.pokemonDetailTypeValueTextView.addView(typeTextView)
            }

            updateFavoriteIcon(pokemon.isFavorite)

            pokemonViewModel.pokemonDetail.observe(viewLifecycleOwner) { updatedPokemon ->
                updateFavoriteIcon(updatedPokemon.isFavorite)
            }

            binding.favoriteDetailImageView.setOnClickListener {
                pokemonViewModel.toggleFavoriteStatus(pokemon)
            }

            binding.favoriteDetailImageView.setOnClickListener {
                pokemon.let {
                    pokemonViewModel.toggleFavoriteStatus(it)
                    pokemonViewModel.getPokemonById(it.id)
                }
            }
        }

        return binding.root
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoriteDetailImageView.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.favoriteDetailImageView.setImageResource(R.drawable.ic_favorite_outline)
        }
    }
}
