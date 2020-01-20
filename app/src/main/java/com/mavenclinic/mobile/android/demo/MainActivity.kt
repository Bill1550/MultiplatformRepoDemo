package com.mavenclinic.mobile.android.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mavenclinic.mobile.android.demo.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

}
