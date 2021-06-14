package com.example.amway.Database

import android.content.ContentValues
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.amway.CheckStock
import com.example.amway.Modal.StockCountModal
import com.example.amway.Modal.SubInvModal
import com.example.amway.Modal.WTOModal
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHandler(private val context: Context) {

    companion object{
        const val database = "database.db"
        const val master = "master.db"
        var user_auth = "false"
        var totalWTO = 0
        var uncountWto = 0
        var totalStock = 0
        var totalStockQty = 0
        var subList = ArrayList<String>()
        var locationList = ArrayList<String>()
        var wareHouse = ""
        var countId = ""
        var countType = ""
        var countSeq = ""
        var countDate = ""
        var team = ""
        var description = ""
        var oracle = ""
        var items = 0
        var itemsQty = 0
        var currentQty = 0
        var cartonQty=0
        var ViewSC = ArrayList<StockCountModal>()
        var ViewWTO = ArrayList<WTOModal>()
        var ViewSub = ArrayList<SubInvModal>()
        var cartoncheck = ""
        var orcCheck=""
    }

    fun openDatabase(): SQLiteDatabase {
        val dbFile=context.getDatabasePath(database)
        if (!dbFile.exists()) {
            try {
                val checkDB=context.openOrCreateDatabase(database, Context.MODE_PRIVATE, null)

                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }

        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    private fun copyDatabase(dbFile: File?) {
        val `is`=context.assets.open(database)
        val os= FileOutputStream(dbFile)

        val buffer=ByteArray(1024)
        while(`is`.read(buffer)>0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        `is`.close()
        Log.d("#DB", "completed..")
    }

    fun createTable(){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        db.execSQL("CREATE TABLE \"record\" (\n" +
                "\t\"barcode\"\tVARCHAR2(20),\n" +
                "\t\"inventory\"\tVARCHAR2(20),\n" +
                "\t\"location\"\tVARCHAR2(20),\n" +
                "\t\"qty\"\tINTEGER(10),\n" +
                "\t\"user\"\tVARCHAR2(20),\n" +
                "\t\"team\"\tVARCHAR2(6),\n" +
                "\t\"warehouse\"\tVARCHAR2(20),\n" +
                "\t\"time\"\tVARCHAR2(30),\n" +
                "\t\"scanned_barcode\"\tVARCHAR2(20),\n" +
                "\t\"description\"\tVARCHAR2(80),\n" +
                "\tPRIMARY KEY(\"barcode\",\"location\",\"inventory\",\"warehouse\",\"team\")\n" +
                ");")

        db.execSQL("CREATE INDEX \"record_index\" ON \"record\" (\n" +
                "\t\"barcode\",\n" +
                "\t\"scanned_barcode\",\n" +
                "\t\"warehouse\",\n" +
                "\t\"team\",\n" +
                "\t\"location\",\n" +
                "\t\"inventory\"\n" +
                ");")
        db.close()
    }

    fun loginCheck(user:String, password:String ){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM users WHERE username='$user' AND password='$password'"
        val cursor = db.rawQuery(query,null)

        if(cursor.moveToFirst()){
            user_auth = "true"
        }
        else{
            user_auth = "false"
        }
        db.close()
    }

    fun detailCheck(){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM details"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            wareHouse = cursor.getString(3)
            countId = cursor.getString(0)
            countType = cursor.getString(1)
            countSeq = cursor.getString(2)
            countDate = cursor.getString(4)
            wtoCheck()
        }
        else{
            Toast.makeText(context,"No data found",Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    fun wtoCheck(){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT DISTINCT[Oracle Item],[Item Description] FROM tbl_WTO WHERE Warehouse='$wareHouse' AND Flag='Y'"
        val query1 = "SELECT DISTINCT[Oracle Item], [Item Description] FROM tbl_WTO  WHERE [Oracle Item] Not IN  (SELECT barcode FROM record) and warehouse='$wareHouse' AND Flag='Y' "
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            totalWTO = cursor.count
        }
        else{
            totalWTO = 0
        }

        val cursor1 = db.rawQuery(query1,null)
        if(cursor1.moveToFirst()){
            uncountWto = cursor1.count
        }
        else{
            uncountWto = totalWTO
        }
        db.close()
    }

    fun stockCheck(){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*) as count FROM record "
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            totalStock = cursor.getInt(0)
        }
        else{
            totalStock = 0
        }

        val query1 = "SELECT SUM(qty) from record"
        val cursor1 = db.rawQuery(query1,null)
        if(cursor1.moveToFirst()){
            totalStockQty = cursor1.getInt(0)
        }
        else{
            totalStockQty = 0
        }
        db.close()
    }

    fun getInventory(){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT subinventory FROM locations GROUP BY subinventory"
        val cursor = db.rawQuery(query,null)
        subList.clear()
        if(cursor.moveToFirst()){
            subList.add("Select subinventory")
            do{
                subList.add(cursor.getString(0))
            }while (cursor.moveToNext())
        }
        else{
            Toast.makeText(context,"No Subinventory Found",Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    fun getLocation(location:String){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT location FROM locations WHERE subinventory='$location'"
        val cursor = db.rawQuery(query,null)
        locationList.clear()
        if(cursor.moveToFirst()){
            locationList.add("Select location")
            do{
                locationList.add(cursor.getString(0))
                println(locationList[0])
            }while (cursor.moveToNext())
        }
        else{
            Toast.makeText(context,"No Location Found",Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    fun checkBarcode(barcode:String,recode:String,subInventory: String,warehouse:String,location: String,qty:String,user: String,team:String,time:String){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT SKU FROM blackmarkets WHERE RecodeConfig='$recode'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            println("BLACK MARKET")
            var sku = ""
            if(cursor.getString(0).substring(0,1) == "W"){
                sku = cursor.getString(0).substring(0,7)
            }
            else{
                sku = cursor.getString(0).substring(0,6)
            }
            checkMaster(barcode,sku,subInventory,warehouse,location,qty,user,team,time)
        }
        else{
            val query1 = "SELECT SKU FROM recodes WHERE RecodeConfig='$barcode'"
            val cursor1 = db.rawQuery(query1,null)
            if(cursor1.moveToFirst()){
                println("RECODE")
                var sku = ""
                if(cursor1.getString(0).substring(0,1) == "W"){
                    sku = cursor1.getString(0).substring(0,7)
                }
                else{
                    if(cursor1.getString(0).length < 6){
                        sku = cursor1.getString(0).substring(0,cursor1.getString(0).length)
                    }
                    else{
                        sku = cursor1.getString(0).substring(0,6)
                    }
                }
                checkMaster(barcode,sku,subInventory,warehouse,location,qty,user,team,time)
            }
            else{
                var sku = ""
                if(barcode.substring(0,1) == "W"){
                    sku = barcode.substring(0,7)
                }
                else{
                    if(barcode.length < 6){
                        sku = barcode.substring(0,barcode.length)
                    }
                    else{
                        sku = barcode.substring(0,6)
                    }
                }
                checkMaster(barcode,sku,subInventory,warehouse,location,qty,user,team,time)
            }
        }
        db.close()
    }

    fun checkMaster(scanned_barcode: String,barcode:String,subInventory:String,warehouse:String,location: String,qty:String,user: String,team:String,time:String){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT [Item Description],[Oracle Item] FROM tbl_WTO WHERE [Oracle Item] like '$barcode%' AND Warehouse='$warehouse' AND Flag='Y'"
        val query3 = "SELECT [Item Description],[Oracle Item] FROM tbl_WTO WHERE [Item] like '$barcode%' AND Warehouse='$warehouse' AND Flag='Y'"
        val cursor = db.rawQuery(query,null)
        val cursor3 = db.rawQuery(query3,null)
        if(cursor.moveToFirst()){
            println("MASTER")
            description = cursor.getString(0)
            oracle = cursor.getString(1)

            addData(scanned_barcode, description,warehouse, oracle,subInventory,location,qty,user,team,time)

            val query2 = "SELECT count(*) as count FROM record WHERE inventory='$subInventory'"
            val cursor2 = db.rawQuery(query2,null)
            if(cursor2.moveToFirst()){
                items = cursor2.getInt(0)
            }
            else{
                items = 0
            }

            val query1 = "SELECT SUM(qty) from record WHERE inventory='$subInventory'"
            val cursor1 = db.rawQuery(query1,null)
            if(cursor1.moveToFirst()){
                itemsQty = cursor1.getInt(0)
            }
            else{
                itemsQty = 0
            }
            orcCheck = "yes"
        }
        else{
            if(cursor3.moveToFirst()){
                println("MASTER")
                description = cursor3.getString(0)
                oracle = cursor3.getString(1)

                addData(scanned_barcode, description,warehouse, oracle,subInventory,location,qty,user,team,time)

                val query2 = "SELECT count(*) as count FROM record WHERE inventory='$subInventory'"
                val cursor2 = db.rawQuery(query2,null)
                if(cursor2.moveToFirst()){
                    items = cursor2.getInt(0)
                }
                else{
                    items = 0
                }

                val query1 = "SELECT SUM(qty) from record WHERE inventory='$subInventory'"
                val cursor1 = db.rawQuery(query1,null)
                if(cursor1.moveToFirst()){
                    itemsQty = cursor1.getInt(0)
                }
                else{
                    itemsQty = 0
                }
                orcCheck = "yes"
            }
            else{
                val afd: AssetFileDescriptor = context.assets.openFd("buzz.wav")
                val player = MediaPlayer()
                player.setDataSource(
                        afd.getFileDescriptor(),
                        afd.getStartOffset(),
                        afd.getLength()
                )
                player.prepare()
                player.start()
                val handler = Handler()
                handler.postDelayed({ player.stop() }, 1 * 1000.toLong())
                Toast.makeText(context,"Item not found",Toast.LENGTH_LONG).show()
                oracle=""
                description=""
                currentQty=0
                orcCheck = "no"
                println("NOPE")
            }
        }
        db.close()
    }

    fun addData(scanned_barcode:String,description:String,warehouse:String,barcode:String,subInventory:String,location: String,qty:String,user: String,team:String,time:String){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val values = ContentValues()
        values.put("scanned_barcode",scanned_barcode)
        values.put("description",description)
        values.put("barcode",barcode)
        values.put("inventory",subInventory)
        values.put("warehouse",warehouse)
        values.put("location",location)
        values.put("qty",qty)
        values.put("user",user)
        values.put("team",team)
        values.put("time",time)
        println("ADD OK")
        val id=db.insertWithOnConflict("record", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        currentQty = Integer.parseInt(qty)
        if(id == -1L){
            currentQty = 0
            val query = "SELECT qty FROM record WHERE warehouse='$warehouse' AND inventory='$subInventory' AND location='$location' AND barcode='$barcode' AND team='$team'"
            val cursor = db.rawQuery(query,null)
            if(cursor.moveToFirst()){
                val currentqty = cursor.getInt(0)
                val updateQty = currentqty + Integer.parseInt(qty)
                currentQty = updateQty
                val values = ContentValues()
                values.put("qty",updateQty)
                db.update("record",values,"warehouse=? AND inventory=? AND location=? AND barcode=? AND team=?", arrayOf(warehouse,subInventory,location,barcode,team))
            }
            else{
                println("NONO")
            }
        }
    }

    fun checkCarton(recode:String,prtnum:String,subInventory:String,warehouse:String,location: String,qty:String,user: String,team:String,time:String){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT SKU FROM blackmarkets WHERE RecodeConfig='$recode'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            println("BLACK MARKET")
            var sku = ""
            if(cursor.getString(0).substring(0,1) == "W"){
                sku = cursor.getString(0).substring(0,7)
            }
            else{
                sku = cursor.getString(0).substring(0,6)
            }
            checkCar(sku,subInventory,warehouse,location,qty,user,team,time)
        }
        else{
            val query1 = "SELECT SKU FROM recodes WHERE RecodeConfig='$prtnum'"
            val cursor1 = db.rawQuery(query1,null)
            if(cursor1.moveToFirst()){
                println("RECODE")
                var sku = ""
                if(cursor1.getString(0).substring(0,1) == "W"){
                    sku = cursor1.getString(0).substring(0,7)
                }
                else{
                    if(cursor1.getString(0).length < 6){
                        sku = cursor1.getString(0).substring(0,cursor1.getString(0).length)
                    }
                    else{
                        sku = cursor1.getString(0).substring(0,6)
                    }
                }
                checkCar(sku,subInventory,warehouse,location,qty,user,team,time)
            }
            else{
                var sku = ""
                if(prtnum.substring(0,1) == "W"){
                    sku = prtnum.substring(0,7)
                }
                else{
                    if(prtnum.length < 6){
                        sku = prtnum.substring(0,prtnum.length)
                    }
                    else{
                        sku = prtnum.substring(0,6)
                    }
                }
                checkCar(sku,subInventory,warehouse,location,qty,user,team,time)
            }
        }
        db.close()
    }

    fun checkCar(prtnum:String,subInventory:String,warehouse:String,location: String,qty:String,user: String,team:String,time:String){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM uoms WHERE prtnum like '$prtnum%' ORDER BY prtnum ASC "
        val queryn = "SELECT [Item Description],[Oracle Item] FROM tbl_WTO WHERE [Oracle Item] like'$prtnum%' AND Warehouse='$warehouse' AND Flag='Y'"
        val cursorn = db.rawQuery(queryn,null)
        val cursor = db.rawQuery(query,null)
        if(cursorn.moveToFirst()) {
            description = cursorn.getString(0)
            println(description)
            oracle = cursorn.getString(1)

            println(oracle)
            if (cursor.moveToLast()) {
//                oracle = cursor.getString(1)
                cartonQty = cursor.getInt(4)
                val multipliedQty = cartonQty * Integer.parseInt(qty)

                addData(prtnum, description,warehouse, oracle, subInventory, location, multipliedQty.toString(), user, team, time)

                val query2 = "SELECT count(*) as count FROM record WHERE inventory='$subInventory'"
                val cursor2 = db.rawQuery(query2, null)
                if (cursor2.moveToFirst()) {
                    items = cursor2.getInt(0)
                } else {
                    items = 0
                }

                val query1 = "SELECT SUM(qty) from record WHERE inventory='$subInventory'"
                val cursor1 = db.rawQuery(query1, null)
                if (cursor1.moveToFirst()) {
                    itemsQty = cursor1.getInt(0)
                } else {
                    itemsQty = 0
                }

            } else {
                cartonQty = 1
                val multipliedQty = cartonQty * Integer.parseInt(qty)

                addData(prtnum, description,warehouse, oracle, subInventory, location, multipliedQty.toString(), user, team, time)

                val query2 = "SELECT count(*) as count FROM record WHERE inventory='$subInventory'"
                val cursor2 = db.rawQuery(query2, null)
                if (cursor2.moveToFirst()) {
                    items = cursor2.getInt(0)
                } else {
                    items = 0
                }

                val query1 = "SELECT SUM(qty) from record WHERE inventory='$subInventory'"
                val cursor1 = db.rawQuery(query1, null)
                if (cursor1.moveToFirst()) {
                    itemsQty = cursor1.getInt(0)
                } else {
                    itemsQty = 0
                }
//                val afd: AssetFileDescriptor = context.assets.openFd("buzz.wav")
//                val player = MediaPlayer()
//                player.setDataSource(
//                        afd.getFileDescriptor(),
//                        afd.getStartOffset(),
//                        afd.getLength()
//                )
//                player.prepare()
//                player.start()
//                val handler = Handler()
//                handler.postDelayed({ player.stop() }, 1 * 1000.toLong())
//                Toast.makeText(context, "Item not found", Toast.LENGTH_LONG).show()
//                oracle = ""
//                description = ""
//                currentQty = 0
//                println("NOPE")
            }
            cartoncheck = "yes"

        }
        else{
            cartoncheck = "no"
            Toast.makeText(context,"Item Not Found",Toast.LENGTH_SHORT).show()
        }
    }

    fun loadStockCount_1(subInventory: String?){
        ViewSC.clear()
        val db = context.openOrCreateDatabase(master, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM record WHERE inventory='$subInventory'"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val item = cursor.getString(0)
                val sub_inv = cursor.getString(1)
                val loc = cursor.getString(2)
                val desc = cursor.getString(9)
                val qty = cursor.getInt(3)

                println(item)

                val data = StockCountModal(item, sub_inv, loc, desc, qty)
                ViewSC.add(data)
            } while (cursor.moveToNext())
        } else {

        }
    }

    fun loadStockCount() {
        ViewSC.clear()
        val db = context.openOrCreateDatabase(master, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM record"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val item = cursor.getString(0)
                val sub_inv = cursor.getString(1)
                val loc = cursor.getString(2)
                val desc = cursor.getString(9)
                val qty = cursor.getInt(3)

                println(item)

                val data = StockCountModal(item, sub_inv, loc, desc, qty)
                ViewSC.add(data)
            } while (cursor.moveToNext())
        } else {

        }
    }

    fun loadSubInv() {
        ViewSub.clear()
        val db = context.openOrCreateDatabase(master, Context.MODE_PRIVATE, null)
        val query = "SELECT distinct inventory FROM record"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val subInv = cursor.getString(0)

                println(subInv)

                val data = SubInvModal(subInv)
                ViewSub.add(data)
            } while (cursor.moveToNext())
        } else {

        }
    }

    fun loadWTO(){
        ViewWTO.clear()
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT DISTINCT[Oracle Item],[Item Description] FROM tbl_WTO WHERE Warehouse='$wareHouse' AND Flag='Y' ORDER BY  [Oracle Item] ASC "
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val item = cursor.getString(0)
                val desc = cursor.getString(1)
                val data = WTOModal(item,desc)
                ViewWTO.add(data)
            }while(cursor.moveToNext())
            println(ViewWTO.size)
        }
        else{

        }
    }

    fun loadUncheckWTO(){
        ViewWTO.clear()
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT DISTINCT[Oracle Item], [Item Description] FROM tbl_WTO  WHERE [Oracle Item] Not IN  (SELECT barcode FROM record) and warehouse='$wareHouse' AND Flag='Y' ORDER BY [Oracle Item] ASC  "
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val item = cursor.getString(0)
                val desc = cursor.getString(1)
                val data = WTOModal(item,desc)
                ViewWTO.add(data)
            }while(cursor.moveToNext())
            println(ViewWTO.size)
        }
        else{

        }
    }

    fun search(barcode:String){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM record WHERE scanned_barcode='$barcode'"
        val cursor = db.rawQuery(query,null)

        if(cursor.moveToFirst()){
            ViewSC.clear()
            do{
                val item = cursor.getString(0)
                val sub_inv = cursor.getString(1)
                val loc = cursor.getString(2)
                val desc = cursor.getString(9)
                val qty = cursor.getInt(3)

                println(item)

                val data = StockCountModal(item, sub_inv, loc, desc, qty)
                ViewSC.add(data)
            }while (cursor.moveToNext())

        }
        else{
            Toast.makeText(context,"Item not found",Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    fun addNew(oracle:String, description:String, warehouse:String,subInventory:String,location: String,qty:String,user: String,team:String,time:String){
        val db = context.openOrCreateDatabase(master,Context.MODE_PRIVATE,null)
        var values = ContentValues()
        values.put("[Oracle Item]",oracle)
        values.put("[Item Description]",description)
        values.put("Warehouse",warehouse)
        db.insert("masters",null,values)

        var values1 = ContentValues()
        values1.put("scanned_barcode",oracle)
        values1.put("description",description)
        values1.put("barcode",oracle)
        values1.put("inventory",subInventory)
        values1.put("warehouse",warehouse)
        values1.put("location",location)
        values1.put("qty",qty)
        values1.put("user",user)
        values1.put("team",team)
        values1.put("time",time)
        db.insertWithOnConflict("record", null, values1, SQLiteDatabase.CONFLICT_IGNORE)
        println(qty)

        val query2 = "SELECT count(*) as count FROM record WHERE inventory='$subInventory'"
        val cursor2 = db.rawQuery(query2,null)
        if(cursor2.moveToFirst()){
            items = cursor2.getInt(0)
        }
        else{
            items = 0
        }

        val query1 = "SELECT SUM(qty) from record WHERE inventory='$subInventory'"
        val cursor1 = db.rawQuery(query1,null)
        if(cursor1.moveToFirst()){
            itemsQty = cursor1.getInt(0)
        }
        else{
            itemsQty = 0
        }

        db.close()

    }

}