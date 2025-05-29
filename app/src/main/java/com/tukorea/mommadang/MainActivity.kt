package com.tukorea.mommadang

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.tukorea.mommadang.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 앱 실행 시 HomeFragment 표시
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.containerMain.id, HomeFragment())
                .commit()
        }
    }
}