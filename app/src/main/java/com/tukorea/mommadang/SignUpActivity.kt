package com.tukorea.mommadang

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tukorea.mommadang.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    private var isIdChecked = false
    private var lastCheckedId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 아이디 중복 확인 버튼 클릭 리스너
        binding.editTextIdConfirm.setOnClickListener {
            val id = binding.editSignupId.text.toString().trim()

            if (id.isEmpty()) {
                Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usersRef = FirebaseDatabase.getInstance().getReference("Users")
            usersRef.orderByChild("id").equalTo(id)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        isIdChecked = false
                        Toast.makeText(this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        isIdChecked = true
                        lastCheckedId = id
                        Toast.makeText(this, "사용할 수 있는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "중복 확인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // 회원가입 버튼 클릭 리스너
        binding.btnSignup.setOnClickListener {
            val id = binding.editSignupId.text.toString().trim()
            val pw = binding.editSignupPw.text.toString().trim()
            val pwConfirm = binding.editSignupPwConfirm.text.toString().trim()
            val name = binding.editSignupName.text.toString().trim()
            val phone = binding.editSignupPhone.text.toString().trim()
            val nickname = binding.editSignupNickname.text.toString().trim()
            val region = binding.editSignupRegion.text.toString().trim()

            // 아이디 중복 확인 여부 체크
            if (!isIdChecked || lastCheckedId != id) {
                Toast.makeText(this, "아이디 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 필수 입력값 확인
            if (id.isEmpty() || pw.isEmpty() || pwConfirm.isEmpty() || name.isEmpty()
                || phone.isEmpty() || nickname.isEmpty() || region.isEmpty()
            ) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 비밀번호 일치 확인
            if (pw != pwConfirm) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = "$id@momma.com"

            // Firebase Authentication에 사용자 생성
            auth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                        val userMap = mapOf(
                            "id" to id,
                            "name" to name,
                            "phone" to phone,
                            "nickname" to nickname,
                            "region" to region
                        )

                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(uid)
                            .setValue(userMap)
                            .addOnCompleteListener(this) { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "정보 저장 실패: ${dbTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    } else {
                        Toast.makeText(
                            this,
                            "회원가입 실패: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // 로그인 화면 이동
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
