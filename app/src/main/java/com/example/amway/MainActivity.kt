package com.example.amway

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.amway.Database.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var fab:FloatingActionButton
        lateinit var txtWtoItems:TextView
        lateinit var txtCountItem:TextView
        lateinit var txtCountQty:TextView
        private lateinit var mToggle: ActionBarDrawerToggle
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById<FloatingActionButton>(R.id.fab)
        txtWtoItems = findViewById(R.id.txt_wto_items)
        txtCountItem = findViewById(R.id.count_item)
        txtCountQty = findViewById(R.id.count_qty)

        txtWtoItems.text = DatabaseHandler.totalWTO.toString()
        txtCountItem.text = DatabaseHandler.totalStock.toString()
        txtCountQty.text = DatabaseHandler.totalStockQty.toString()

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

            }
            true
        }

        fab.setOnClickListener {
            val intent = Intent(this,Setup::class.java)
            startActivity(intent)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (mToggle.onOptionsItemSelected(item)) {

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}