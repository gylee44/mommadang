package com.tukorea.mommadang

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tukorea.mommadang.databinding.ActivityBoardWriteBinding

class BoardWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardWriteBinding
    private var selectedCategory: String = "자유게시판" // 기본 카테고리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카테고리 버튼 클릭 이벤트
        binding.btnFree.setOnClickListener {
            selectedCategory = "자유게시판"
            updateCategorySelection(selectedCategory)
        }

        binding.btnMarket.setOnClickListener {
            selectedCategory = "중고 거래"
            updateCategorySelection(selectedCategory)
        }

        binding.btnProud.setOnClickListener {
            selectedCategory = "자녀 자랑 게시판"
            updateCategorySelection(selectedCategory)
        }

        binding.btnLocal.setOnClickListener {
            selectedCategory = "지역별 게시판"
            updateCategorySelection(selectedCategory)
        }

        // 등록 버튼 클릭
        binding.btnSubmit.setOnClickListener {
            val title = binding.editTitle.text.toString().trim()
            val content = binding.editContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("title", title)
                putExtra("content", content)
                putExtra("category", selectedCategory)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.boardWritePhoto.setOnClickListener {
            // 사진 등록 추가
        }

        // 기본 선택 상태 설정ㅡ
        updateCategorySelection(selectedCategory)
    }

    private fun updateCategorySelection(selected: String) {
        val selectedColor = ContextCompat.getColor(this, R.color.Apricot)
        val unselectedColor = ContextCompat.getColor(this, R.color.black)
        val textColor = ContextCompat.getColor(this, R.color.white)

        val allButtons = listOf(binding.btnFree, binding.btnMarket, binding.btnProud, binding.btnLocal)

        allButtons.forEach { button ->
            val isSelected = when (button) {
                binding.btnFree -> selected == "자유게시판"
                binding.btnMarket -> selected == "중고 거래"
                binding.btnProud -> selected == "자녀 자랑 게시판"
                binding.btnLocal -> selected == "지역별 게시판"
                else -> false
            }

            button.backgroundTintList = ColorStateList.valueOf(
                if (isSelected) selectedColor else unselectedColor
            )
            button.setTextColor(textColor)
        }
    }

}
