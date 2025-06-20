package com.tukorea.mommadang

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.tukorea.mommadang.databinding.ActivitySignupBinding
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.ArrayAdapter

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
        //스페이스바 안쳐지게하는 함수 (아이디)
        val noSpaceFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.contains(" ")) {
                ""  // 공백 입력 무시
            } else {
                null  // 원래 입력 허용
            }
        }

        //입력 글자수 길이제한
        val maxLengthId = InputFilter.LengthFilter(12)
        val maxLengthName = InputFilter.LengthFilter(5)

        binding.editSignupId.filters = arrayOf(noSpaceFilter, maxLengthId)
        binding.editSignupName.filters = arrayOf(noSpaceFilter,maxLengthName)

        //전화번호 '-' 자동 삽입, '010-'부터시작
        binding.editSignupPhone.setText("010-")
        binding.editSignupPhone.setSelection(binding.editSignupPhone.text!!.length) // 커서를 맨 뒤로 이동

        binding.editSignupPhone.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing || s == null) return
                isEditing = true

                // "010-"은 고정 유지
                if (!s.startsWith("010-")) {
                    binding.editSignupPhone.setText("010-")
                    binding.editSignupPhone.setSelection(binding.editSignupPhone.text!!.length)
                    isEditing = false
                    return
                }

                val digits = s.toString().replace("-", "")
                val tail = digits.removePrefix("010")
                val formatted = when {
                    tail.length <= 4 -> "010-${tail}"
                    else -> "010-${tail.substring(0, 4)}-${
                        tail.substring(
                            4,
                            minOf(tail.length, 8)
                        )
                    }"
                }
                if (s.toString() != formatted) {
                    binding.editSignupPhone.setText(formatted)
                    binding.editSignupPhone.setSelection(formatted.length)
                }
                isEditing = false
            }
        })

        //시흥시 지역 스크롤바
        val regionList = resources.getStringArray(R.array.sihung_legal_dongs)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, regionList)
        binding.editSignupRegion.setAdapter(adapter)


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

        // 아이디 중복 확인하고 아이디 변경하였을 때 다시 확인
        binding.editSignupId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isIdChecked = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

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
