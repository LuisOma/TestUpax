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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokemonViewModel(
    private val repository: PokemonRepository,
    private val context: Context
) : ViewModel() {

    private val _pokemons = MutableLiveData<List<PokemonEntity>>()
    val pokemons: LiveData<List<PokemonEntity>> get() = _pokemons

    private val _pokemonDetail = MutableLiveData<PokemonEntity>()
    val pokemonDetail: LiveData<PokemonEntity> get() = _pokemonDetail

    private val _pokemonList = MutableLiveData<List<PokemonEntity>>()
    val pokemonList: LiveData<List<PokemonEntity>> get() = _pokemonList

    private val _favoritePokemons = MutableLiveData<List<PokemonEntity>>()
    val favoritePokemons: LiveData<List<PokemonEntity>> get() = _favoritePokemons

    private var currentPage = 0
    var isLoading = false

    fun fetchPokemonList(isPagining: Boolean, isFirstTime: Boolean) {
        if (isLoading) {
            return
        }

        isLoading = true
        viewModelScope.launch {
            if (isInternetAvailable(context) && (isPagining || isFirstTime)) {
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
                        _pokemonDetail.postValue(pokemon)
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

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    fun toggleFavoriteStatus(pokemon: PokemonEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val newFavoriteStatus = !pokemon.isFavorite
            repository.updateFavoriteStatus(pokemon.id, newFavoriteStatus)

            val updatedPokemon = repository.getPokemonById(pokemon.id)
            val updatedList = repository.getAllPokemons()
            withContext(Dispatchers.Main) {
                _pokemonDetail.postValue(updatedPokemon)
                _pokemonList.postValue(updatedList)
                fetchFavoritePokemons()
            }
        }
    }

    fun getPokemonById(pokemonId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val pokemon = repository.getPokemonById(pokemonId)
            withContext(Dispatchers.Main) {
                _pokemonDetail.postValue(pokemon)
            }
        }
    }

    fun fetchFavoritePokemons() {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites = repository.getFavoritePokemons()
            withContext(Dispatchers.Main) {
                _favoritePokemons.postValue(favorites)
            }
        }
    }

}
