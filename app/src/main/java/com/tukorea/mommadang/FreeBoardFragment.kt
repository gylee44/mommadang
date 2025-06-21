package com.tukorea.mommadang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tukorea.mommadang.databinding.FragmentFreeBoardBinding

class FreeBoardFragment : Fragment() {

    private var _binding: FragmentFreeBoardBinding? = null
    private val binding get() = _binding!!

    private val postList = mutableListOf<Triple<String, String, String>>() // title, content, author
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFreeBoardBinding.inflate(inflater, container, false)

        adapter = PostAdapter(postList)
        binding.recyclerViewFree.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFree.adapter = adapter

        return binding.root
    }

    fun addPost(title: String, content: String, author: String) {
        postList.add(0, Triple(title, content, author))
        adapter.notifyItemInserted(0)
        binding.recyclerViewFree.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
