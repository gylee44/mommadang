package com.tukorea.mommadang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tukorea.mommadang.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // ▼ ▼ ▼ 클릭 이벤트 정의 ▼ ▼ ▼

        // 지역 행사 화살표 클릭
        binding.icArrowRight.setOnClickListener {
            Toast.makeText(requireContext(), "지역 행사 더보기 클릭됨", Toast.LENGTH_SHORT).show()
        }

        // 게시판 카드 클릭 → BoardFragment로 전환
        binding.boardCard.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, BoardFragment())
                .addToBackStack(null)
                .commit()
        }

        // 내 정보 카드 클릭 → ProfileFragment로 전환
        binding.profileCard.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}