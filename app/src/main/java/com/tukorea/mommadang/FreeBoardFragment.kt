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

class FreeBoardFragment : Fragment() {

    private var _binding: FragmentFreeBoardBinding? = null
    private val binding get() = _binding!!

    private val fullPostList = mutableListOf<Post>()
    private val postList = mutableListOf<Post>() // title, content, author, timestamp
    private var adapter: PostAdapter? = null
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Firestore에 게시글 저장 및 목록에 추가
    fun addPost(title: String, content: String, author: String) {
        val timestamp = System.currentTimeMillis()
        val post = hashMapOf(
            "title" to title,
            "content" to content,
            "author" to author,
            "category" to "자유 게시판",
            "timestamp" to timestamp
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener {
                postList.add(0, Post(title, content, author, timestamp, "자유 게시판"))
                adapter?.notifyItemInserted(0)
                if (isAdded && view != null) {
                    binding.recyclerViewFree.scrollToPosition(0)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "게시글 등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error adding post", it)
            }
    }

    // Firestore에서 게시글 불러오기
    private fun loadPosts() {
        db.collection("posts")
            .whereEqualTo("category", "자유 게시판")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                postList.clear()
                fullPostList.clear()
                for (document in result) {
                    val title = document.getString("title") ?: ""
                    val content = document.getString("content") ?: ""
                    val author = document.getString("author") ?: "익명"
                    val timestamp = document.getLong("timestamp") ?: 0L
                    val category = document.getString("category") ?: "자유 게시판"
                    postList.add(Post(title, content, author, timestamp, category))
                    fullPostList.add(Post(title, content, author, timestamp, category))
                }
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "게시글 불러오기 실패", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Error loading posts", it)
            }
    }

    fun filterPosts(query: String) {
        postList.clear()
        if (query.isBlank()) {
            postList.addAll(fullPostList)
        } else {
            postList.addAll(
                fullPostList.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.content.contains(query, ignoreCase = true)
                }
            )
        }
        adapter?.notifyDataSetChanged()
    }
}