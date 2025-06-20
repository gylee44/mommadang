package com.tukorea.mommadang

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tukorea.mommadang.BannerItem
import com.tukorea.mommadang.databinding.ItemBannerBinding
import jp.wasabeef.glide.transformations.CropTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.MultiTransformation

// 배너 이미지 리스트를 ViewPager2로 표시할 때 사용하는 어댑터
class BannerAdapter(
    private val items: List<BannerItem>,         // 배너 데이터 목록
    private val onClick: (String) -> Unit        // 클릭 시 URL 클릭 시 호출되는 콜백 (링크 열기용)
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    // ViewHolder: 개별 배너 아이템 레이아웃(item_banner.xml)을 관리
    inner class BannerViewHolder(private val binding: ItemBannerBinding)
        : RecyclerView.ViewHolder(binding.root) {

        // 배너 하나를 바인딩할 때 호출됨
        fun bind(item: BannerItem) {
            // Glide를 이용해 이미지 URL 로드 + 자르기 적용
            Glide.with(binding.bannerImage.context)
                .load(item.imageUrl)
                .transform(
                    CropTransformation(600, 300, item.cropType) // 이미지 크롭: 위치는 item.cropType(TOP, CENTER, BOTTOM)
                )
                .into(binding.bannerImage)

            // 이미지 클릭 시 지정된 URL을 콜백으로 전달
            binding.bannerImage.setOnClickListener {
                onClick(item.url)
            }
        }
    }

    // ViewHolder가 처음 생성될 때 호출: 레이아웃 XML을 인플레이트함
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    // 해당 위치의 데이터를 ViewHolder에 바인딩할 때 호출
    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // 아이템 전체 개수 반환 (ViewPager2가 필요로 함)
    override fun getItemCount(): Int = items.size
}
