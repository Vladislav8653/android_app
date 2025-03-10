package com.arefin.memeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailBottomSheetDialog(private val meme: Meme, private var btnName: String) : BottomSheetDialogFragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var favoriteButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_detail, container, false)

        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        favoriteButton.text = btnName;
        viewPager = view.findViewById(R.id.viewPager)

        titleTextView.text = meme.title
        descriptionTextView.text = meme.description

        // Устанавливаем адаптер для ViewPager
        imageSliderAdapter = meme.images?.let { ImageSliderAdapter(it) }!!
        viewPager.adapter = imageSliderAdapter

        // Обработчик нажатия для добавления в избранное
        favoriteButton.setOnClickListener {
           manageFuture(meme)
        }


        return view
    }

    private fun manageFuture(meme: Meme) {
        if (favoriteButton.text == "Добавить в избранное") {
            favoriteButton.setOnClickListener {
                (context as MainActivity).addToFavorites(meme)
            }
        } else if (favoriteButton.text == "Убрать из избранных") {
            favoriteButton.setOnClickListener {
                (context as MainActivity).removeFromFavorites(meme)
            }

        }

    }


}