package com.example.pokemontest.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.pokemontest.R

class ImageDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_IMAGE_URL = "image_url"

        fun newInstance(imageUrl: String?): ImageDialogFragment {
            val fragment = ImageDialogFragment()
            val args = Bundle().apply {
                putString(ARG_IMAGE_URL, imageUrl)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_image_dialog, null)
        val imageView: ImageView = view.findViewById(R.id.dialogImageView)
        val imageUrl = arguments?.getString(ARG_IMAGE_URL)

        Glide.with(requireActivity())
            .load(imageUrl)
            .error(R.drawable.ic_pokeball)
            .fallback(R.drawable.ic_pokeball)
            .into(imageView)

        return AlertDialog.Builder(requireActivity())
            .setView(view)
            .create()
    }
}
