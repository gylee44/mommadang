package com.tukorea.mommadang

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.tukorea.mommadang.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // "회원가입" 텍스트에 밑줄 효과
        binding.txtSignup.paintFlags = binding.txtSignup.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // 로그인 버튼 클릭 시
        binding.btnLogin.setOnClickListener {
            val id = binding.editId.text.toString().trim()
            val pw = binding.editPw.text.toString().trim()

            if (id.isEmpty() || pw.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = "$id@momma.com" // 회원가입 시 사용한 이메일 형식으로 변환

            auth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 사용자 이름을 Firebase에서 가져와 SharedPreferences에 저장
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val userRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(uid)

                        userRef.get().addOnSuccessListener { snapshot ->
                            val userName =
                                snapshot.child("name").getValue(String::class.java) ?: "알 수 없음"

                            // SharedPreferences 저장
                            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            prefs.edit().putString("user_name", userName).apply()

                            // 로그인 성공 → 메인 화면으로 이동
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, "사용자 정보 로드 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        // "회원가입" 클릭 시 회원가입 화면으로 이동
        binding.txtSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
