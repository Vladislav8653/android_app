package com.arefin.memeapp

import MemeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FavoritesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val favoriteMemes = mutableListOf<Meme>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val databaseUrl = "https://memeapp-147f1-default-rtdb.europe-west1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(databaseUrl)
        databaseReference = database.getReference("memes")
        loadFavorites()
    }

    private fun loadFavorites() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val favoriteMemes = mutableListOf<Meme>()
                for (snapshot in dataSnapshot.children) {
                    val meme = snapshot.getValue(Meme::class.java)
                }
                // Обновите адаптер или UI с использованием списка favoriteMemes
                updateFavoritesUI(favoriteMemes)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Метод для обновления интерфейса
    private fun updateFavoritesUI(favoriteMemes: List<Meme>) {
        val adapter = FavoriteMemeAdapter(favoriteMemes, requireActivity() as MainActivity) { meme, isAdding ->
            if (isAdding) {
                // Обработка добавления в избранные

            } else {
                // Обработка удаления из избранных
                removeFromFavorites(meme)
            }
        }

        recyclerView.adapter = adapter
    }


    private fun removeFromFavorites(meme: Meme) {
        /*meme.isFavorite = false // Устанавливаем isFavorite в false
        // Обновляем мем в базе данных
        meme.key?.let { key ->
            databaseReference.child(key).setValue(meme).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "${meme.title} убран из избранного", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Ошибка при удалении из избранного", Toast.LENGTH_SHORT).show()
                }
            }
        }*/
    }
}