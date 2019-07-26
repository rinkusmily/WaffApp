package shrinkcom.waff.com.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import shrinkcom.waff.com.bean.DangerZone
import java.lang.Exception

class SqliteDB(activty:Context) : SQLiteOpenHelper(activty,DATABASE_NAME  ,null , DATABASE_VERSION)
{


    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "WaffApp"

    }

    override fun onCreate(db: SQLiteDatabase?)
    {
        db?.execSQL("create table DangerStatus(id  integer primary key ," +
                " name text , " +
                "latitude double ," +
                " longitude double , " +
                "status int ,image text , userid text) ")



    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }


    public  fun saveDangerList( dangerArrayList: ArrayList<DangerZone>)
    {
        for (i in 0 until dangerArrayList.size) {

            try {
                val danger = dangerArrayList.get(i)
                 Log.e("dangersIds" ,"id "+danger.id)

                if (!isIdExits(danger.id))
                {
                    val contentValues = ContentValues()
                    contentValues.put("id" , danger.id);
                    contentValues.put("name" , danger.dangersName);
                    contentValues.put("latitude" , danger.latitude);
                    contentValues.put("longitude" , danger.longitude);
                    contentValues.put("image" , danger.image);
                    contentValues.put("userid" , danger.userId);
                    writableDatabase.insert("DangerStatus" , null , contentValues)
                }

            }


            catch (e:Exception)
            {
               Log.e("sqlExecep" , e.message)
            }
            catch (e: SQLiteConstraintException)
            {
                Log.e("sqlExecep" , e.message)
            }
            finally {
                Log.e("sqlno" , "finally")

            }


        }

    }



     fun isIdExits(dangerId:Int) : Boolean
    {
        val dangerIds = arrayOfNulls<String>(1)
        dangerIds[0] = ""+dangerId ;

        val cursor = readableDatabase.rawQuery("select *from DangerStatus where id = ?" , dangerIds)


        if (cursor == null)
        {
            return false ;
        }
        else
        {
           return cursor.moveToNext();
        }

    }


    fun getDangerList(): ArrayList<DangerZone>
    {
        val dangerArrayList = ArrayList<DangerZone>()

       val cursor = readableDatabase.rawQuery("select *from DangerStatus" , null)


        while (cursor.moveToNext())
        {

            try {
                val danger = DangerZone()
                danger.id = cursor.getInt(cursor.getColumnIndex("id"))
                danger.dangersName = cursor.getString(cursor.getColumnIndex("name"))
                danger.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))
                danger.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))
                danger.status = cursor.getInt(cursor.getColumnIndex("status"))
                danger.image = cursor.getString(cursor.getColumnIndex("image"))
                danger.userId = cursor.getString(cursor.getColumnIndex("userid"))

                dangerArrayList.add(danger)
            }
            catch (excep:SQLiteException)
            {
              Log.e("ecxep" ,  excep.localizedMessage);
            }
            catch (e:Exception)
            {

            }

        }

        return dangerArrayList ;
    }



    fun getDangerById(id:String): DangerZone
    {

    var   ids =  arrayOfNulls<String>(1)
        ids[0] = id;

        val dangerZone = DangerZone()

        val cursor = readableDatabase.rawQuery("select *from DangerStatus where id = ?" , ids)


        if (cursor.moveToNext())
        {

            try {
                dangerZone.id = cursor.getInt(cursor.getColumnIndex("id"))
                dangerZone.dangersName = cursor.getString(cursor.getColumnIndex("name"))
                dangerZone.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))
                dangerZone.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))
                dangerZone.status = cursor.getInt(cursor.getColumnIndex("status"))
                dangerZone.image = cursor.getString(cursor.getColumnIndex("image"))
                dangerZone.userId = cursor.getString(cursor.getColumnIndex("userid"))

            }
            catch (excep:SQLiteException)
            {
                Log.e("ecxep" ,  excep.localizedMessage);
            }
            catch (e:Exception)
            {

            }

        }

        return dangerZone ;
    }



    fun changeDangerStatus(danger: DangerZone)
    {
        val s = arrayOfNulls<String>(1)
        s[0] = ""+ danger.id
        val contentValues = ContentValues()
        contentValues.put("name" , danger.dangersName);
        contentValues.put("latitude" , danger.latitude);
        contentValues.put("longitude" , danger.longitude);
        contentValues.put("status" , danger.status);

        writableDatabase.update("DangerStatus" ,   contentValues , "id = ?" ,  s)

    }
    fun deleteDanger(ids: String)
    {
        val s = arrayOfNulls<String>(1)
        s[0] = ""+ ids
        writableDatabase.delete("DangerStatus"  , "id = ?" ,  s)

    }

    fun deleteDanger(dangerArrayList: ArrayList<DangerZone>)
    {
        val cursor = readableDatabase.rawQuery("select *from DangerStatus" , null)

        while (cursor.moveToNext())
        {
            val sqlID:String = cursor.getString(0);


            var isIDExits = false
            var ids = ""

           for (i in 0 until dangerArrayList.size)
           {
             val  dangerZone:DangerZone = dangerArrayList.get(i)

               ids = ""+dangerZone.id

               if (sqlID.equals(ids))
               {
                   isIDExits = true
                   break
               }


           }

            if (!isIDExits)
            {
                deleteDanger(sqlID)
            }


        }

    }


    fun changeDangerStatus(id: String , status :String )
    {
        val s = arrayOfNulls<String>(1)
        s[0] = ""+ id
        val contentValues = ContentValues()
        contentValues.put("status" , status);
        writableDatabase.update("DangerStatus" ,   contentValues , "id = ?" ,  s)

    }

    fun clearTable()
    {
        writableDatabase.delete("DangerStatus", null , null )
    }


}