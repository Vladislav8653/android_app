package com.arefin.memeapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserProfileFragment : Fragment() {

    private lateinit var editName: EditText
    private lateinit var editBirthdate: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var editAddress: EditText
    private lateinit var editBio: EditText
    private lateinit var editGender: EditText
    private lateinit var editOccupation: EditText
    private lateinit var editHobbies: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonLogout: Button

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_user_profile, container, false)

        editName = view.findViewById(R.id.edit_name)
        editBirthdate = view.findViewById(R.id.edit_birthdate)
        editEmail = view.findViewById(R.id.edit_email)
        editPhone = view.findViewById(R.id.edit_phone)
        editAddress = view.findViewById(R.id.edit_address)
        editBio = view.findViewById(R.id.edit_bio)
        editGender = view.findViewById(R.id.edit_gender)
        editOccupation = view.findViewById(R.id.edit_occupation)
        editHobbies = view.findViewById(R.id.edit_hobbies)
        buttonSave = view.findViewById(R.id.button_save)
        buttonLogout = view.findViewById(R.id.button_logout)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        buttonSave.setOnClickListener { saveUserProfile() }

        buttonLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, MainActivity::class.java))
            requireActivity().finish()
        }

        return view
    }

    private fun saveUserProfile() {
        val name = editName.text.toString()
        val birthdate = editBirthdate.text.toString()
        val email = editEmail.text.toString()
        val phone = editPhone.text.toString()
        val address = editAddress.text.toString()
        val bio = editBio.text.toString()
        val gender = editGender.text.toString()
        val occupation = editOccupation.text.toString()
        val hobbies = editHobbies.text.toString()

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userProfile = UserProfile(name, birthdate, email, phone, address, bio, gender, occupation, hobbies)
            databaseReference.child(userId).setValue(userProfile).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Профиль сохранён", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Ошибка сохранения профиля", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}