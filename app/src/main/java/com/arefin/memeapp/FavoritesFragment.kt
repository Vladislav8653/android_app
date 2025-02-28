package com.arefin.memeapp

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
    private var userId: String? = null

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: String): FavoritesFragment {
            val fragment = FavoritesFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString(ARG_USER_ID)
    }



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
        loadFavorites { favoritesList ->
            loadMemes(favoritesList)
        }

    }

    private fun loadMemes(favorites: List<Favorites>) {
        databaseReference = database.getReference("memes")
        var favoritesMemes: List<String>? = listOf();
        for(item in favorites) {
            if (item.userId == userId) {
                favoritesMemes = item.memes;
            }
        }
        if (favoritesMemes.isNullOrEmpty()) {
            return;
        }
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val memeList = mutableListOf<Meme>()
                for (snapshot in dataSnapshot.children) {
                    val meme = snapshot.getValue(Meme::class.java)
                    if (meme != null) {
                        if(favoritesMemes.contains(meme.id)) {
                            meme.let { memeList.add(it) }
                        }
                    }

                }
                val adapter = FavoriteMemeAdapter(memeList, requireActivity() as MainActivity)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadFavorites(callback: (List<Favorites>) -> Unit) {
        databaseReference = database.getReference("favorites")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val favoritesList = mutableListOf<Favorites>()
                for (snapshot in dataSnapshot.children) {
                    val favorite = snapshot.getValue(Favorites::class.java)
                    if (favorite != null) {
                        favoritesList.add(favorite)
                    }
                }
                callback(favoritesList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun addMemeToDatabase() {
        // Получаем ссылку на базу данных
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference

        // Создаем уникальный ключ для нового мема
        val memeId = database.child("favorites").push().key

        if (memeId != null) {
            // Добавляем мем в базу данных
            database.child("favorites").child(memeId).setValue("meme")
                .addOnSuccessListener {
                    println("Мем успешно добавлен!")
                }
                .addOnFailureListener {
                    println("Ошибка при добавлении мема: ${it.message}")
                }
        }
    }
}