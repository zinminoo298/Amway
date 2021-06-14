package com.example.amway.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.amway.Database.DatabaseHandler
import com.example.amway.Modal.SubInvModal
import com.example.amway.R

class SubInvAdapter(private var Dataset: ArrayList<SubInvModal>, private val context: Context) :
    RecyclerView.Adapter<SubInvAdapter.MyViewHolder>() {

    companion object{
        lateinit var db: DatabaseHandler
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val subInv = itemView.findViewById<TextView>(R.id.txt_1)
        val recycler = itemView.findViewById<RecyclerView>(R.id.recyc_1)
        val btn_hide = itemView.findViewById<Button>(R.id.btn_hide)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.test_subinv_1, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtInv = holder.subInv
        val recycler = holder.recycler
        val btn_hide = holder.btn_hide
        txtInv.text = Dataset[position].subInv

        db = DatabaseHandler(context)
        db.loadStockCount_1(Dataset[position].subInv)
        viewManager = LinearLayoutManager(context)
        viewAdapter = StockCountAdapter(DatabaseHandler.ViewSC, context)
        recycler.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        btn_hide.setOnClickListener {
            if(recycler.isVisible){
                recycler.visibility = GONE
            }
            else{
                db.loadStockCount_1(Dataset[position].subInv)
                viewManager = LinearLayoutManager(context)
                viewAdapter = StockCountAdapter(DatabaseHandler.ViewSC, context)
                recycler.apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
                recycler.visibility = VISIBLE
            }


        }
    }

    override fun getItemCount() = Dataset.size


}