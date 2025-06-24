package com.tukorea.mommadang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tukorea.mommadang.databinding.FragmentMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment as NaverMapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback


class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var sdkMapFragment: NaverMapFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        val fm = childFragmentManager
        val containerId = binding.fullMapContainer.id
        sdkMapFragment = fm.findFragmentById(containerId) as? NaverMapFragment
            ?: NaverMapFragment.newInstance().also { fragment ->
                fm.beginTransaction()
                    .add(containerId, fragment)
                    .commitNow() // 동기 커밋으로 바로 추가
            }

        // 지도가 준비되면 onMapReady로 콜백
        sdkMapFragment?.getMapAsync(this)


        return binding.root
    }

    override fun onMapReady(naverMap: NaverMap) {
        // 지도 준비 후 동작: 서울로 카메라 이동
        val seoul = LatLng(37.5665, 126.9780)
        naverMap.moveCamera(CameraUpdate.scrollTo(seoul))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}