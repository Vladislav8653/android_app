package com.arefin.memeapp

class Meme {
    public val title: String? = null
    public val description: String? = null
    public val images: List<String>? = null
    public var isFavorite = false // Пустой конструктор и геттеры/сеттеры
    var key: String? = null // Новое поле для хранения уникального ключа
}