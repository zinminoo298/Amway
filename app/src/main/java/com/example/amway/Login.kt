package com.example.amway

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.amway.Database.DatabaseHandler
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Login : AppCompatActivity() {

    companion object{
        var STORAGE_PERMISSION_CODE = 1
        lateinit var login:Button
        lateinit var edtUser:EditText
        lateinit var edtPsw:EditText
        lateinit var db:DatabaseHandler
        var user = ""
        var filePath = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById<Button>(R.id.btn_login)
        edtUser = findViewById<EditText>(R.id.edit_username)
        edtPsw = findViewById<EditText>(R.id.edit_password)

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestStoragePermission();
        }

        db = DatabaseHandler(this)
        db.openDatabase()

        recursiveScan(File("/sdcard/Download/"))

        edtUser.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->

            if (event.keyCode == KeyEvent.KEYCODE_SPACE && event.action == KeyEvent.ACTION_UP || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                if (edtUser.text.toString() == "") {
                    Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show()
                    edtUser.requestFocus()
                } else {
                    edtPsw.requestFocus()
                }
            }

            false

        })

        edtPsw.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->

            if (event.keyCode == KeyEvent.KEYCODE_SPACE && event.action == KeyEvent.ACTION_UP || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                if (edtUser.text.toString() == "" || edtPsw.text.toString() == "") {
                    Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                } else {
                    loginProcess()
                }
            }

            false

        })


        login.setOnClickListener {
            loginProcess()
        }
    }

    private fun recursiveScan(f: File) {
        val file = f.listFiles()
        for (files in file) {
//            if (ff.isDirectory) recursiveScan(f)
            if (files.isFile && files.path.endsWith(".sqlite")) {
                Log.d("Debug", files.toString())
                filePath = files.toString()
            }
        }
    }

    private fun loginProcess(){
        if(edtUser.text.toString()=="" || edtPsw.text.toString()==""){
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
        }
        else{
//            val filepath="/sdcard/Download/database.sqlite"
            val file= File(filePath)
            if(file.exists())
            {
                setTeam("")
                AsyncTaskRunner(this, File(filePath), File("/data/data/com.example.amway/databases/master.db")).execute()
            }
            else{
                if(File("/data/data/com.example.amway/databases/master.db").exists()){
                    //DO DB job
                    db.loginCheck(edtUser.text.toString(), edtPsw.text.toString())
                    if(DatabaseHandler.user_auth == "true"){
                        db.detailCheck()
                        db.stockCheck()
                        user = edtUser.text.toString()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, "Wrong username or password", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this, "Master file not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setTeam(v: String) {
        val editor = getSharedPreferences("team", MODE_PRIVATE).edit()
        editor.putString("valTeam", v)
        editor.apply()
    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        }

        else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class AsyncTaskRunner(val context: Context?, val src: File?, val output: File?) : AsyncTask<String, String, String>() {
        internal lateinit var pgd: ProgressDialog
        private var running = true
        internal lateinit var db:DatabaseHandler

        override fun doInBackground(vararg params: String?): String {

            while(running){
                copy(src, output)
            }
            return "gg"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
//            pgd.setMessage("Restoring")
            pgd.setTitle("Loading")
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            db = DatabaseHandler(context!!)
            pgd.dismiss()
            src?.delete()

            db.loginCheck(edtUser.text.toString(), edtPsw.text.toString())
            if(DatabaseHandler.user_auth == "true"){
                db.detailCheck()
                db.stockCheck()
                user = edtUser.text.toString()
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
            else{
                Toast.makeText(context, "Wrong username or password", Toast.LENGTH_SHORT).show()
            }

            super.onPostExecute(result)
        }

        override fun onCancelled() {
            running = false
        }

        fun copy(src: File?, dst: File?) {
            val `is`= FileInputStream(src)
            val os= FileOutputStream(dst)

            val buffer=ByteArray(1024)
            while(`is`.read(buffer)>0) {
                os.write(buffer)
                Log.d("#DB", "writing>>")
            }

            os.flush()
            os.close()
            `is`.close()
            running = false
            Log.d("#DB", "completed..")
            db = DatabaseHandler(context!!)
            db.createTable()
        }
    }
}