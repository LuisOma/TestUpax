package com.example.pokemontest.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val sprite: String?,
    val height: Int,
    val weight: Int,
    val types: String?,
    var isFavorite: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(sprite)
        parcel.writeInt(height)
        parcel.writeInt(weight)
        parcel.writeString(types)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<PokemonEntity> {
        override fun createFromParcel(parcel: Parcel) = PokemonEntity(parcel)
        override fun newArray(size: Int) = arrayOfNulls<PokemonEntity>(size)
    }
}
