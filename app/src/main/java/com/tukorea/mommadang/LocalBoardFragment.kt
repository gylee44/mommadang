package com.tukorea.mommadang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tukorea.mommadang.databinding.FragmentLocalBoardBinding

class LocalBoardFragment : Fragment() {

    private var _binding: FragmentLocalBoardBinding? = null
    private val binding get() = _binding!!

    private val postList = mutableListOf<Triple<String, String, String>>()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocalBoardBinding.inflate(inflater, container, false)

        adapter = PostAdapter(postList)
        binding.recyclerViewLocal.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewLocal.adapter = adapter

        return binding.root
    }

    fun addPost(title: String, content: String, author: String) {
        postList.add(0, Triple(title, content, author))
        adapter.notifyItemInserted(0)
        binding.recyclerViewLocal.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
