package com.example.pokemontest.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pokemontest.R
import com.example.pokemontest.database.PokemonEntity
import com.example.pokemontest.databinding.FragmentDetailBinding
import com.google.gson.Gson

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail, container, false
        )
        val pokemon = arguments?.getParcelable<PokemonEntity>("pokemon")

        pokemon?.let {
            Glide.with(this)
                .load(pokemon.sprite)
                .error(R.drawable.ic_pokeball)
                .fallback(R.drawable.ic_pokeball)
                .into(binding.pokemonDetailImageView)

            binding.pokemonDetailNameTextView.text = pokemon.name.replaceFirstChar { it.uppercase() }

            binding.pokemonDetailHeightValueTextView.text = pokemon.height.toString()

            binding.pokemonDetailWeightValueTextView.text = pokemon.weight.toString()

            val types = it.types
            val typesList = parseTypes(types)
            binding.pokemonDetailTypeValueTextView.text = typesList.joinToString(", ")
        }
        return binding.root
    }

    private fun parseTypes(typesJson: String?): List<String> {
        val gson = Gson()
        val typeList = gson.fromJson(typesJson, Array<TypeWrapper>::class.java).map { it.type.name }
        return typeList
    }

    data class TypeWrapper(val type: Type)
    data class Type(val name: String)
}
