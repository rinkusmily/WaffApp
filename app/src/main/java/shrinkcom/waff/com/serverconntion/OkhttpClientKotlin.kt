package shrinkcom.waff.com.serverconntion

import android.app.Activity
import android.app.ProgressDialog
import android.os.Handler
import android.os.Looper
import okhttp3.*
import org.json.JSONObject
import shrinkcom.waff.com.interfaces.ServerRespondingListener
import shrinkcom.waff.com.util.ShowMessage
import java.io.File
import java.io.IOException
import kotlin.collections.HashMap

class OkhttpClientKotlin
{
    lateinit var activity: Activity
    lateinit var request: Request
    internal var okHttpClient: OkHttpClient
    internal var showMessage: ShowMessage
    internal var progressDialog: ProgressDialog

    constructor(activity: Activity)
    {
        this.activity = activity
        okHttpClient = OkHttpClient()
        showMessage = ShowMessage(activity)
        progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
    }

    fun getResponse(param: HashMap<String?, Any?>, serverRespondingListener: ServerRespondingListener) {


        progressDialog.show()
        request = Request.Builder()

            .url(WebServices.BASE_URL)

            .post(getMultiPartRrquest(param))
            .build()

        /*HttpUrl.Builder httpBuider = HttpUrl.parse(WebServices.BASE_URL).newBuilder();
        httpBuider.addQueryParameter("action", "regi.setType(MultipartBody.FORM)ster");


         request = new Request.Builder().url(httpBuider.build()).build();*/


        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                serverRespondingListener.onError(""+e.message)
                progressDialog.dismiss()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                val myResponse = response.body()!!.string()


                val handler = Handler(Looper.getMainLooper())

                handler.post {
                    try {
                        val jsonObject = JSONObject(myResponse)
                        if (jsonObject.getInt("result") == 1) {
                            serverRespondingListener.onRespose(jsonObject)

                        } else {
                            serverRespondingListener.onError(jsonObject.getString("message"))

                        }


                    } catch (e: Exception) {
                        val msg = e.message ;
                        serverRespondingListener.onError(""+msg)

                    }
                }


                progressDialog.dismiss()
            }
        })
    }


    private fun getMultiPartRrquest(param: HashMap<String?, Any?>): MultipartBody {
        val multipartBody = MultipartBody.Builder()
        multipartBody.setType(MultipartBody.FORM)


        val keyValuePairList = param.entries

        val keyValuePair = keyValuePairList.iterator()

        while (keyValuePair.hasNext()) {
            val keyValue = keyValuePair.next()

            if (keyValue.value is File) {
                val file = keyValue.value as File
                multipartBody.addFormDataPart(
                    keyValue.key,
                    file.name,
                    RequestBody.create(MediaType.get(file.absolutePath), file)
                )
            } else {
                multipartBody.addFormDataPart(keyValue.key, keyValue.value.toString())
            }
        }





        return multipartBody.build()
    }


}