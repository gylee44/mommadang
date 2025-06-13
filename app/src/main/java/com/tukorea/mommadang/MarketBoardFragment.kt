package com.tukorea.mommadang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tukorea.mommadang.databinding.FragmentMarketBoardBinding

class MarketBoardFragment : Fragment() {

    private var _binding: FragmentMarketBoardBinding? = null
    private val binding get() = _binding!!

    private val postList = mutableListOf<Pair<String, String>>()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketBoardBinding.inflate(inflater, container, false)

        adapter = PostAdapter(postList)
        binding.recyclerViewMarket.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMarket.adapter = adapter

        return binding.root
    }

    fun addPost(title: String, content: String) {
        postList.add(0, title to content)
        adapter.notifyItemInserted(0)
        binding.recyclerViewMarket.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
