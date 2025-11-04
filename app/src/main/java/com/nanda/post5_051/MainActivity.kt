package com.nanda.post5_051

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nanda.post5_051.databinding.ActivityMainBinding
import com.nanda.post5_051.ui.home.HomeFragment   //

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commit()
        }
    }
}
