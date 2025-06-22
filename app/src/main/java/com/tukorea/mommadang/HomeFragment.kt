package com.tukorea.mommadang

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tukorea.mommadang.databinding.FragmentHomeBinding
import android.content.Intent
import android.net.Uri
import androidx.viewpager2.widget.ViewPager2
import jp.wasabeef.glide.transformations.CropTransformation

class HomeFragment : Fragment() {

    private lateinit var bannerItems: List<BannerItem>  // 배너 아이템 목록
    private lateinit var adapter: BannerAdapter         // 배너 어댑터
    private var currentPage = 0                         // 현재 배너 페이지 인덱스

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // 자동 슬라이드 핸들러 및 루프
    private val slideHandler = Handler(Looper.getMainLooper())
    private val slideRunnable = object : Runnable {
        override fun run() {
            if (bannerItems.isNotEmpty()) {
                currentPage++
                binding.viewPager.setCurrentItem(currentPage, true)
                slideHandler.postDelayed(this, 3000)  // 3초 간격으로 슬라이드
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 실제 사용할 배너 목록 정의
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

        // 무한 슬라이드를 위해 앞뒤에 아이템 복제
        bannerItems = listOf(realBannerItems.last()) + realBannerItems + listOf(realBannerItems.first())

        // 어댑터 생성: 배너 클릭 시 웹 페이지 열기
        val adapter = BannerAdapter(bannerItems) { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        binding.viewPager.adapter = adapter

        // 무한 슬라이드를 위한 페이지 이동 처리
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    // 양 끝일 경우 복제 아이템에서 실제 아이템으로 순간이동
                    if (currentPage == 0) {
                        binding.viewPager.setCurrentItem(bannerItems.size - 2, false)
                    } else if (currentPage == bannerItems.size - 1) {
                        binding.viewPager.setCurrentItem(1, false)
                    }
                }
            }
        })

        // ▼ ▼ ▼ 카드 뷰 클릭 이벤트 ▼ ▼ ▼

        // 게시판 카드 클릭 → BoardFragment로 이동
        binding.boardCard.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, BoardFragment())
                .addToBackStack(null)
                .commit()
        }

        // 미니맵 카드 클릭 → MapFragment로 이동 (현재 주석처리됨)
//        binding.miniMapView.setOnClickListener {
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.container_main, MapFragment())
//                .addToBackStack(null)
//                .commit()
//        }

        // 내 정보 카드 클릭 → ProfileFragment로 이동
        binding.profileCard.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // 시작 페이지를 실제 첫 번째 배너로 설정 (복제 아닌 진짜)
        binding.viewPager.setCurrentItem(1, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 프래그먼트 재개 시 자동 슬라이드 시작
    override fun onResume() {
        super.onResume()
        slideHandler.post(slideRunnable)
    }

    // 프래그먼트 정지 시 자동 슬라이드 중지
    override fun onPause() {
        super.onPause()
        slideHandler.removeCallbacks(slideRunnable)
    }
}
