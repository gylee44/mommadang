package com.tukorea.mommadang

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tukorea.mommadang.databinding.ActivityBoardWriteBinding

class BoardWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val title = binding.editTitle.text.toString()
            val content = binding.editContent.text.toString()

            // 등록 처리 (Firebase 연동 전이므로 임시 Toast 처리)
            Toast.makeText(this, "제목: $title\n내용: $content", Toast.LENGTH_SHORT).show()

            finish() // 작성 완료 후 종료
        }
    }
}