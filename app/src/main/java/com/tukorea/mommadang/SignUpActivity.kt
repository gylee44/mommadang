package com.tukorea.mommadang

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tukorea.mommadang.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //입력 칸을 채우지 않았을 때 회원가입 안되게 기능 추가 예정
        
        
        // 회원가입 버튼 클릭 시 로그인 화면으로 이동 (단, 회원가입이 완료 됐을 시)
        binding.btnSignup.setOnClickListener {
            // 실제 회원가입 로직 처리 후
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //클릭 시 로그인 화면으로 바로 이동
        binding.txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}