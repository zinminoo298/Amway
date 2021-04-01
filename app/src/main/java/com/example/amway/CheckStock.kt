package com.example.amway

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.amway.Database.DatabaseHandler
import java.text.SimpleDateFormat
import java.util.*


class CheckStock : AppCompatActivity() {

    companion object{
        lateinit var Layout:RelativeLayout
        lateinit var Layout1:RelativeLayout
        lateinit var txtSub:TextView
        lateinit var txtLocation:TextView
        lateinit var txtWarehouse:TextView
        lateinit var txtTeam:TextView
        lateinit var txtCountSeq:TextView
        lateinit var edtBarcode:EditText
        lateinit var edtQty:EditText
        lateinit var edtItem:EditText
        lateinit var edtDesc:EditText
        lateinit var txtItems:TextView
        lateinit var txtQty:TextView
        lateinit var txtCurrentQty:TextView
        lateinit var switchQty:Switch
        lateinit var switchBox:Switch
        lateinit var Imgsub:ImageView
        lateinit var ImgLoc:ImageView
        lateinit var db:DatabaseHandler
        lateinit var txtCartonQty:TextView
        internal lateinit var dialog: AlertDialog
        internal lateinit var dialog1: AlertDialog
        var check = 0
        var newitem=""
        var n = 0
        var bc:String? = null
        var date = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_stock)

        db = DatabaseHandler(this)
        Layout = findViewById(R.id.RelativeLayout)
        Layout1 = findViewById(R.id.RelativeLayout1)
        txtSub = findViewById(R.id.txt_sub)
        txtLocation = findViewById(R.id.txt_location)
        txtWarehouse = findViewById(R.id.txt_warehouse)
        txtTeam = findViewById(R.id.txt_team)
        txtCountSeq = findViewById(R.id.txt_type_seq)
        edtBarcode = findViewById(R.id.edit_barcode)
        edtQty = findViewById(R.id.edit_qty)
        edtItem = findViewById(R.id.edit_item)
        edtDesc = findViewById(R.id.edit_desc)
        txtItems = findViewById(R.id.txt_items)
        txtQty = findViewById(R.id.txt_qty)
        switchQty = findViewById(R.id.switch_qty)
        switchBox = findViewById(R.id.switch_carton)
        txtCurrentQty = findViewById(R.id.bcqty)
        Imgsub = findViewById(R.id.img_sub)
        ImgLoc = findViewById(R.id.img_loc)
        txtCartonQty = findViewById(R.id.txt_carton_qty)

        txtWarehouse.text = DatabaseHandler.wareHouse
        txtTeam.text = Setup.team
        txtCountSeq.text = "${DatabaseHandler.countType} ${DatabaseHandler.countSeq}"
        txtSub.text = Setup.subinv
        txtLocation.text = Setup.location
        txtCurrentQty.text = "Qty : "
        edtBarcode.requestFocus()

        edtQty.setText("1")
        edtQty.isFocusableInTouchMode = false
        edtQty.isFocusable = false
        edtQty.isEnabled = false
        txtCartonQty.visibility = GONE

        getDate()

        if(DatabaseHandler.countType != "TeamCount"){
            txtLocation.text = ""
            Layout.visibility = GONE
            Layout1.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        }


