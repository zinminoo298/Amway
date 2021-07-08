package com.example.amway

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.amway.Database.DatabaseHandler


class Setup : AppCompatActivity() {

    companion object{
        lateinit var db:DatabaseHandler
        lateinit var btn:Button
        lateinit var invSpinner:Spinner
        lateinit var locationSpinner:Spinner
        lateinit var teamSpinner:Spinner
        lateinit var txtWarehouse:TextView
        lateinit var txtTypeSeq:TextView
        var teamList = ArrayList<String>()
        var subinv = ""
        var location = ""
        var team =""
        var subinvId=0
        var locationId = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)


        db = DatabaseHandler(this)
        db.getInventory()
        teamList.clear()
        loadTeam()

        btn = findViewById<Button>(R.id.btn_next)
        invSpinner = findViewById(R.id.spinner_inv)
        locationSpinner = findViewById(R.id.spinner_location)
        teamSpinner = findViewById(R.id.spinner_team)
        txtWarehouse = findViewById(R.id.txt_warehouse)
        txtTypeSeq = findViewById(R.id.txt_type_seq)
        locationSpinner.isEnabled = false

        txtWarehouse.text = DatabaseHandler.wareHouse
        txtTypeSeq.text = "${DatabaseHandler.countType} ${DatabaseHandler.countSeq}"

        if(MainActivity.team != ""){
            teamSpinner.isEnabled = false
            teamList.add(MainActivity.team)
        }
        else{
            teamSpinner.isEnabled = true
            teamList.add("A")
            teamList.add("B")
        }

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.subList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        invSpinner.adapter = arrayAdapter

        val arrayAdapter2: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teamList)
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        teamSpinner.adapter = arrayAdapter2

        if(DatabaseHandler.countType != "TeamCount"){
            locationSpinner.isEnabled = false
            teamSpinner.isEnabled = false
        }

        invSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(invSpinner.selectedItem.toString() != "Select subinventory"){
                    if(DatabaseHandler.countType == "TeamCount"){
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
            if(DatabaseHandler.countType == "TeamCount"){
                if(invSpinner.selectedItem.toString() == "Select subinventory" || locationSpinner.selectedItem.toString() == "Select location" ){
                    Toast.makeText(this, "Please select sub-inventory and location", Toast.LENGTH_SHORT).show()
                }
                else{
                    subinv = invSpinner.selectedItem.toString()
                    location = locationSpinner.selectedItem.toString()
                    team = teamSpinner.selectedItem.toString()
                    subinvId = invSpinner.selectedItemPosition
                    locationId = locationSpinner.selectedItemPosition
                    setTeam(teamSpinner.selectedItem.toString())
                    val intent = Intent(this, CheckStock::class.java)
                    startActivity(intent)
                }
            }
            else{
                if(invSpinner.selectedItem.toString() == "Select subinventory" ){
                    Toast.makeText(this, "Please select sub-inventory", Toast.LENGTH_SHORT).show()
                }
                else{
                    subinv = invSpinner.selectedItem.toString()
                    location = "no location"
                    team = ""
                    subinvId = invSpinner.selectedItemPosition
                    locationId = locationSpinner.selectedItemPosition
                    setTeam("")
                    val intent = Intent(this, CheckStock::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun setTeam(v: String) {
        val editor = getSharedPreferences("team", MODE_PRIVATE).edit()
        editor.putString("valTeam", v)
        editor.apply()
    }

    private fun loadTeam() {
        var prefs = getSharedPreferences("team", Activity.MODE_PRIVATE)
        MainActivity.team = prefs.getString("valTeam", "").toString()
    }

    override fun onBackPressed() {
        db.stockCheck()
        db.wtoCheck()
        MainActivity.txtWtoItems.text = DatabaseHandler.totalWTO.toString()
        MainActivity.txtUncountItem.text = DatabaseHandler.uncountWto.toString()

        if(DatabaseHandler.totalStock == 0){
            MainActivity.stockView.visibility = View.GONE
        }
        else{
            MainActivity.stockView.visibility = View.VISIBLE
        }

        if(MainActivity.team == ""){
            MainActivity.teamView.visibility = View.GONE
        }
        else{
            loadTeam()
            MainActivity.teamView.visibility = View.VISIBLE
            MainActivity.txtTeam.text = MainActivity.team
        }
        MainActivity.txtCountItem.text = DatabaseHandler.totalStock.toString()
        MainActivity.txtCountQty.text = DatabaseHandler.totalStockQty.toString()
        loadStatus()
        if(MainActivity.status != "exported"){
            MainActivity.txtStatus.setText("Stock Count")
            MainActivity.stockView.setBackgroundColor(Color.parseColor("#1199EE"))
        }

        val myIntent = Intent(this, MainActivity::class.java)

        myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(myIntent)
        this.finish()
        super.onBackPressed()
    }

    private fun loadStatus() {
        var prefs = getSharedPreferences("status", Activity.MODE_PRIVATE)
        MainActivity.status = prefs.getString("valStatus", "").toString()
    }
}