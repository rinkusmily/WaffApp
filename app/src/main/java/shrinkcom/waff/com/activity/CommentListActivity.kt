package shrinkcom.waff.com.activity

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.LinearLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import shrinkcom.waff.com.R
import shrinkcom.waff.com.adapter.CommentListAdapter
import shrinkcom.waff.com.bean.Comment
import shrinkcom.waff.com.bean.DangerZone
import shrinkcom.waff.com.databinding.CommentListLayoutBinding
import shrinkcom.waff.com.databinding.SendCommentLayoutBinding
import shrinkcom.waff.com.interfaces.ServerRespondingListener
import shrinkcom.waff.com.serverconntion.OkHttpRequest
import java.lang.Exception
import java.util.ArrayList

class CommentListActivity : AppCompatActivity() {

    lateinit var commentLayoutBinding: CommentListLayoutBinding
    lateinit var activity: Activity
    lateinit var ojHttpRequest: OkHttpRequest
    lateinit var dangersId:String
    lateinit var commentArrayList:ArrayList<Comment>
    lateinit var commentListAdapter: CommentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        commentLayoutBinding = DataBindingUtil.setContentView(activity , R.layout.comment_list_layout)
        commentLayoutBinding.commentListActivity = this
        ojHttpRequest = OkHttpRequest(activity)

        dangersId = intent.getStringExtra("id")

        commentArrayList = ArrayList();

        commentListAdapter = CommentListAdapter(activity ,commentArrayList ,dangersId );



        commentLayoutBinding.backBtn.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

                finish()
            }

        })

        commentLayoutBinding.commentListview.layoutManager = LinearLayoutManager(this)
        commentLayoutBinding.commentListview.adapter = commentListAdapter

    }

    override fun onResume() {
        super.onResume()

        getCommentList()
    }


    public fun getCommentList()
    {

       val param:HashMap<String , Any>  =  HashMap<String , Any>()
        param["action"] = "getDangerComment"
        param["dangers_id"] = dangersId

        ojHttpRequest.getResponse(param , object :ServerRespondingListener(this){
            override fun onRespose(resultData: JSONObject) {

                try
                {
                    val dangerJsonArrayStr = resultData.getString("userData")
                    Log.e("dangerJsonArrayStr",dangerJsonArrayStr)
                    val list:ArrayList<Comment>  = Gson().fromJson<ArrayList<Comment>>(dangerJsonArrayStr, object :
                        TypeToken<List<Comment>>() {

                    }.type)
                    commentArrayList.clear()
                    commentArrayList.addAll(list)
                    commentListAdapter.notifyDataSetChanged()

                }
                catch (e:Exception)
                {

                }

            }

        })



    }


}