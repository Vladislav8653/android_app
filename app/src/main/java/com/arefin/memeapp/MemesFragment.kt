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

class MemesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadMemes()
    }

    private fun loadMemes() {
        val databaseUrl = "https://memeapp-147f1-default-rtdb.europe-west1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(databaseUrl)
        databaseReference = database.getReference("memes")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val memeList = mutableListOf<Meme>()
                for (snapshot in dataSnapshot.children) {
                    val meme = snapshot.getValue(Meme::class.java)
                    meme?.let { memeList.add(it) }
                }
                val adapter = MemeAdapter(memeList, requireActivity() as MainActivity)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        })
    }

}