package com.example.amway

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.amway.Database.DatabaseHandler


class Setup : AppCompatActivity() {

    companion object{
        lateinit var db:DatabaseHandler
        lateinit var btn:Button
        lateinit var invSpinner:Spinner
        lateinit var locationSpinner:Spinner
        lateinit var teamSpinner:Spinner
        var teamList = ArrayList<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)


        db = DatabaseHandler(this)
        db.getInventory()
        teamList.clear()

        btn = findViewById<Button>(R.id.btn_next)
        invSpinner = findViewById(R.id.spinner_inv)
        locationSpinner = findViewById(R.id.spinner_location)
        teamSpinner = findViewById(R.id.spinner_team)
        locationSpinner.isEnabled = false

        teamList.add("Team A")
        teamList.add("Team B")

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.subList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        invSpinner.adapter = arrayAdapter

        val arrayAdapter2: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teamList)
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        teamSpinner.adapter = arrayAdapter2



        invSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(invSpinner.selectedItem.toString() != "Select subinventory"){
                    println("OK")
                    locationSpinner.isEnabled = true
                    db.getLocation(invSpinner.selectedItem.toString())


                    val arrayAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, DatabaseHandler.locationList)
                    arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    locationSpinner.adapter = arrayAdapter1
                }
                else{
                    locationSpinner.isEnabled = false
                    DatabaseHandler.locationList.clear()
                    DatabaseHandler.locationList.add("Select location")
                    val arrayAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, DatabaseHandler.locationList)
                    arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    locationSpinner.adapter = arrayAdapter1
                }
            }
        }

        btn.setOnClickListener {
            val intent = Intent(this, CheckStock::class.java)
            startActivity(intent)
        }
    }
}