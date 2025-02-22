package com.arefin.memeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import android.widget.Button
import android.widget.TextView

class DetailActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var viewPager: ViewPager
    private lateinit var favoriteButton: Button
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        viewPager = findViewById(R.id.viewPager)
        favoriteButton = findViewById(R.id.favoriteButton)

        // Получение данных из Intent
        val title = intent.getStringExtra("TITLE") ?: "Нет названия"
        val description = intent.getStringExtra("DESCRIPTION") ?: "Нет описания"
        val images = intent.getStringArrayListExtra("IMAGES") ?: arrayListOf()

        // Установка данных в элементы
        titleTextView.text = title
        descriptionTextView.text = description

        // Настройка ViewPager
        val adapter = ImageSliderAdapter(images)
        viewPager.adapter = adapter

        // Обработка нажатия на кнопку "Добавить в избранное"
        favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            val buttonText = if (isFavorite) "Убрать из избранного" else "Добавить в избранное"
            favoriteButton.text = buttonText
            // Здесь можно добавить логику для сохранения в избранное
        }
    }
}