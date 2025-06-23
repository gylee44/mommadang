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
    private val infoBoardFragment = InfoBoardFragment()

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
                    "정보 게시판" -> infoBoardFragment.addPost(title, content, author)
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

        binding.btnInfo.setOnClickListener {
            selectedCategory = "정보 게시판"
            updateCategorySelection()
            childFragmentManager.beginTransaction()
                .replace(R.id.board_content_container, infoBoardFragment)
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
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.cardColor)
        val activeTextColor = ContextCompat.getColor(requireContext(), R.color.bt_click_textColor)
        val inactiveTextColor = ContextCompat.getColor(requireContext(), R.color.textColor1)

        val allButtons = listOf(binding.btnFree, binding.btnMarket, binding.btnProud, binding.btnInfo)

        allButtons.forEach { button ->
            val isSelected = when (button) {
                binding.btnFree -> selectedCategory == "자유게시판"
                binding.btnMarket -> selectedCategory == "중고 거래"
                binding.btnProud -> selectedCategory == "자녀 자랑 게시판"
                binding.btnInfo -> selectedCategory == "정보 게시판"
                else -> false
            }

            button.backgroundTintList = ColorStateList.valueOf(
                if (isSelected) selectedColor else unselectedColor
            )
            button.setTextColor(
                if (isSelected) activeTextColor
                else inactiveTextColor
            )
            //버튼 fade 효과
            button.animate()
                .alpha(if (isSelected) 1f else 0.5f) // 선택된 버튼은 뚜렷하게, 나머지는 흐리게
                .setDuration(200)
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
