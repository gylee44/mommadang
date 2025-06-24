package com.tukorea.mommadang

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class PostDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val title = intent.getStringExtra("title") ?: ""
        val content = intent.getStringExtra("content") ?: ""
        val author = intent.getStringExtra("author") ?: "알 수 없음"
        val category = intent.getStringExtra("category") ?: "게시글"

        // 오늘 날짜 (yyyy.MM.dd)
        val currentDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())

        val categoryText = when (category) {
            "자유 게시판" -> "자유 게시판 ☕"
            "중고 거래" -> "중고 거래 🛍"
            "자녀 자랑 게시판" -> "자녀 자랑 🌟"
            "정보 게시판" -> "정보 게시판 💡"
            else -> category
        }

        // 바인딩
        findViewById<TextView>(R.id.postTitle).text = title
        findViewById<TextView>(R.id.postContent).text = content
        findViewById<TextView>(R.id.postMeta).text = "$author | $currentDate"
        findViewById<TextView>(R.id.board_text).text = categoryText
        findViewById<ImageView>(R.id.bt_back1).setOnClickListener{finish()}
    }
}
