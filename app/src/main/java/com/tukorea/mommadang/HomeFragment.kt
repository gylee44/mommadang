package com.tukorea.mommadang

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.tukorea.mommadang.databinding.FragmentHomeBinding
import jp.wasabeef.glide.transformations.CropTransformation
// 날씨
import androidx.lifecycle.lifecycleScope
import com.tukorea.mommadang.api.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import com.tukorea.mommadang.utils.DateUtils.getBaseDateTime






class HomeFragment : Fragment(), OnMapReadyCallback {

    // 날씨 주기적으로 받아오는 함수
    private fun getBaseDateTime(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val baseTimes = listOf(2, 5, 8, 11, 14, 17, 20, 23)
        var baseTimeHour = baseTimes.lastOrNull { it <= currentHour } ?: 23

        if (currentHour == 0 || (currentHour == 2 && currentMinute < 30)) {
            calendar.add(Calendar.DATE, -1)
            baseTimeHour = 23
        }

        val baseDate = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(calendar.time)
        val baseTime = String.format("%02d00", baseTimeHour)

        return Pair(baseDate, baseTime)
    }



    private lateinit var bannerItems: List<BannerItem>
    private lateinit var adapter: BannerAdapter
    private var currentPage = 0

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val initLat = 37.3814
    private val initLng = 126.8059

    private val slideHandler = Handler(Looper.getMainLooper())
    private val slideRunnable = object : Runnable {
        override fun run() {
            if (bannerItems.isNotEmpty()) {
                currentPage++
                binding.viewPager.setCurrentItem(currentPage, true)
                slideHandler.postDelayed(this, 3000)
            }
        }
    }

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


