package com.example.pokemontest.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.pokemontest.R

class CustomImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private var backgroundColor: Int = Color.RED
    private var textColor: Int = Color.WHITE
    private var placeholderResId: Int = R.drawable.ic_pokeball
    private var initials: String = ""

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomImageView,
            0, 0).apply {

            try {
                backgroundColor = getColor(R.styleable.CustomImageView_backgroundColor, Color.RED)
                textColor = getColor(R.styleable.CustomImageView_textColor, Color.WHITE)
                placeholderResId = getResourceId(R.styleable.CustomImageView_placeholder, R.drawable.ic_pokeball)
            } finally {
                recycle()
            }
        }
    }

    fun setImageUrl(url: String?, text: String) {
        if (url.isNullOrEmpty()) {
            initials = getInitials(text)
            setImageDrawable(createTextDrawable(initials, backgroundColor, textColor))
        } else {
            val requestOptions = RequestOptions()
                .placeholder(placeholderResId)
                .error(createTextDrawable(getInitials(text), backgroundColor, textColor))

            Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_pokeball)
                .apply(requestOptions)
                .into(this)
        }
    }

    private fun getInitials(text: String): String {
        val words = text.split(" ").filter { it.isNotBlank() }
        return if (words.size == 1) {
            words[0].substring(0, 1).uppercase()
        } else {
            words[0].substring(0, 1).uppercase() + words[1].substring(0, 1).uppercase()
        }
    }

    private fun createTextDrawable(text: String, backgroundColor: Int, textColor: Int): Drawable {
        val bitmap = getTextDrawable(text, backgroundColor, textColor)
        return BitmapDrawable(context.resources, bitmap)
    }

    private fun getTextDrawable(text: String, backgroundColor: Int, textColor: Int): Bitmap {
        val size = 100
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paintCircle = Paint().apply {
            color = backgroundColor
            isAntiAlias = true
        }

        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paintCircle)

        val paintText = Paint().apply {
            color = textColor
            textSize = size / 2.5f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val textY = (canvas.height / 2f) - ((paintText.descent() + paintText.ascent()) / 2)

        canvas.drawText(text, size / 2f, textY, paintText)

        return bitmap
    }
}
