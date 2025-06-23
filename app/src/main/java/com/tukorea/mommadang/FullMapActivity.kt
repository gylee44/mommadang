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
import com.naver.maps.map.overlay.Marker        // 마커


class FullMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityFullMapBinding
    private var initLat = 37.3814    // 기본값 : 시흥시청
    private var initLng = 126.8059   // 기본값
    private val markers = mutableListOf<Marker>()

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
        // 카메라 초기 위치 세팅
        val initLat = intent.getDoubleExtra("lat", 37.5665)
        val initLng = intent.getDoubleExtra("lng", 126.9780)
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(initLat, initLng)))

        // 지도 터치할 때마다 마커 추가
        naverMap.setOnMapClickListener { _, latLng ->
            Marker().apply {
                position = latLng
                map = naverMap

                // **이 마커를 클릭하면 삭제** 되도록 리스너 등록
                setOnClickListener { overlay ->
                    // 지도에서 제거
                    this.map = null
                    // 리스트에서도 제거
                    markers.remove(this)
                    true  // 이벤트 소모(true를 리턴해야 마커 클릭이 끝)
                }
            }.also {
                markers.add(it)
            }
        }
    }
}


