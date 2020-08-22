package com.androiddevs.newsflash.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androiddevs.newsflash.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
