package shrinkcom.waff.com.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import shrinkcom.waff.com.bean.DangerZone
import shrinkcom.waff.com.interfaces.ServerRespondingListenerWithoutMessage
import shrinkcom.waff.com.serverconntion.OkHttpRequest
import shrinkcom.waff.com.serverconntion.WebServices
import shrinkcom.waff.com.util.SqliteDB
import java.util.ArrayList

class SaveDataSqliteBroadcast : BroadcastReceiver() {

    lateinit var okHttpRequest: OkHttpRequest
    lateinit var sqliteDB: SqliteDB

    override fun onReceive(context: Context, intent: Intent?) {

        okHttpRequest = OkHttpRequest();
        sqliteDB = SqliteDB(context);
        requestForgetDangerList()


     Log.e("datassss" ,"Save Data")
    }


     fun requestForgetDangerList()
    {

        okHttpRequest.getResponseGet(WebServices.getDangerZone , object : ServerRespondingListenerWithoutMessage
        {
            override fun onRespose(resultData: JSONObject) {

                Log.e("datassss" , resultData.toString())



                try {
                    val dangerJsonArrayStr = resultData.getString("userData")
                    val dangerZoneArrayList = Gson().fromJson<ArrayList<DangerZone>>(dangerJsonArrayStr, object :
                        TypeToken<List<DangerZone>>() {

                    }.type)
                    sqliteDB.saveDangerList(dangerZoneArrayList)
                    sqliteDB.deleteDanger(dangerZoneArrayList)



                    //  sqliteDB.saveDangerList(Dashboard.this , dangerZoneArrayList);

                } catch (e1: Exception) {
                    Log.e("Excep", e1.message)
                }

            }

        });
    }


}