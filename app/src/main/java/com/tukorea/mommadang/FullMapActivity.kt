package com.tukorea.mommadang

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tukorea.mommadang.databinding.ActivityFullMapBinding
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.NaverMap
import com.naver.maps.map.CameraUpdate
import com.naver.maps.geometry.LatLng

class FullMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityFullMapBinding
    private var initLat = 37.5665    // 기본값
    private var initLng = 126.9780   // 기본값

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트로 받은 좌표 (필요 없으면 빼도 돼)
        initLat = intent.getDoubleExtra("lat", initLat)
        initLng = intent.getDoubleExtra("lng", initLng)

        // MapFragment 세팅
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.full_map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.full_map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        // 카메라 위치 이동
        val target = LatLng(initLat, initLng)
        naverMap.moveCamera(CameraUpdate.scrollTo(target))
        // 여기서 마커나 스타일 추가 가능
    }
}
