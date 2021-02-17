package com.example.amway.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHandler(private val context: Context) {

    companion object{
        const val database = "database.db"
        const val master = "master.db"
        var user_auth = "false"
        var totalWTO = 0
        var totalStock = 0
        var totalStockQty = 0
        var subList = ArrayList<String>()
        var locationList = ArrayList<String>()
    }

    fun openDatabase(): SQLiteDatabase {
        val dbFile=context.getDatabasePath(database)
        if (!dbFile.exists()) {
            try {
                val checkDB=context.openOrCreateDatabase(database, Context.MODE_PRIVATE, null)

                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }

        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    private fun copyDatabase(dbFile: File?) {
        val `is`=context.assets.open(database)
        val os= FileOutputStream(dbFile)

        val buffer=ByteArray(1024)
        while(`is`.read(buffer)>0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        `is`.close()
        Log.d("#DB", "completed..")
    }

    fun loginCheck(user:String, password:String ){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM users WHERE username='$user' AND password='$password'"
        val cursor = db.rawQuery(query,null)

        if(cursor.moveToFirst()){
            user_auth = "true"
        }
        else{
            user_auth = "false"
        }
        db.close()
    }

    fun statusCheck(){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = ""
        val cursor = db.rawQuery(query,null)
        db.close()

    }

    fun wtoCheck(){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*) as count FROM masters"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            totalWTO = cursor.getInt(0)
        }
        else{
            totalWTO = 0
        }
        db.close()
    }

    fun stockCheck(){
        val db = context.openOrCreateDatabase(database,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*) as count FROM record "
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            totalStock = cursor.getInt(0)
        }
        else{
            totalStock = 0
        }

        val query1 = "SELECT SUM(qty) from record"
        val cursor1 = db.rawQuery(query1,null)
        if(cursor1.moveToFirst()){
            totalStockQty = cursor1.getInt(0)
        }
        else{
            totalStockQty = 0
        }
        db.close()
    }

    fun getInventory(){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT subinventory FROM locations GROUP BY subinventory"
        val cursor = db.rawQuery(query,null)
        subList.clear()
        if(cursor.moveToFirst()){
            subList.add("Select subinventory")
            do{
                subList.add(cursor.getString(0))
            }while (cursor.moveToNext())
        }
        else{
            Toast.makeText(context,"No Subinventory Found",Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    fun getLocation(location:String){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT location FROM locations WHERE subinventory='$location'"
        val cursor = db.rawQuery(query,null)
        locationList.clear()
        if(cursor.moveToFirst()){
            locationList.add("Select location")
            do{
                locationList.add(cursor.getString(0))
                println(locationList[0])
            }while (cursor.moveToNext())
        }
        else{
                Toast.makeText(context,"No Location Found",Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

}