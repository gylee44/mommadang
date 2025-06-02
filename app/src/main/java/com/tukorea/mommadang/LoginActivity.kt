package com.tukorea.mommadang

import android.content.Intent
import android.graphics.Paint
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tukorea.mommadang.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //회원가입 밑줄
        binding.txtSignup.paintFlags = binding.txtSignup.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        // 로그인 버튼 클릭 시 MainActivity 이동
        binding.btnLogin.setOnClickListener {
            val id = binding.editId.text.toString()
            val pw = binding.editPw.text.toString()

            // 추가할 것!!    ex.if(id == " "&& pw =="")   아이디 비번 맞는지 (데이터베이스 연동해서 확인)

            if (id.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //성공 시
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 회원가입 텍스트 클릭 시 SignupActivity 이동
        binding.txtSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}