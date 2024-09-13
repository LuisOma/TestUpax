package com.example.pokemontest.ui.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemontest.database.PokemonEntity
import com.example.pokemontest.utils.PokemonUtils.typesToJson
import com.example.pokemontest.network.response.PokemonDetailResponse
import com.example.pokemontest.network.response.PokemonResponse
import com.example.pokemontest.repository.PokemonRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokemonViewModel(
    private val repository: PokemonRepository,
    private val context: Context
) : ViewModel() {

    private val _pokemons = MutableLiveData<List<PokemonEntity>>()
    val pokemons: LiveData<List<PokemonEntity>> get() = _pokemons
    private var currentPage = 0
    var isLoading = false

    init {
        fetchPokemonList()
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    fun fetchPokemonList() {
        if (isLoading) {
            return
        }

        isLoading = true
        viewModelScope.launch {
            if (isInternetAvailable(context)) {
                fetchPokemonsFromApi()
            } else {
                val localPokemons = repository.getAllPokemons()
                if (localPokemons.isNotEmpty()) {
                    _pokemons.postValue(localPokemons)
                } else {
                    _pokemons.postValue(emptyList())
                }
                isLoading = false
            }
        }
    }

    private fun fetchPokemonsFromApi() {
        repository.getPokemonList(25, currentPage * 25).enqueue(object : Callback<PokemonResponse> {
            override fun onResponse(call: Call<PokemonResponse>, response: Response<PokemonResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    val results = response.body()?.results ?: emptyList()
                    results.forEachIndexed { index, _ ->
                        val pokemonId = currentPage * 25 + index + 1
                        fetchPokemonDetail(pokemonId)
                    }
                    currentPage++
                } else {
                    Log.e("PokeAPI", "Error fetching pokemon list: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PokemonResponse>, t: Throwable) {
                isLoading = false
                Log.e("PokeAPI", "Failed to fetch pokemon list", t)
            }
        })
    }


    private fun fetchPokemonDetail(pokemonId: Int) {
        repository.getPokemonDetail(pokemonId).enqueue(object : Callback<PokemonDetailResponse> {
            override fun onResponse(call: Call<PokemonDetailResponse>, response: Response<PokemonDetailResponse>) {
                if (response.isSuccessful) {
                    val pokemonDetail = response.body()
                    pokemonDetail?.let { detail ->
                        val typesJson = typesToJson(detail.types)
                        val pokemon = PokemonEntity(
                            id = pokemonId,
                            name = detail.name,
                            sprite = detail.sprites.frontDefault,
                            height = detail.height,
                            weight = detail.weight,
                            types = typesJson,
                            isFavorite = false
                        )

                        viewModelScope.launch {
                            repository.insertPokemon(pokemon)
                            val updatedList = repository.getAllPokemons()
                            _pokemons.postValue(updatedList)
                        }

                    }
                } else {
                    Log.e("PokeAPI", "Error fetching pokemon details for ID: $pokemonId")
                }
            }

            override fun onFailure(call: Call<PokemonDetailResponse>, t: Throwable) {
                Log.e("PokeAPI", "Failed to fetch pokemon details for ID: $pokemonId", t)
            }
        })
    }
}
