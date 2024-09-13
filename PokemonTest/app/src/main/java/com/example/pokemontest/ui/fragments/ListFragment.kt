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
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemontest.R
import com.example.pokemontest.databinding.FragmentListBinding
import com.example.pokemontest.repository.PokemonRepository
import com.example.pokemontest.ui.adapter.PokemonAdapter
import com.example.pokemontest.ui.viewmodel.PokemonViewModel
import com.example.pokemontest.ui.viewmodel.PokemonViewModelFactory

class ListFragment : Fragment() {

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

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        pokemonAdapter = PokemonAdapter(
            emptyList(),
            onImageClick = { imageUrl ->
                val dialogFragment = ImageDialogFragment.newInstance(imageUrl.toString())
                dialogFragment.show(parentFragmentManager, "ImageDialog")
            },
            onItemClick = { pokemon ->
                val action = ListFragmentDirections.actionListFragmentToDetailFragment(pokemon)
                findNavController().navigate(action)
            }
        )
        binding.recyclerView.adapter = pokemonAdapter

        viewModel.pokemons.observe(viewLifecycleOwner) { pokemonList ->
            if (pokemonList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.internet),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                pokemonAdapter.updateData(pokemonList)
            }
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!viewModel.isLoading && totalItemCount <= (lastVisibleItemPosition + 5)) {
                    viewModel.fetchPokemonList()
                }
            }
        })
        return binding.root
    }
}
