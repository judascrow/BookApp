package com.judascrow.bookapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.*
import com.judascrow.bookapp.adapters.AdapterPdfUser
import com.judascrow.bookapp.databinding.FragmentBooksUserBinding
import com.judascrow.bookapp.models.ModelPdf

class BooksUserFragment : Fragment {

    private lateinit var binding: FragmentBooksUserBinding

    public companion object{
        private const val TAG = "BOOKS_USER_TAG"

        public fun newInstance(categoryId: String, category: String, uid: String) : BooksUserFragment {
            val fragment = BooksUserFragment()

            val args = Bundle()
            args.putString("categoryId", categoryId)
            args.putString("category", category)
            args.putString("uid", uid)
            fragment.arguments = args
            return  fragment
        }
    }

    private var categoryId = ""
    private var category = ""
    private var uid = ""

    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {
            categoryId = args.getString("categoryId")!!
            category = args.getString("category")!!
            uid = args.getString("uid")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentBooksUserBinding.inflate(LayoutInflater.from(context), container, false)

        Log.d(TAG, "onCreateView: Category: $category")
        if (category == "All") {
            // load all books
            loadAllBooks()
        }
        else if (category == "Most Viewed") {
            // load most viewed books
            loadMostViewedDownloadedBooks("viewsCount")
        }
        else if (category == "Most Downloaded") {
            // load most downloaded books
            loadMostViewedDownloadedBooks("downloadsCount")
        }
        else {
            // load selected category books
            loadCategorizedBooks()
        }

        // search
        binding.searchEt.addTextChangedListener { object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
               try {
                   adapterPdfUser.filter.filter(s)
               }
               catch (e: Exception) {
                   Log.d(TAG, "onTextChanged: SEARCH EXCEPTION: ${e.message}")
               }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        } }

        return binding.root
    }

    private fun loadAllBooks() {
        // init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelPdf::class.java)

                    pdfArrayList.add(model!!)
                }

                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)

                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loadMostViewedDownloadedBooks(orderBy: String) {
        // init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild(orderBy).limitToLast(10)
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelPdf::class.java)

                    pdfArrayList.add(model!!)
                }

                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)

                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loadCategorizedBooks() {
        // init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        val model = ds.getValue(ModelPdf::class.java)

                        pdfArrayList.add(model!!)
                    }

                    adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)

                    binding.booksRv.adapter = adapterPdfUser
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

}













































