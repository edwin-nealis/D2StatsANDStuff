package com.example.d2statsnstuff

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.d2statsnstuff.HomeFragment.HomeFragmentListener
import com.example.d2statsnstuff.data.StatsDTO

class MainActivity : AppCompatActivity(), HomeFragmentListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .add(R.id.containerView, HomeFragment())
            .commit()
    }

    override fun onLogIn() {}

    override fun onSearch(allStats: StatsDTO) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerView, SessionFragment.newInstance(allStats))
            .addToBackStack(null)
            .commit()
    }
}

