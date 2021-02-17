package com.example.amway

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.amway.Database.DatabaseHandler
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class Login : AppCompatActivity() {

    companion object{
        var STORAGE_PERMISSION_CODE = 1
        lateinit var login:Button
        lateinit var edtUser:EditText
        lateinit var edtPsw:EditText
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

        val db:DatabaseHandler = DatabaseHandler(this)
        db.openDatabase()

        login.setOnClickListener {

            if(edtUser.text.toString()=="" || edtPsw.text.toString()==""){
                Toast.makeText(this,"Please enter username and password",Toast.LENGTH_SHORT).show()
            }
            else{
                val filepath="/sdcard/Download/database.sqlite"
                val file= File(filepath)
                if(file.exists())
                {
                    AsyncTaskRunner(this, File("/sdcard/Download/database.sqlite"), File("/data/data/com.example.amway/databases/master.db")).execute()
                }
                else{
                    if(File("/data/data/com.example.amway/databases/master.db").exists()){
                        //DO DB job
                        db.loginCheck(edtUser.text.toString(), edtPsw.text.toString())
                        if(DatabaseHandler.user_auth == "true"){
                            db.statusCheck()
                            db.stockCheck()
                            db.wtoCheck()
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,"Wrong username or password",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(this,"Master file not found",Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this,MainActivity::class.java)
//                        startActivity(intent)
                    }
                }
            }
        }
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
                copy(src,output)
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
                db.statusCheck()
                db.stockCheck()
                db.wtoCheck()
                val intent = Intent(context,MainActivity::class.java)
                context.startActivity(intent)
            }
            else{
                Toast.makeText(context,"Wrong username or password",Toast.LENGTH_SHORT).show()
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
        }
    }
}