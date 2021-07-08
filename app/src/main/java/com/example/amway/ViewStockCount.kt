package com.example.amway

import android.app.Activity
import android.os.Bundle
import android.service.autofill.Dataset
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amway.Adapter.StockCountAdapter
import com.example.amway.Database.DatabaseHandler
import com.example.amway.Modal.StockCountModal


class ViewStockCount : AppCompatActivity() {

    companion object{
        internal lateinit var db:DatabaseHandler
//        private lateinit var btnSearchView: ImageView
//        private lateinit var btnClear : ImageView
//        private lateinit var edtSearch:EditText
        private lateinit var txtRecord:TextView
        private lateinit var txtTeam:TextView
        private lateinit var txtType:TextView
        private lateinit var searchView: SearchView
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
        var team = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stock_count)

        db = DatabaseHandler(this)
//        btnSearchView = findViewById(R.id.btn_search)
//        edtSearch = findViewById(R.id.edt_search)
//        btnClear = findViewById(R.id.btn_clear)
        txtRecord = findViewById(R.id.total_records)
        txtTeam = findViewById(R.id.txt_team)
        txtType = findViewById(R.id.txt_type)
        searchView = findViewById(R.id.search_view)

        DatabaseHandler.ViewSC.clear()
        db.loadStockCount()
        loadTeam()

        txtTeam.setText("Team $team")
        txtType.text = "${DatabaseHandler.countType} ${DatabaseHandler.countSeq}"
        viewManager = LinearLayoutManager(this)
        viewAdapter = StockCountAdapter(DatabaseHandler.ViewSC, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
        txtRecord.setText("Total ${DatabaseHandler.ViewSC.size} records")
        var list = DatabaseHandler.ViewSC

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // collapse the view ?
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // search goes here !!
                var filteredList = ArrayList<StockCountModal>()
                if (newText != "") {
                    for (item in list) {
                        if (item.barcode!!.toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(item)
                        }
                    }
                    setupRecyclerView(filteredList)
                } else {
                    setupRecyclerView(list)
                }
                return false
            }
        })


//        btnSearchView.setOnClickListener {
//            db.search(edtSearch.text.toString())
//
//            if(DatabaseHandler.ViewSC.size == 0){
//                edtSearch.text.clear()
//            }
//            else{
//                btnClear.visibility = VISIBLE
//                btnSearchView.visibility = GONE
//                recyclerView.adapter!!.notifyDataSetChanged()
//                txtRecord.setText("Total ${DatabaseHandler.ViewSC.size} records")
//
//            }
//        }

//        btnClear.setOnClickListener {
//            DatabaseHandler.ViewSC.clear()
//            db.loadStockCount()
//            recyclerView.adapter!!.notifyDataSetChanged()
//            txtRecord.setText("Total ${DatabaseHandler.ViewSC.size} records")
//            edtSearch.text.clear()
//            btnClear.visibility = GONE
//            btnSearchView.visibility = VISIBLE
//        }

    }

    private fun setupRecyclerView(list: ArrayList<StockCountModal>) {
        viewAdapter = StockCountAdapter(list, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun loadTeam() {
        var prefs = getSharedPreferences("team", Activity.MODE_PRIVATE)
        team = prefs.getString("valTeam", "").toString()
    }
}