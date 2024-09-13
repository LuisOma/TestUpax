package com.example.pokemontest.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Query("SELECT * FROM pokemon")
    suspend fun getAllPokemons(): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonEntity

    @Query("UPDATE pokemon SET isFavorite = :isFavorite WHERE id = :pokemonId")
    fun updateFavoriteStatus(pokemonId: Int, isFavorite: Boolean)
}
