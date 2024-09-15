package com.example.pokemontest.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemontest.R
import com.example.pokemontest.databinding.FragmentListBinding
import com.example.pokemontest.repository.PokemonRepository
import com.example.pokemontest.ui.adapter.PokemonAdapter
import com.example.pokemontest.ui.viewmodel.PokemonViewModel
import com.example.pokemontest.ui.viewmodel.PokemonViewModelFactory

class FavoriteListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var viewModel: PokemonViewModel
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list, container, false
        )

        val repository = PokemonRepository(requireContext())
        val factory = PokemonViewModelFactory(repository, requireContext())
        viewModel = ViewModelProvider(this, factory)[PokemonViewModel::class.java]

        viewModel.fetchFavoritePokemons()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        showLoadingAnimation()

        pokemonAdapter = PokemonAdapter(
            onImageClick = { imageUrl ->
                val dialogFragment = ImageDialogFragment.newInstance(imageUrl)
                dialogFragment.show(parentFragmentManager, "ImageDialog")
            },
            onItemClick = { pokemon ->
                val action = FavoriteListFragmentDirections.actionFavoriteListFragmentToDetailFragment(pokemon)
                findNavController().navigate(action)
            },
            onFavoriteClick = { pokemon ->
                viewModel.toggleFavoriteStatus(pokemon)
            }
        )

        binding.recyclerView.adapter = pokemonAdapter

        viewModel.favoritePokemons.observe(viewLifecycleOwner) { favoriteList ->
            if (favoriteList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Sin favoritos",
                    Toast.LENGTH_LONG
                ).show()
            }
            pokemonAdapter.submitList(favoriteList)
            hideLoadingAnimation()
        }

        return binding.root
    }

    private fun showLoadingAnimation() {
        binding.lottieLoading.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun hideLoadingAnimation() {
        binding.lottieLoading.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }
}
