package com.example.edgevisionrt

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Test native library
        val tv = TextView(this)
        tv.text = stringFromJNI()
        setContentView(tv)
    }

    private external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("edgevisionrt")
        }
    }
}