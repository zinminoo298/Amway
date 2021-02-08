package com.example.amway

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Setup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val btn = findViewById<Button>(R.id.btn_next)

        btn.setOnClickListener {
            val intent = Intent(this,CheckStock::class.java)
            startActivity(intent)
        }
    }
}