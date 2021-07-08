package com.example.amway.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.amway.*
import com.example.amway.Database.DatabaseHandler
import com.example.amway.Modal.WTOModal
import java.text.SimpleDateFormat
import java.util.*

class WTOAdapter(private var Dataset: ArrayList<WTOModal>, private val context: Context) :
    RecyclerView.Adapter<WTOAdapter.MyViewHolder>() {

    internal lateinit var dialog: AlertDialog
    internal lateinit var db:DatabaseHandler
    var date = ""
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtBarcode = itemView.findViewById<TextView>(R.id.txt_barcode)
        val txtDesc = itemView.findViewById<TextView>(R.id.txt_desc)
        val txtInv = itemView.findViewById<TextView>(R.id.txt_inv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_wto_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtbc = holder.txtBarcode
        val txtdesc = holder.txtDesc
        val txtInv = holder.txtInv

        txtbc.text = Dataset[position].barcode
        txtdesc.text = Dataset[position].description
        txtInv.text = Dataset[position].subInv

        holder.view.setOnLongClickListener(View.OnLongClickListener {
            if (MainActivity.checkUncount) {
                alertDialog(
                    Dataset[position].barcode!!,
                    Dataset[position].description!!,
                    Dataset[position].subInv!!,
                    position
                )
            }

            true
        })
    }

    private fun alertDialog(barcode: String, description: String, subInv: String, position: Int) {
        val btnOK: Button
        val btnCancel: Button
        val spinnerLc: Spinner
        val editTextQty : EditText
        val textViewSub : TextView
        val textViewBc : TextView
        val builder= AlertDialog.Builder(context)
        val inflater=context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view=inflater.inflate(R.layout.edit_uncount, null)
        builder.setView(view)
        dialog =builder.create()
        dialog.show()
        dialog.setCancelable(false)

        btnOK = view.findViewById(R.id.btn_ok)
        btnCancel = view.findViewById(R.id.btn_cancel)
        spinnerLc = view.findViewById(R.id.spinner_lc)
        textViewBc = view.findViewById(R.id.txt_bc)
        textViewSub = view.findViewById(R.id.txt_sub)
        editTextQty = view.findViewById(R.id.edt_qty)
        textViewBc.text = barcode
        textViewSub.text = subInv

        val upperInv: String = subInv.substring(0, 1).toUpperCase() + subInv.substring(1).toLowerCase()
        db = DatabaseHandler(context)
        db.getLocation(upperInv)
        val arrayAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(
            context.applicationContext,
            android.R.layout.simple_spinner_item,
            DatabaseHandler.locationList
        )
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLc.adapter = arrayAdapter1

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOK.setOnClickListener {
            if(spinnerLc.selectedItem == "Select location" || editTextQty.text.toString() == "") {
                Toast.makeText(context, "Please fill the fields", Toast.LENGTH_SHORT).show()
            }
            else{
                var subBarcode = barcode.substring(0, barcode.length.coerceAtMost(4))
                db.checkBarcode(
                    barcode,
                    subBarcode,
                    upperInv,
                    DatabaseHandler.wareHouse,
                    spinnerLc.selectedItem.toString(),
                    editTextQty.text.toString(),
                    "${Login.user}",
                    MainActivity.team,
                    date
                )
                Dataset.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, Dataset.size)
                ViewWtoPlan.txtRecord.text ="Total ${Dataset.size} records"
                dialog.dismiss()
            }
        }
    }

    private fun getDate(){
        val c = Calendar.getInstance()
        val day = c[Calendar.DAY_OF_MONTH]
        val month = c[Calendar.MONTH] + 1
        val year = c[Calendar.YEAR]
        val sdf = SimpleDateFormat("hh:mma")
        val t = sdf.format(Date())
        date = "" + day + "/" + month + "/" + year+"_"+t
        println(CheckStock.date)
    }

    override fun getItemCount() = Dataset.size
}