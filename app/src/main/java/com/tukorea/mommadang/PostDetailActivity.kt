package com.tukorea.mommadang

import android.os.Bundle
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

        // 오늘 날짜 (yyyy.MM.dd)
        val currentDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())

        // 바인딩
        findViewById<TextView>(R.id.postTitle).text = title
        findViewById<TextView>(R.id.postContent).text = content
        findViewById<TextView>(R.id.postMeta).text = "$author | $currentDate"
    }
}
