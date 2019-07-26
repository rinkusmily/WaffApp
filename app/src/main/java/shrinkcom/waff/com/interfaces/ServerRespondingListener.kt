package shrinkcom.waff.com.interfaces

import android.app.Activity
import org.json.JSONObject
import shrinkcom.waff.com.util.ShowMessage

abstract class ServerRespondingListener {

      var activity: Activity
      var showMessage:ShowMessage


    public constructor(activity: Activity)
    {
        this.activity = activity ;
        showMessage = ShowMessage(activity)
    }

  abstract  public fun onRespose(resultData : JSONObject);

     public open fun  onError(error:String)
     {
                                                                                                                                                showMessage.showDialogMessage(error)
     }
}