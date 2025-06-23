package com.tukorea.mommadang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tukorea.mommadang.databinding.FragmentHomeBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment as NaverMapFragment
import com.tukorea.mommadang.MapFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // ▼ ▼ ▼ 클릭 이벤트 정의 ▼ ▼ ▼

        // 게시판 카드 클릭 → BoardFragment로 전환
        binding.boardCard.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, BoardFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.mapFragmentContainer.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, MapFragment())
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

        // ── MapFragment 동적 삽입 ──
        val containerId = binding.mapFragmentContainer.id
        val fm = childFragmentManager
        var mapFrag = fm.findFragmentById(containerId) as? com.naver.maps.map.MapFragment

        if (mapFrag == null) {
            mapFrag = com.naver.maps.map.MapFragment.newInstance()
            fm.beginTransaction()
                .add(containerId, mapFrag)
                .commit()
        }

        mapFrag.getMapAsync { naverMap ->
            val seoul = com.naver.maps.geometry.LatLng(37.5665, 126.9780)
            naverMap.moveCamera(com.naver.maps.map.CameraUpdate.scrollTo(seoul))
        }


        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}