package shrinkcom.waff.com.util

import android.app.Activity
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import shrinkcom.waff.com.bean.UserData
import shrinkcom.waff.com.bean.WaffAddress

class SessionManager {

    var sharePhrefrence: SharedPreferences
    var editor: SharedPreferences.Editor;
    var activity: Activity

    constructor(activity: Activity) {
        this.activity = activity;
        sharePhrefrence = activity.getSharedPreferences("waff", Activity.MODE_PRIVATE)
        editor = sharePhrefrence.edit();
    }

     fun saveUser(user:UserData)
    {
        val userStr = Gson().toJson(user);
        editor.putString("userobj" , userStr)
        editor.commit()
    }

    fun  getUser() : UserData
    {
      val userStr = sharePhrefrence.getString("userobj" , "")

        val userData = Gson().fromJson<UserData>(userStr ,UserData::class.java)

        return userData ;

    }
    fun clear()
    {
        editor.clear()
        editor.commit()
    }




    public fun saveOridinAddress(latitude:Double , longitude:Double , address:String?)
    {
        val originAddress = WaffAddress()
        originAddress.latitude = latitude
        originAddress.longitude = longitude
        originAddress.googleAddress = address
        val originAddressStr = Gson().toJson(originAddress);
        editor.putString("originaddress" , originAddressStr)
        editor.commit()

    }

    public fun saveDestinationAddress(latitude:Double , longitude:Double , address:String)
    {
        val destinationAddress = WaffAddress()
        destinationAddress.latitude = latitude
        destinationAddress.longitude = longitude
        destinationAddress.googleAddress = address
        val destinationAddressStr = Gson().toJson(destinationAddress);
        editor.putString("destinationaddress" , destinationAddressStr)
        editor.commit()

    }


    fun  getoriginAddress() : WaffAddress
    {
        val originaddressStr = sharePhrefrence.getString("originaddress" , "")
        try {
            val userData:WaffAddress = Gson().fromJson(originaddressStr ,WaffAddress::class.java)
            return userData ;
        }
        catch (e:Exception)
        {
            val userData = WaffAddress()
            return userData ;
        }


    }





    fun getDestinationAddresss() : WaffAddress
    {

        val userStr = sharePhrefrence.getString("destinationaddress" , "")

        try {
            val userData:WaffAddress = Gson().fromJson(userStr ,WaffAddress::class.java)
            return  userData ;
        }
        catch (e:Exception)
        {
            val userData = WaffAddress()
            return userData ;
        }


    }
    fun getLanguage(): String? {

        luange = sharePhrefrence.getString("luange", "")

        if (TextUtils.isEmpty(luange)) {
            luange = "english"
        }

        return luange
    }

    fun setLanguage(luange: String) {

        editor.putString("luange", luange)
        editor.commit()
    }

    internal var luange: String? = null
}