package com.tukorea.mommadang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tukorea.mommadang.databinding.FragmentFreeBoardBinding


class LocalBoardFragment : Fragment() {

    private var _binding: FragmentFreeBoardBinding? = null
    private val binding get() = _binding!!

    private val postList = mutableListOf<Pair<String, String>>()
    private lateinit var adapter: PostAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFreeBoardBinding.inflate(inflater, container, false)

        adapter = PostAdapter(postList)
        binding.recyclerViewFree.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFree.adapter = adapter

        // Firestore에서 게시글 불러오기
        loadPosts()

        return binding.root
    }

    fun addPost(title: String, content: String) {
        val post = hashMapOf(
            "title" to title,
            "content" to content,
            "category" to "지역별 게시판",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener {
                postList.add(0, title to content)
                adapter.notifyItemInserted(0)
                binding.recyclerViewFree.scrollToPosition(0)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "게시글 등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error adding post", it)
            }
    }

    // Firestore에서 게시글 불러오기
    private fun loadPosts() {
        db.collection("posts")
            .whereEqualTo("category", "지역별 게시판")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                postList.clear()
                for (document in result) {
                    val title = document.getString("title") ?: ""
                    val content = document.getString("content") ?: ""
                    postList.add(title to content)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "게시글 불러오기 실패", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error loading posts", it)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
