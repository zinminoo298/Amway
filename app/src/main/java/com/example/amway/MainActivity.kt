package com.example.amway

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.amway.Database.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var fab:FloatingActionButton
        lateinit var txtStatus:TextView
        lateinit var txtWtoItems:TextView
        lateinit var txtCountItem:TextView
        lateinit var txtUncountItem:TextView
        lateinit var txtCountQty:TextView
        lateinit var txtWarehouse:TextView
        lateinit var txtTeam:TextView
        lateinit var txtCountType:TextView
        lateinit var txtSequence:TextView
        lateinit var stockView:RelativeLayout
        lateinit var teamView:RelativeLayout
        lateinit var wtoView: RelativeLayout
        lateinit var txtDate:TextView
        private lateinit var mToggle: ActionBarDrawerToggle
        private var progressBar1: ProgressBar? =null
        internal lateinit var dialog: AlertDialog
        internal lateinit var export: Button
        var team = ""
        var status=""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById<FloatingActionButton>(R.id.fab)
        txtStatus = findViewById(R.id.export_status)
        txtWtoItems = findViewById(R.id.txt_wto_items)
        txtCountItem = findViewById(R.id.count_item)
        txtCountQty = findViewById(R.id.count_qty)
        txtWarehouse = findViewById(R.id.txt_warehouse)
        txtTeam = findViewById(R.id.txt_team)
        txtCountType = findViewById(R.id.txt_count_type)
        txtSequence = findViewById(R.id.txt_seq)
        stockView = findViewById(R.id.anchor3)
        teamView = findViewById(R.id.anchor4)
        wtoView = findViewById(R.id.anchor1)
        txtDate = findViewById(R.id.date)
        txtUncountItem = findViewById(R.id.txt_uncount_items)

        loadTeam()
        loadStatus()

        txtWtoItems.text = DatabaseHandler.totalWTO.toString()
        txtCountItem.text = DatabaseHandler.totalStock.toString()
        txtCountQty.text = DatabaseHandler.totalStockQty.toString()
        txtWarehouse.text = DatabaseHandler.wareHouse
        txtTeam.text = team
        txtCountType.text = DatabaseHandler.countType
        txtSequence.text = DatabaseHandler.countSeq
        txtDate.setText("Date : ${ DatabaseHandler.countDate }")
        txtUncountItem.text = DatabaseHandler.uncountWto.toString()

        if(DatabaseHandler.totalStock == 0){
            stockView.visibility = GONE
        }

        if(team == ""){
            teamView.visibility = GONE
        }

        if(status == "exported"){
            txtStatus.setText("Exported")
            stockView.setBackgroundColor(Color.parseColor("#66BB66"))
        }

        val mDrawerLayout=findViewById<DrawerLayout>(R.id.drawer)
        mToggle= ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView =findViewById(R.id.nav)
        navigationView.setNavigationItemSelectedListener { menuItem ->

            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()
            menuItem.isChecked = !menuItem.isChecked

            // Handle navigation view item clicks here.
            when (menuItem.itemId) {
                R.id.nav_home->{

                }

                R.id.nav_view->{
                    if(stockView.isVisible){
                        val intent = Intent(this,ViewStockCount::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this,"No Scanned Data",Toast.LENGTH_SHORT).show()
                    }
                }

                R.id.nav_export->{
                    exportDialog()
                }

                R.id.nav_clear->{
                    val db = this.openOrCreateDatabase("master.db",Context.MODE_PRIVATE,null)
                    db.execSQL("DELETE FROM record")
                    Toast.makeText(this,"Data Cleared",Toast.LENGTH_SHORT).show()
                    stockView.visibility = GONE
                    teamView.visibility = GONE
                    setTeam("")
                }
            }
            true
        }

        fab.setOnClickListener {
            val intent = Intent(this,Setup::class.java)
            startActivity(intent)
        }

        stockView.setOnClickListener {
            val intent = Intent(this,ViewStockCount::class.java)
            startActivity(intent)
        }

        wtoView.setOnClickListener {
            val intent = Intent(this,ViewWtoPlan::class.java)
            startActivity(intent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (mToggle.onOptionsItemSelected(item)) {

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun exportDialog(){
        val builder= AlertDialog.Builder(this)
        val inflater=this.layoutInflater
        val view=inflater.inflate(R.layout.export_layout, null)
        builder.setView(view)
        dialog=builder.create()
        dialog.setMessage("DATA WILL BE EXPORTED TO sdcard/Download")
        progressBar1 = view.findViewById(R.id.progress_bar)
        progressBar1!!.visibility = View.GONE
        dialog.show()

        export = view.findViewById(R.id.btn_export)
        export.setOnClickListener {
            progressBar1!!.visibility = View.VISIBLE

            Handler().postDelayed({

                val filepath = "/sdcard/Download/export.csv"
                val file = File(filepath)
                if (file.exists()) {
                    export()
//                Toast.makeText(this, "FILE EXPORT SUCCESSFUL", Toast.LENGTH_LONG).show()

                } else {
                    generateNoteOnSD(this, "/export.csv")
                    if (file.exists()) {
                        export()
//                    Toast.makeText(this, "FILE EXPORT SUCCESSFUL", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                                this,
                                "EXPORT UNSUCCESSFUL. MAKE SURE TO GIVE STORAGE ACCESS",
                                Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }, 1000)

            setStatus("exported")
            txtStatus.setText("Exported")
            stockView.setBackgroundColor(Color.parseColor("#66BB66"))
        }
    }

    fun generateNoteOnSD(context: Context, sFileName: String) {
        try {
            val root=File("/sdcard/Download")
            if (!root.exists()) {
                root.mkdirs()
            }
            val gpxfile=File(root, sFileName)
            val writer= FileWriter(gpxfile)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun export(){
        progressBar1!!.visibility = View.GONE

        try {
            val database=this.openOrCreateDatabase("master.db", Context.MODE_PRIVATE, null)
            val selectQuery=
                    " SELECT warehouse,barcode,inventory,location,qty,team,user,time FROM record "
            val cursor=database.rawQuery(selectQuery, null)
            var rowcount: Int
            var colcount: Int

            val saveFile=File("/sdcard/Download/export.csv")
            val fw=FileWriter(saveFile)

            var k = 1
            val bw= BufferedWriter(fw)
            rowcount=cursor.getCount()
            colcount=cursor.getColumnCount()
            bw.write("Warehouse,OracleItem,SubInv,Location,Qty,Team,User,UpdateDateTime")
            bw.newLine()

            if (rowcount>0) {

                for (i in 0 until rowcount) {
                    cursor!!.moveToPosition(i)

                    for (j in 0 until colcount) {
                        if (j == 0) {
                            bw.write(cursor!!.getString(j) + ",")
                        }
                        if (j == 1) {

                            bw.write(cursor!!.getString(j) + ",")

                        }
                        if (j == 2) {

                            bw.write(cursor!!.getString(j) + ",")

                        }
                        if (j == 3) {

                            bw.write(cursor!!.getString(j) + ",")

                        }
                        if (j == 4) {
                            bw.write(cursor!!.getString(j) + ",")

                        }
                        if (j == 5) {

                            bw.write(cursor!!.getString(j) + ",")

                        }
                        if (j == 6) {

                            bw.write(cursor!!.getString(j) + ",")

                        }
                        if (j == 7) {

                            bw.write(cursor!!.getString(j))
                        }
                    }
                    bw.newLine()
                    k++
                }
                bw.flush()

            }
            Toast.makeText(this,"Export Successful",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        catch (ex: Exception) {
            ex.printStackTrace()

        }

    }

    private fun loadTeam() {
        var prefs = getSharedPreferences("team", Activity.MODE_PRIVATE)
        team = prefs.getString("valTeam", "").toString()
    }

    private fun setStatus(v:String){
        var editor = getSharedPreferences("status",Activity.MODE_PRIVATE).edit()
        editor.putString("valStatus",v)
        editor.apply()
    }

    private fun loadStatus() {
        var prefs = getSharedPreferences("status", Activity.MODE_PRIVATE)
        status = prefs.getString("valStatus", "").toString()
    }

    private fun setTeam(v: String) {
        val editor = getSharedPreferences("team", MODE_PRIVATE).edit()
        editor.putString("valTeam", v)
        editor.apply()
    }

}