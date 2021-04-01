package com.example.amway.Modal

class StockCountModal {
    var barcode:String? = null
    var subinventory:String? = null
    var location:String? = null
    var description:String? = null
    var qty:Int? = null

    constructor(barcode: String, subinventory: String,location:String, description: String, qty: Int){
        this.barcode = barcode
        this.subinventory = subinventory
        this.location = location
        this.description = description
        this.qty = qty
    }


}