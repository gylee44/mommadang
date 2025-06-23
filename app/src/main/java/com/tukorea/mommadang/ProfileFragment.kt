package com.tukorea.mommadang

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tukorea.mommadang.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 이미지 선택 결과 처리
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri: Uri? = data?.data
                if (imageUri != null) {
                    // 1. 이미지 선택 후 바로 미리보기 표시
                    Glide.with(this).load(imageUri).into(binding.imageProfile)

                    // 2. 업로드
                    uploadProfileImage(imageUri)
                } else {
                    Toast.makeText(context, "이미지를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        loadUserProfile()

        binding.imageProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }
        // 좌상단 헤더 리스트
        binding.btMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(requireContext(), "홈으로 이동", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main, HomeFragment())
                        .commit()
                }
                R.id.nav_board -> {
                    Toast.makeText(requireContext(), "게시판으로 이동", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.container_main, BoardFragment())
                            .commit()
                }
                R.id.nav_profile -> {
//                    Toast.makeText(requireContext(), "프로필 창으로 이동", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_map -> {
                    Toast.makeText(requireContext(), "지도 화면으로 이동", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container_main, MapFragment())
                        .commit()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        return binding.root
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return

        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val nickname = snapshot.child("nickname").getValue(String::class.java) ?: ""
                val phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                val region = snapshot.child("region").getValue(String::class.java) ?: ""
                val imageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

                binding.profileMainNickname.text = nickname
                binding.profileName.text = "이름: $name"
                binding.profileNickname.text = "닉네임: $nickname"
                binding.profilePhone.text = "휴대전화번호: $phone"
                binding.profileLive.text = "지역: $region"

                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this).load(imageUrl).into(binding.imageProfile)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "데이터 로드 실패: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadProfileImage(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(uid)
                        .child("profileImageUrl")
                        .setValue(downloadUri.toString())

                    Glide.with(this).load(downloadUri).into(binding.imageProfile)
                    Toast.makeText(context, "프로필 사진이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "이미지 업로드 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