        switchQty.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    check = 1
                    edtQty.isFocusable = true
                    edtQty.isEnabled = true
                    edtQty.isFocusableInTouchMode = true
                    edtQty.setText("1")
                    println("CHECKED")
                } else {
                    check = 0
                    edtQty.setText("1")

                    edtBarcode.requestFocus()
                    edtQty.isFocusableInTouchMode = false
                    edtQty.isFocusable = false
                    edtQty.isEnabled = false

                }
            }
        })

        switchBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    edtQty.setText("1")
                    edtBarcode.requestFocus()
                    edtQty.isFocusableInTouchMode = false
                    edtQty.isFocusable = false
                    edtQty.isEnabled = false
                    switchQty.isEnabled = false
                    txtCartonQty.visibility = VISIBLE
                } else {
                    edtQty.isFocusable = true
                    edtQty.isEnabled = true
                    edtQty.isFocusableInTouchMode = true
                    switchQty.isEnabled = true
                    txtCartonQty.visibility = GONE
                }
            }
        })

        edtBarcode.setFilters(arrayOf<InputFilter>(AllCaps()))

        edtBarcode.setOnKeyListener(View.OnKeyListener { _, _, event ->

            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (edtBarcode.text.toString() == "" || txtSub.text.toString() == "Select subinventory" || txtLocation.text.toString() == "Select location" || txtLocation.text.toString() == "") {
                    Toast.makeText(this, "Please check barcode,subinventory and location", Toast.LENGTH_SHORT).show()
                } else {
                    if (switchBox.isChecked) {
                        newitem = edtBarcode.text.toString()
                        var barcode = edtBarcode.text.toString()
                        var subBarcode = barcode.substring(0, barcode.length.coerceAtMost(4))
                        getDate()
                        db.checkCarton(subBarcode, edtBarcode.text.toString(), txtSub.text.toString(), txtWarehouse.text.toString(), txtLocation.text.toString(), edtQty.text.toString(), "${Login.user}", txtTeam.text.toString(), date)
                        if (DatabaseHandler.cartoncheck == "yes") {
                            println("OK $bc")
                            edtItem.setText("${DatabaseHandler.oracle}")
                            edtDesc.setText("${DatabaseHandler.description}")
                            txtItems.text = "${DatabaseHandler.items}"
                            txtQty.text = "${DatabaseHandler.itemsQty}"
                            txtCurrentQty.text = "Qty : ${DatabaseHandler.currentQty}"
                            if (txtCurrentQty.text.toString() == "0") {
                                txtCartonQty.text = "Carton Qty : 0"
                            } else {
                                if (bc == edtBarcode.text.toString()) {
                                    n = n + 1
                                    txtCartonQty.text = "Carton Qty : $n"
                                    bc = edtBarcode.text.toString()
                                } else {
                                    bc = edtBarcode.text.toString()
                                    n = 1
                                    txtCartonQty.text = "Carton Qty : $n"
                                }
                            }
                            edtBarcode.text.clear()
                            edtBarcode.requestFocus()
                            setStatus("")
                        } else {
                            addNewItem()
                            edtBarcode.text.clear()
                        }

                    } else {
                        if (switchQty.isChecked) {
                            edtQty.requestFocus()
                            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(edtQty, InputMethodManager.SHOW_IMPLICIT)
                        } else {
                            var barcode = edtBarcode.text.toString()
                            var subBarcode = barcode.substring(0, barcode.length.coerceAtMost(4))
                            newitem = edtBarcode.text.toString()
                            getDate()
                            db.checkBarcode(edtBarcode.text.toString(), subBarcode, txtSub.text.toString(), txtWarehouse.text.toString(), txtLocation.text.toString(), edtQty.text.toString(), "${Login.user}", txtTeam.text.toString(), date)
                            if (DatabaseHandler.orcCheck == "no") {
                                addNewItem()
                            } else {
                                edtItem.setText("${DatabaseHandler.oracle}")
                                edtDesc.setText("${DatabaseHandler.description}")
                                txtItems.text = "${DatabaseHandler.items}"
                                txtQty.text = "${DatabaseHandler.itemsQty}"
                                txtCurrentQty.text = "Qty : ${DatabaseHandler.currentQty}"
                                edtBarcode.text.clear()
                                edtBarcode.requestFocus()
                                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(edtBarcode.getWindowToken(), 0)
                                setStatus("")
                            }
                        }
                    }
                }
            }
            false
        })

        edtQty.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                println(txtLocation.text.toString()+"OKOK")

                if (edtBarcode.text.toString() == "" || edtQty.text.toString() == "" || txtSub.text.toString() == "Select subinventory"  || txtLocation.text.toString() == "Select location" || txtLocation.text.toString() == "") {
                    Toast.makeText(this,"Please check barcode,subinventory,location and qty",Toast.LENGTH_SHORT).show()
                    edtQty.requestFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(edtQty, InputMethodManager.SHOW_IMPLICIT)
                } else {
                    var barcode = edtBarcode.text.toString()
                    var subBarcode = barcode.substring(0, barcode.length.coerceAtMost(4))
                    newitem = edtBarcode.text.toString()
                    getDate()
                    db.checkBarcode(edtBarcode.text.toString(), subBarcode, txtSub.text.toString(), txtWarehouse.text.toString(), txtLocation.text.toString(), edtQty.text.toString(), "${Login.user}", txtTeam.text.toString(), date)
                    if (DatabaseHandler.orcCheck == "no") {
                        addNewItem()
                    } else {
                        edtItem.setText("${DatabaseHandler.oracle}")
                        edtDesc.setText("${DatabaseHandler.description}")
                        txtItems.text = "${DatabaseHandler.items}"
                        txtQty.text = "${DatabaseHandler.itemsQty}"
                        txtCurrentQty.text = "Qty : ${DatabaseHandler.currentQty}"
                        edtQty.text.clear()
                        edtBarcode.text.clear()
                        edtBarcode.requestFocus()
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(edtBarcode.getWindowToken(), 0)
                        setStatus("")
                    }
                }
            }
