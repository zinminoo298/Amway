package com.example.amway.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.amway.Modal.StockCountModal
import com.example.amway.Modal.WTOModal
import com.example.amway.R

class WTOAdapter(private var Dataset: ArrayList<WTOModal>, private val context: Context) :
        RecyclerView.Adapter<WTOAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtBarcode = itemView.findViewById<TextView>(R.id.txt_barcode)
        val txtDesc = itemView.findViewById<TextView>(R.id.txt_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_wto_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtbc = holder.txtBarcode
        val txtdesc = holder.txtDesc

        txtbc.text = Dataset[position].barcode
        txtdesc.text = Dataset[position].description
    }

    override fun getItemCount() = Dataset.size
}