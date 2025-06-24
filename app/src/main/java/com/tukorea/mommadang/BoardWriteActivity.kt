package com.tukorea.mommadang

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tukorea.mommadang.databinding.ActivityBoardWriteBinding
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts

class BoardWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardWriteBinding
    private var selectedCategory: String = "자유 게시판" // 기본 카테고리

    // 사진
    private var selectedImageUri: Uri? = null

    // 사진 선택 결과 처리 런처 등록
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri

            // 선택한 이미지 Uri에서 Drawable 생성 후 editContent에 이미지 삽입 (수정된 부분)
            val inputStream = contentResolver.openInputStream(uri)
            val drawable = Drawable.createFromStream(inputStream, uri.toString())
            inputStream?.close()

            drawable?.let {
                // 이미지 크기 조절 (높이 200px 기준)
                val height = 600
                val ratio = it.intrinsicWidth.toFloat() / it.intrinsicHeight
                val width = (height * ratio).toInt()
                it.setBounds(0, 0, width, height)

                // ImageSpan 생성
                val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BASELINE)

                // SpannableStringBuilder에 이미지 추가
                val ssb = SpannableStringBuilder(binding.editContent.text)
                val cursorPos = binding.editContent.selectionStart
                ssb.insert(cursorPos, "￼") // 이미지 자리 표시 특수문자
                ssb.setSpan(imageSpan, cursorPos, cursorPos + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                // EditText에 반영
                binding.editContent.setText(ssb)
                binding.editContent.setSelection(cursorPos + 1)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카테고리 버튼 클릭 이벤트
        binding.btnFree.setOnClickListener {
            selectedCategory = "자유 게시판"
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

        binding.btnInfo.setOnClickListener {
            selectedCategory = "정보 게시판"
            updateCategorySelection(selectedCategory)
        }

        binding.btBack.setOnClickListener { //상단 뒤로가기 버튼
            finish()
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
                // 선택한 이미지 URI 전달 (문자열 형태)
                selectedImageUri?.let {
                    putExtra("imageUri", it.toString())
                }
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.boardWritePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*") // 사진 선택 (수정된 부분: 사진 선택 시 바로 EditText에 삽입)
        }

        // 기본 선택 상태 설정ㅡ
        updateCategorySelection(selectedCategory)
    }

    private fun updateCategorySelection(selected: String) {
        val selectedColor = ContextCompat.getColor(this, R.color.Apricot)
        val unselectedColor = ContextCompat.getColor(this, R.color.cardColor)
        val activeTextColor = ContextCompat.getColor(this, R.color.bt_click_textColor)
        val inactiveTextColor = ContextCompat.getColor(this, R.color.textColor1)

        val allButtons = listOf(binding.btnFree, binding.btnMarket, binding.btnProud, binding.btnInfo)

        allButtons.forEach { button ->
            val isSelected = when (button) {
                binding.btnFree -> selected == "자유 게시판"
                binding.btnMarket -> selected == "중고 거래"
                binding.btnProud -> selected == "자녀 자랑 게시판"
                binding.btnInfo -> selected == "정보 게시판"
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
        binding.categoryGuideText.text = when (selected) {
            "중고 거래" -> "🛍 판매하거나 구매할 물건을 적어주세요"
            "자녀 자랑 게시판" -> "🌟 아이의 멋진 순간을 자랑해주세요"
            "정보 게시판" -> "💡 유용한 정보를 나눠주세요"
            "자유 게시판" -> "☕ 아무 이야기나 편하게 적어보세요"
            else -> ""
        }

    }

}
