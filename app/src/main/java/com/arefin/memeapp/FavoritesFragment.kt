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
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val databaseUrl = "https://memeapp-147f1-default-rtdb.europe-west1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(databaseUrl)
        databaseReference = database.getReference("favorites")
        loadFavorites()
    }

    private fun loadFavorites() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                favoriteMemes.clear()
                for (snapshot in dataSnapshot.children) {
                    val meme = snapshot.getValue(Meme::class.java)
                    meme?.let { favoriteMemes.add(it) }
                }
                val adapter = MemeAdapter(favoriteMemes, requireActivity() as MainActivity)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun addToFavorites(userId: String, meme: Meme) {
        val memeId = databaseReference.push().key // Генерация уникального ID для мемов
        memeId?.let {
            // Сначала сохраняем мем в разделе memes (если вы еще этого не сделали)
            databaseReference.child("memes").child(it).setValue(meme).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Теперь добавляем memeId в раздел favorites для данного userId
                    databaseReference.child("favorites").child(userId).child(it).setValue(true)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "${meme.title} добавлен в избранное", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Ошибка при добавлении в избранное", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Ошибка при сохранении мема", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun removeFromFavorites(memeId: String) {
        databaseReference.child(memeId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Мем удален из избранного", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Ошибка при удалении из избранного", Toast.LENGTH_SHORT).show()
            }
        }
    }
}