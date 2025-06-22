package com.tukorea.mommadang

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tukorea.mommadang.databinding.FragmentBoardBinding

class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private val freeBoardFragment = FreeBoardFragment()
    private val marketBoardFragment = MarketBoardFragment()
    private val proudBoardFragment = ProudBoardFragment()
    private val localBoardFragment = LocalBoardFragment()

    private var selectedCategory: String = "자유게시판"

    private val writeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra("title")
            val content = data?.getStringExtra("content")
            val category = data?.getStringExtra("category")

            val prefs = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
            val author = prefs.getString("user_name", "알 수 없음") ?: "알 수 없음"

            if (title != null && content != null && category != null) {
                when (category) {
                    "자유게시판" -> freeBoardFragment.addPost(title, content, author)
                    "중고 거래" -> marketBoardFragment.addPost(title, content, author)
                    "자녀 자랑 게시판" -> proudBoardFragment.addPost(title, content, author)
                    "지역별 게시판" -> localBoardFragment.addPost(title, content, author)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)

        // 글쓰기 버튼
        binding.btnWrite.setOnClickListener {
            val intent = Intent(requireContext(), BoardWriteActivity::class.java)
            writeLauncher.launch(intent)
        }

        // 카테고리 버튼 클릭 시
        binding.btnFree.setOnClickListener {
            selectedCategory = "자유게시판"
            updateCategorySelection()
            childFragmentManager.beginTransaction()
                .replace(R.id.board_content_container, freeBoardFragment)
                .commit()
        }

        binding.btnMarket.setOnClickListener {
            selectedCategory = "중고 거래"
            updateCategorySelection()
            childFragmentManager.beginTransaction()
                .replace(R.id.board_content_container, marketBoardFragment)
                .commit()
        }

        binding.btnProud.setOnClickListener {
            selectedCategory = "자녀 자랑 게시판"
            updateCategorySelection()
            childFragmentManager.beginTransaction()
                .replace(R.id.board_content_container, proudBoardFragment)
                .commit()
        }

        binding.btnLocal.setOnClickListener {
            selectedCategory = "지역별 게시판"
            updateCategorySelection()
            childFragmentManager.beginTransaction()
                .replace(R.id.board_content_container, localBoardFragment)
                .commit()
        }

        // 초기 진입 시 기본 게시판
        if (savedInstanceState == null) {
            updateCategorySelection()
            childFragmentManager.beginTransaction()
                .replace(R.id.board_content_container, freeBoardFragment)
                .commit()
        }

        return binding.root
    }

    private fun updateCategorySelection() {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.Apricot)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.black)
        val textColor = ContextCompat.getColor(requireContext(), R.color.white)

        val allButtons = listOf(binding.btnFree, binding.btnMarket, binding.btnProud, binding.btnLocal)

        allButtons.forEach { button ->
            val isSelected = when (button) {
                binding.btnFree -> selectedCategory == "자유게시판"
                binding.btnMarket -> selectedCategory == "중고 거래"
                binding.btnProud -> selectedCategory == "자녀 자랑 게시판"
                binding.btnLocal -> selectedCategory == "지역별 게시판"
                else -> false
            }

            button.backgroundTintList = ColorStateList.valueOf(
                if (isSelected) selectedColor else unselectedColor
            )
            button.setTextColor(textColor)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
