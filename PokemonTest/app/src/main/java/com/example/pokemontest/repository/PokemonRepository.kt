package com.example.pokemontest.repository

import android.content.Context
import com.example.pokemontest.database.PokemonEntity
import com.example.pokemontest.network.response.PokemonResponse
import com.example.pokemontest.database.AppDatabase
import com.example.pokemontest.database.PokemonDao
import com.example.pokemontest.network.PokeApiService
import com.example.pokemontest.network.RetrofitClient
import retrofit2.Call

class PokemonRepository(context: Context) {

    private val service: PokeApiService by lazy {
        RetrofitClient.retrofit.create(PokeApiService::class.java)
    }

    private val pokemonDao: PokemonDao = AppDatabase.getDatabase(context).pokemonDao()

    fun getPokemonList(offset: Int, limit: Int): Call<PokemonResponse> {
        return service.getPokemonList(offset, limit)
    }

    fun getPokemonDetail(id: Int) = service.getPokemonDetail(id)

    suspend fun insertPokemon(pokemon: PokemonEntity) {
        pokemonDao.insertPokemon(pokemon)
    }

    suspend fun getAllPokemons() = pokemonDao.getAllPokemons()

}
