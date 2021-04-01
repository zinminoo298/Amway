package com.example.amway.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.amway.Modal.StockCountModal
import com.example.amway.R

class StockCountAdapter(private var Dataset: ArrayList<StockCountModal>, private val context: Context) :
        RecyclerView.Adapter<StockCountAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtBarcode = itemView.findViewById<TextView>(R.id.view_bc)
        val txtSub = itemView.findViewById<TextView>(R.id.view_inv)
        val txtDesc = itemView.findViewById<TextView>(R.id.view_desc)
        val txtQty = itemView.findViewById<TextView>(R.id.view_qty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_stock_count_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtbc = holder.txtBarcode
        val txtsub = holder.txtSub
        val txtdesc = holder.txtDesc
        val txtqty = holder.txtQty

        txtbc.text = Dataset[position].barcode
        txtsub.text= "${ Dataset[position].subinventory } / ${ Dataset[position].location }"
        txtdesc.text = Dataset[position].description
        txtqty.text = Dataset[position].qty.toString()
    }
    override fun getItemCount() = Dataset.size
}