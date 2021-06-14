package com.example.amway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amway.Adapter.StockCountAdapter
import com.example.amway.Adapter.SubInvAdapter
import com.example.amway.Database.DatabaseHandler

class SubInv : AppCompatActivity() {

    companion object{
        lateinit var db:DatabaseHandler
        lateinit var recycler:RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_inv)

        db=DatabaseHandler(this)
        db.loadSubInv()

        viewManager = LinearLayoutManager(this)
        viewAdapter = SubInvAdapter(DatabaseHandler.ViewSub, this)
        recycler = findViewById(R.id.rec_sub)
        recycler.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }
}