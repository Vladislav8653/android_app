package com.arefin.memeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteMemeAdapter(
    private val memes: List<Meme>,
    private val listener: OnMemeClickListener,
    private val onFavoriteToggled: (Meme, Boolean) -> Unit
) : RecyclerView.Adapter<FavoriteMemeAdapter.MemeViewHolder>() {

    inner class MemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.meme_title)
        val description: TextView = itemView.findViewById(R.id.meme_description)
        val buttonRemoveFavorite: Button = itemView.findViewById(R.id.button_remove_favorite)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val meme = memes[position]
                listener.onMemeClick(meme)
            }

            // Обработчик нажатия на кнопку "Убрать из избранных"
            buttonRemoveFavorite.setOnClickListener {
                val position = adapterPosition
                val meme = memes[position]
                onFavoriteToggled(meme, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meme_item, parent, false)
        return MemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val meme = memes[position]
        holder.title.text = meme.title
        holder.description.text = meme.description
        holder.buttonRemoveFavorite.visibility = View.VISIBLE // Всегда показываем кнопку удаления
    }

    override fun getItemCount(): Int = memes.size
}