        // 날씨 api 호출
        lifecycleScope.launch {
            try {
                val (baseDate, baseTime) = getBaseDateTime()
                Log.d("Weather", "요청 baseDate: $baseDate, baseTime: $baseTime")
                val nx = 56  // 시흥시 격자좌표
                val ny = 124


                val response = RetrofitClient.instance.getWeather(
                    serviceKey = "gp1oeAt31dIFh6jUiegvQxvkno3y4ImmNCcdqOIHVFIWDFgfIK5z8HZTeaR0J3ly%2BKseGmaFBBXJlCl9Hw%2F5Fg%3D%3D",
                    pageNo = 1,
                    numOfRows = 1000,
                    dataType = "JSON",
                    baseDate = baseDate,
                    baseTime = baseTime,
                    nx = nx,
                    ny = ny
                )   // 56, 124

                if (response.isSuccessful) {
                    val items = response.body()?.response?.body?.items?.item
                    val now = Calendar.getInstance()
                    val nowHour = now.get(Calendar.HOUR_OF_DAY)
                    val forecastHour = (nowHour / 3) * 3
                    val fcstTime = String.format("%02d00", forecastHour)



                    val tmp = items?.firstOrNull { it.category == "TMP" && it.fcstTime == fcstTime }?.fcstValue
                    val sky = items?.firstOrNull { it.category == "SKY" && it.fcstTime == fcstTime }?.fcstValue
                    val pty = items?.firstOrNull { it.category == "PTY" && it.fcstTime == fcstTime }?.fcstValue


                    // 날짜/요일 출력
                    binding.todayInfo.text = "${getDayOfWeek()} · ${getTodayDateFormatted()}"

                    // 기온 출력
                    binding.todayTemp.text = "${tmp ?: "--"}℃"

                    // 날씨 이미지 출력
                    val iconRes = when {
                        pty == "1" || pty == "2" || pty == "4" -> R.drawable.ic_weather_rain
                        sky == "1" -> R.drawable.ic_weather_sun
                        else -> R.drawable.ic_weather_cloud
                    }
                    binding.weatherIcon.setImageResource(iconRes)

                } else {
                    Log.e("Weather", "API 실패: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("Weather", "예외 발생: ${e.localizedMessage}")
            }
        }

        /*val iconRes = when {
                        pty == "1" || pty == "2" || pty == "4" -> R.drawable.ic_weather_rain
                        sky == "1" -> R.drawable.ic_weather_sun
                        sky == "3" -> R.drawable.ic_weather_cloud
                        else -> R.drawable.ic_weather_sun
                    }*/


        // 배너 아이템 세팅
        val realBannerItems = listOf(
            BannerItem(
                "https://kfescdn.visitkorea.or.kr/kfes/upload/contents/db/7e08a4f9-ce96-4ac5-9c04-c2b59bdfc24e_3.png",
                "https://korean.visitkorea.or.kr/kfes/detail/fstvlDetail.do?fstvlCntntsId=7e08a4f9-ce96-4ac5-9c04-c2b59bdfc24e",
                CropTransformation.CropType.TOP
            ),
            BannerItem(
                "https://kfescdn.visitkorea.or.kr/kfes/upload/contents/db/646ab327-6614-457f-b147-b77abf72e257_3.jpg",
                "https://korean.visitkorea.or.kr/kfes/detail/fstvlDetail.do?cmsCntntsId=2828336",
                CropTransformation.CropType.CENTER
            )
        )

        bannerItems = listOf(realBannerItems.last()) + realBannerItems + listOf(realBannerItems.first())
        adapter = BannerAdapter(bannerItems) { url ->
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
            startActivity(intent)
        }

        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(1, false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    if (currentPage == 0) {
                        binding.viewPager.setCurrentItem(bannerItems.size - 2, false)
                    } else if (currentPage == bannerItems.size - 1) {
                        binding.viewPager.setCurrentItem(1, false)
                    }
                }
            }
        })

        // 카드뷰 클릭 이벤트
        binding.boardCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_main, BoardFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.profileCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_main, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // Drawer 메뉴
        binding.btMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> { }
                R.id.nav_board -> {
                    Toast.makeText(requireContext(), "게시판으로 이동", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.container_main, BoardFragment())
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_profile -> {
                    Toast.makeText(requireContext(), "프로필 창으로 이동", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.container_main, ProfileFragment())
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_map -> {
                    Toast.makeText(requireContext(), "지도 화면으로 이동", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), FullMapActivity::class.java).apply {
                        putExtra("lat", initLat)
                        putExtra("lng", initLng)
                    })
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // 미니맵 처리
        binding.mapMiniContainer.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        binding.mapOverlay.setOnClickListener {
            startActivity(Intent(requireContext(), FullMapActivity::class.java).apply {
                putExtra("lat", initLat)
                putExtra("lng", initLng)
            })
        }

        binding.mapMiniContainer.setOnClickListener {
            Log.d("HomeFragment", "mapMiniContainer clicked")
            Toast.makeText(requireContext(), "지도 전체보기", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), FullMapActivity::class.java).apply {
                putExtra("lat", initLat)
                putExtra("lng", initLng)
            })
        }

        val mapFrag = childFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .add(R.id.map_fragment, it)
                    .commit()
            }

        mapFrag.getMapAsync(this)
    }

    // 날짜 반환 함수
    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
        return sdf.format(Date())
    }

    private fun getTodayDateFormatted(): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        return sdf.format(Date())
    }

    private fun getDayOfWeek(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.ENGLISH)
        return sdf.format(Date()).uppercase() // 예: TUESDAY
    }



    override fun onMapReady(naverMap: NaverMap) {
        naverMap.uiSettings.apply {
            isScrollGesturesEnabled = false
            isZoomGesturesEnabled = false
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
        }
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(initLat, initLng)))

        val marker = Marker()
        marker.position = LatLng(initLat, initLng)
        marker.map = naverMap
    }

    override fun onResume() {
        super.onResume()
        slideHandler.post(slideRunnable)
    }

    override fun onPause() {
        super.onPause()
        slideHandler.removeCallbacks(slideRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
