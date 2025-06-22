package com.tukorea.mommadang

import jp.wasabeef.glide.transformations.CropTransformation

data class BannerItem(
    val imageUrl: String,  // 배너 이미지 (리소스 아이디)
    val url: String,       // 클릭 시 이동할 URL
    val cropType: CropTransformation.CropType // ← 자를 위치 정보 추가
)
