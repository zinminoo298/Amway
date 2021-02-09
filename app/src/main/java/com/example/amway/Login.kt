package com.example.amway

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.amway.Database.DatabaseHandler

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val login = findViewById<Button>(R.id.btn_login)
        val edtUser = findViewById<EditText>(R.id.edit_username)
        val edtPsw = findViewById<EditText>(R.id.edit_password)

        val db:DatabaseHandler = DatabaseHandler(this)
//        db.openDatabase()

        login.setOnClickListener {

            if(edtUser.text.toString()=="" || edtPsw.text.toString()==""){
                Toast.makeText(this,"Please enter username and password",Toast.LENGTH_SHORT).show()
            }
            else{
                if(edtUser.text.toString()=="admin" && edtPsw.text.toString()=="123456"){
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"Wrong password or username",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}