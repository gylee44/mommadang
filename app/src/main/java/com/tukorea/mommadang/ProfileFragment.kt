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
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
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
