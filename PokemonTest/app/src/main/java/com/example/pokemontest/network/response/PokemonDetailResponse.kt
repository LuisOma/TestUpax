package com.example.pokemontest.network.response

import com.google.gson.annotations.SerializedName

data class PokemonDetailResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("sprites") val sprites: Sprites,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("types") val types: List<TypeEntry>
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?
)

data class TypeEntry(
    @SerializedName("type") val type: Type
)

data class Type(
    @SerializedName("name") val name: String?
)
