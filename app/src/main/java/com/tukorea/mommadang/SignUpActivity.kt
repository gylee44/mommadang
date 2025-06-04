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

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivitySignupBinding.inflate(layoutInflater)
            setContentView(binding.root)

            auth = FirebaseAuth.getInstance()

            binding.btnSignup.setOnClickListener {
                val id = binding.editSignupId.text.toString().trim()
                val pw = binding.editSignupPw.text.toString().trim()
                val pwConfirm = binding.editSignupPwConfirm.text.toString().trim()
                val name = binding.editSignupName.text.toString().trim()
                val phone = binding.editSignupPhone.text.toString().trim()
                val nickname = binding.editSignupNickname.text.toString().trim()
                val region = binding.editSignupRegion.text.toString().trim()

                if (id.isEmpty() || pw.isEmpty() || pwConfirm.isEmpty() || name.isEmpty() ||
                    phone.isEmpty() || nickname.isEmpty() || region.isEmpty()
                ) {
                    Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (pw != pwConfirm) {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val email = "$id@momma.com"

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
                                        Toast.makeText(this, "정보 저장 실패: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                        } else {
                            Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            binding.txtLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
