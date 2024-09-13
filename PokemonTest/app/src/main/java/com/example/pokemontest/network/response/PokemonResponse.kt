package com.example.pokemontest.network.response

data class PokemonResponse(
    val results: List<Pokemon>
)

data class Pokemon(
    val id: Int,
    val name: String,
    val url: String
)
