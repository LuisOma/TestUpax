package com.example.pokemontest.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.pokemontest.R
import com.example.pokemontest.databinding.FragmentImageBinding
import com.example.pokemontest.utils.Constants.DEFAULT_IMAGE_URL

class ImageFragment : Fragment() {
    private lateinit var binding: FragmentImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_image, container, false
        )

        binding.urlEditText.setText(DEFAULT_IMAGE_URL)

        val text = binding.nameTextView.text.toString().trim()

        binding.loadImageButton.setOnClickListener {
            val imageUrl = binding.urlEditText.text.toString().trim()
            val finalImageUrl = imageUrl.ifEmpty { DEFAULT_IMAGE_URL }
            binding.pokemonDetailImageView.setImageUrl(finalImageUrl, text)
        }

        binding.pokemonDetailImageView.setImageUrl("", text)
        return binding.root
    }
}
