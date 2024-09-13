package com.example.pokemontest.network

import com.example.pokemontest.network.response.PokemonDetailResponse
import com.example.pokemontest.network.response.PokemonResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    fun getPokemonList(@Query("offset") offset: Int, @Query("limit") limit: Int): Call<PokemonResponse>

    @GET("pokemon/{id}")
    fun getPokemonDetail(@Path("id") id: Int): Call<PokemonDetailResponse>
}
