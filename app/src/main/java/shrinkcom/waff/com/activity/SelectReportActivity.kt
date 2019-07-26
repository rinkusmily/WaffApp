package shrinkcom.waff.com.activity

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.mapboxsdk.geometry.LatLng
import org.json.JSONObject
import shrinkcom.waff.com.R
import shrinkcom.waff.com.adapter.DangerListAdapter
import shrinkcom.waff.com.bean.Danger
import shrinkcom.waff.com.databinding.SelectReportLayoutBinding
import shrinkcom.waff.com.interfaces.RecycleViewItemClickListner
import shrinkcom.waff.com.interfaces.ServerRespondingListener
import shrinkcom.waff.com.serverconntion.OkHttpRequest
import shrinkcom.waff.com.serverconntion.WebServices
import shrinkcom.waff.com.util.MarginItemDecoration
import java.util.ArrayList
import java.util.HashMap

class SelectReportActivity : AppCompatActivity()
{
   lateinit var selectReportLayoutBinding:SelectReportLayoutBinding
    lateinit var okHttpRequest:OkHttpRequest
    lateinit var dangerListAdapter:DangerListAdapter
    lateinit var dangerArrayList: ArrayList<Danger>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectReportLayoutBinding = DataBindingUtil.setContentView(this , R.layout.select_report_layout )
        selectReportLayoutBinding.selectReportActivity = this
        okHttpRequest = OkHttpRequest(this)
        dangerArrayList = ArrayList<Danger>();

        dangerListAdapter = DangerListAdapter(this , dangerArrayList);

        dangerListAdapter.addRecycleViewListener(object : RecycleViewItemClickListner(){
            override fun onItemClick(pos: Int, status: Int) {

                val intent = Intent()
                intent.putExtra("danger_id" , dangerArrayList.get(pos).id)
                intent.putExtra("danger_name" , dangerArrayList.get(pos).name)
                setResult(Activity.RESULT_OK ,intent)
                finish()
            }

        })

        selectReportLayoutBinding.dangerListView.adapter = dangerListAdapter ;

        selectReportLayoutBinding.dangerListView.layoutManager = GridLayoutManager(this, 3);

        selectReportLayoutBinding.dangerListView.addItemDecoration(
            MarginItemDecoration(
            resources.getDimension(R.dimen.tendps).toInt())
        )
    }

    override fun onResume() {
        super.onResume()

        getDangersListFromServer()
    }

    fun getDangersListFromServer(/*point: LatLng*/) {
        val param = HashMap<String, Any>()

        okHttpRequest.getResponseGet(WebServices.getDangers, object : ServerRespondingListener(this) {
            override fun onRespose(resultData: JSONObject) {

                Log.e("resultData", "" + resultData)

                try {
                    this@SelectReportActivity.dangerArrayList.clear()
                    val dangerJsonArrayStr = resultData.getString("userData")
                    val dangerArrayList =
                        Gson().fromJson<ArrayList<Danger>>(dangerJsonArrayStr, object : TypeToken<List<Danger>>() {

                        }.type)

                    this@SelectReportActivity.dangerArrayList.addAll(dangerArrayList)

                    dangerListAdapter.notifyDataSetChanged()
                    //showDialogForDangerListSpinner(point, yourArray)
                } catch (e: Exception) {

                }

            }
        })
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        setResult(Activity.RESULT_CANCELED)
    }


    public fun closeActivity() {

        finish()
    }
}