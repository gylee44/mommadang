package com.tukorea.mommadang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.geometry.LatLng
import com.tukorea.mommadang.BoardFragment
import com.tukorea.mommadang.FullMapActivity
import com.tukorea.mommadang.ProfileFragment
import com.tukorea.mommadang.R
import com.tukorea.mommadang.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // 초기 카메라 위치
    private val initLat = 37.5665
    private val initLng = 126.9780

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 게시판 카드 클릭
        binding.boardCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_main, BoardFragment())
                .addToBackStack(null)
                .commit()
        }

        // 프로필 카드 클릭
        binding.profileCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_main, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // ScrollView 터치 인터셉트 방지
        binding.mapMiniContainer.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        // 투명 오버레이 클릭 → 전체 지도 화면으로 전환
        binding.mapOverlay.setOnClickListener {
            startActivity(Intent(requireContext(), FullMapActivity::class.java).apply {
                putExtra("lat", initLat)
                putExtra("lng", initLng)
            })
        }
        // 미니맵 컨테이너 클릭 → 전체 지도 액티비티로 전환
        binding.mapMiniContainer.setOnClickListener {
            Log.d("HomeFragment", "mapMiniContainer clicked")
            Toast.makeText(requireContext(), "지도 전체보기", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), FullMapActivity::class.java).apply {
                putExtra("lat", initLat)
                putExtra("lng", initLng)
            })
        }

        // MapFragment 초기화 및 콜백 설정
        val mapFrag = childFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .add(R.id.map_fragment, it)
                    .commit()
            }
        mapFrag.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        // 맵 제스처 모두 비활성화
        naverMap.uiSettings.apply {
            isScrollGesturesEnabled = false
            isZoomGesturesEnabled   = false
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled   = false
        }
        // 초기 카메라 위치 설정
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(initLat, initLng)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



