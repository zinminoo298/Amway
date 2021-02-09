package com.example.amway.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHandler(val context: Context) {

    companion object{
        const val database = "database.db"
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
}