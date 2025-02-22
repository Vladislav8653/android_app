package com.arefin.memeapp

import MemeAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity(), OnMemeClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manageAuthorization()

    }

    private fun manageAuthorization() {
        setContentView(R.layout.activity_auth)
        auth = FirebaseAuth.getInstance()
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val enterButton = findViewById<Button>(R.id.enter)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUser(email, password)
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            registerUser(email, password)
        }

        enterButton.setOnClickListener {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
            showDefaultMenu()
        }
    }

    private fun showDefaultMenu() {
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("favorites")

        showFragment(MemesFragment()) // Показываем список мемов по умолчанию

        findViewById<Button>(R.id.btn_memes).setOnClickListener {
            showFragment(MemesFragment())
        }

        findViewById<Button>(R.id.btn_favorites).setOnClickListener {
            showFragment(FavoritesFragment())
        }

        findViewById<Button>(R.id.btn_profile).setOnClickListener {
            //showFragment(ProfileFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun addToFavorites(meme: Meme) {
        val memeId = databaseReference.push().key // Генерируем уникальный ID
        memeId?.let {
            databaseReference.child(it).setValue(meme).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "${meme.title} добавлен в избранное", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Ошибка при добавлении в избранное", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showMainList() {
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        //recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
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
                val adapter = MemeAdapter(memeList, this@MainActivity) // Передаем текущую активность как слушатель
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })
    }

    override fun onMemeClick(meme: Meme) {
        val bottomSheet = DetailBottomSheetDialog(meme)
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }
}