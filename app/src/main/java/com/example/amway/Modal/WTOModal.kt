package com.example.amway.Modal

class WTOModal {
    var barcode:String? = null
    var description:String? = null
    var subInv:String? = null

    constructor(barcode: String, description: String, subInv:String){
        this.barcode = barcode
        this.description = description
        this.subInv = subInv
    }
}