//            if (DatabaseHandler.orcCheck == "no" || DatabaseHandler.cartoncheck == "no") {
//                addNewItem()
//            }
            false
        })

        Imgsub.setOnClickListener {
            changeDialog("sub")
        }

        ImgLoc.setOnClickListener {
            changeDialog("loc")
        }
    }

    fun changeDialog(value: String){
        val btnOK:Button
        val btnCancel:Button
        val spinner:Spinner
        val builder= AlertDialog.Builder(this)
        val inflater=this.layoutInflater
        val view=inflater.inflate(R.layout.change_location, null)
        builder.setView(view)
        dialog=builder.create()
//        dialog.setMessage("THE DATA WILL BE EXPORTED TO sdcard/Stock Export")
        dialog.show()
        dialog.setCancelable(false)
        btnOK = view.findViewById(R.id.btn_ok)
        btnCancel = view.findViewById(R.id.btn_cancel)
        spinner = view.findViewById(R.id.spinner)

        if(value == "sub"){
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.subList)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
            spinner.setSelection(Setup.subinvId)
        }
        else{
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.locationList)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
            spinner.setSelection(Setup.locationId)
        }

        btnOK.setOnClickListener {
            if(value == "sub"){
                db.getLocation(spinner.selectedItem.toString())
                dialog.dismiss()
                Setup.locationId = 0
                Setup.subinvId = spinner.selectedItemPosition
                txtSub.setText(spinner.selectedItem.toString())
                txtLocation.text = ""
            }
            else{
                dialog.dismiss()
                Setup.locationId = spinner.selectedItemPosition
                txtLocation.text = spinner.selectedItem.toString()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun addNewItem(){
        val btnOK:Button
        val btnCancel:Button
        val edtItem:EditText
        val edtDesc:EditText
        val edt_Qty:EditText
        val builder= AlertDialog.Builder(this)
        val inflater=this.layoutInflater
        val view=inflater.inflate(R.layout.view_add_new_item, null)
        builder.setView(view)
        dialog1=builder.create()
        dialog1.show()
        dialog1.setCancelable(false)
        btnOK = view.findViewById(R.id.btn_ok)
        btnCancel = view.findViewById(R.id.btn_cancel)
        edtItem = view.findViewById(R.id.edt_orc)
        edtDesc = view.findViewById(R.id.edt_desc)
        edt_Qty = view.findViewById(R.id.edt_qty)

        edtItem.setText(newitem)
        edtDesc.setText("Add New")
        edt_Qty.setText(edtQty.text.toString())

        btnOK.setOnClickListener {
            getDate()
            db.addNew(newitem, edtDesc.text.toString(), txtWarehouse.text.toString(), txtSub.text.toString(), txtLocation.text.toString(), edt_Qty.text.toString(), "${Login.user}", txtTeam.text.toString(), date)
            dialog1.dismiss()

            Companion.edtItem.setText(edtItem.text.toString())
            Companion.edtDesc.setText(edtDesc.text.toString())
            txtItems.text = "${DatabaseHandler.items}"
            txtQty.text = "${DatabaseHandler.itemsQty}"
            txtCurrentQty.text = "Qty : ${edt_Qty.text.toString()}"
            n=1
            bc = edtItem.text.toString()
            println(bc)
            txtCartonQty.text = "Carton Qty : 1"
            edtBarcode.text.clear()
            setStatus("")
        }

        btnCancel.setOnClickListener {
            dialog1.dismiss()
            dialog1.cancel()
            edtBarcode.text.clear()
            edtBarcode.requestFocus()
        }

    }
    private fun setStatus(v: String){
        var editor = getSharedPreferences("status", Activity.MODE_PRIVATE).edit()
        editor.putString("valStatus", v)
        editor.apply()
    }

    private fun getDate(){
        val c = Calendar.getInstance()
        val day = c[Calendar.DAY_OF_MONTH]
        val month = c[Calendar.MONTH] + 1
        val year = c[Calendar.YEAR]
        val sdf = SimpleDateFormat("hh:mma")
        val t = sdf.format(Date())
        date = "" + day + "/" + month + "/" + year+"_"+t
        println(date)
    }

}