package com.example.pokemontest.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.pokemontest.R
import com.example.pokemontest.network.response.TypeEntry
import com.google.gson.Gson

object PokemonUtils {
    fun typesToJson(types: List<TypeEntry>): String {
        val gson = Gson()
        return gson.toJson(types)
    }

    fun getTextDrawable(text: String, context: Context): Bitmap {
        val size = 100
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paintCircle = Paint().apply {
            color = context.resources.getColor(R.color.red) // Color de fondo
            isAntiAlias = true
        }

        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paintCircle)

        val paintText = Paint().apply {
            color = Color.WHITE // Color del texto
            textSize = size / 2.5f // Tamaño del texto
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val initials = getInitials(text)

        val textY = (canvas.height / 2f) - ((paintText.descent() + paintText.ascent()) / 2)

        canvas.drawText(initials, size / 2f, textY, paintText)

        return bitmap
    }

    private fun getInitials(text: String): String {
        val words = text.split(" ").filter { it.isNotBlank() }
        return if (words.size == 1) {
            words[0].substring(0, 1).uppercase()
        } else {
            words[0].substring(0, 1).uppercase() + words[1].substring(0, 1).uppercase()
        }
    }
}
