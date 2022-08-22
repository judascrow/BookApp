package com.judascrow.bookapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.judascrow.bookapp.MyApplication
import com.judascrow.bookapp.R
import com.judascrow.bookapp.adapters.AdapterPdfFavorite
import com.judascrow.bookapp.databinding.ActivityProfileBinding
import com.judascrow.bookapp.models.ModelPdf

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var booksArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfFavorite: AdapterPdfFavorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        loadUserinfo()

        loadFavoriteBooks()

        // handle click, go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        // handle click, open edit profile
        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }
    }

    private fun loadUserinfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    // get user info
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"

                    // convert timestamp
                    val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                    // set data
                    binding.nameTv.text = name
                    binding.emailTv.text = email
                    binding.memberDateTv.text = formattedDate
                    binding.accountTypeTv.text = userType

                    // set image
                    try {
                        Glide.with(this@ProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.ic_person_gray)
                            .into(binding.profileIv)
                    }
                    catch (e: Exception) {

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


    private fun loadFavoriteBooks(){
        booksArrayList = ArrayList();

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    booksArrayList.clear()

                    for (ds in snapshot.children) {
                        val bookId = "${ds.child("bookId").value}"

                        val modelPdf = ModelPdf()
                        modelPdf.id = bookId

                        booksArrayList.add(modelPdf)
                    }

                    binding.favoriteBookCountTv.text = "${booksArrayList.size}"

                    adapterPdfFavorite = AdapterPdfFavorite(this@ProfileActivity, booksArrayList)

                    binding.favoriteRv.adapter = adapterPdfFavorite
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}






































