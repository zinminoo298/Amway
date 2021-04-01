package com.example.amway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amway.Adapter.StockCountAdapter
import com.example.amway.Adapter.WTOAdapter
import com.example.amway.Database.DatabaseHandler
import com.example.amway.ViewWtoPlan.Companion.spinner

class ViewWtoPlan : AppCompatActivity() {

    companion object{
        internal lateinit var db: DatabaseHandler
        lateinit var txtType : TextView
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
        private lateinit var txtRecord:TextView
        private lateinit var spinner:Spinner
        var showList = ArrayList<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_wto_plan)

        txtType = findViewById(R.id.txt_type)
        spinner = findViewById(R.id.spinner)

        db = DatabaseHandler(this)
        DatabaseHandler.ViewWTO.clear()
        showList.clear()
        db.loadWTO()
        txtType.text = "${DatabaseHandler.countType} ${DatabaseHandler.countSeq}"

        showList.add("All")
        showList.add("Uncount")

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_item, showList)
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter

        viewManager = LinearLayoutManager(this)
        viewAdapter = WTOAdapter(DatabaseHandler.ViewWTO, this)
        txtRecord = findViewById(R.id.total_records)

        recyclerView = findViewById<RecyclerView>(R.id.recycler2)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
        txtRecord.setText("Total ${DatabaseHandler.ViewWTO.size} records")

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(spinner.selectedItem == "Uncount"){
                    db.loadUncheckWTO()
                    recyclerView.adapter!!.notifyDataSetChanged()
                    txtRecord.setText("Total ${DatabaseHandler.ViewWTO.size} records")
                }
                else{
                    db.loadWTO()
                    recyclerView.adapter!!.notifyDataSetChanged()
                    txtRecord.setText("Total ${DatabaseHandler.ViewWTO.size} records")
                }
            }
        }

    }
}