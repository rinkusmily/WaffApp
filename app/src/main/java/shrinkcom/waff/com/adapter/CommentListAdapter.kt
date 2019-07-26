package shrinkcom.waff.com.adapter

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import shrinkcom.waff.com.R
import shrinkcom.waff.com.activity.SendCommentActivity
import shrinkcom.waff.com.bean.Comment
import shrinkcom.waff.com.databinding.CommentListItemLayoutBinding
import shrinkcom.waff.com.util.CircleBitmapTranslation

class CommentListAdapter(activity: Activity , arrayList: ArrayList<Comment> , dangerZoneId:String) :RecyclerView.Adapter<CommentListAdapter.CommentListViewHolder>() {


   var  activity: Activity ;
     var arrayList: ArrayList<Comment> ;
    var  dangerZoneId:String ="" ;
    lateinit var requestOptions:RequestOptions;

    init {
        this.activity = activity
        this.arrayList = arrayList
        this.dangerZoneId = dangerZoneId

    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): CommentListViewHolder {


        val commentListItemLayoutBinding:CommentListItemLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity),
            R.layout.comment_list_item_layout , viewGroup,false)

        return CommentListViewHolder(commentListItemLayoutBinding) ;

    }

    override fun getItemCount(): Int {

        return arrayList.size
    }

    override fun onBindViewHolder(holder: CommentListViewHolder, pos: Int) {

        holder.commentListLayoutBinding.commentTv.setText(""+arrayList.get(pos).comment)

        holder.commentListLayoutBinding.userNameTv.setText(""+arrayList.get(pos).username)

        try {
            requestOptions = RequestOptions()
            requestOptions = requestOptions.transform(CircleBitmapTranslation(activity))
            requestOptions = requestOptions.placeholder(R.drawable.user)
            requestOptions = requestOptions.error(R.drawable.user)


            val url = "https://shrinkcom.com/waff/" + arrayList.get(pos).userImage


            Glide.with(activity)
                .setDefaultRequestOptions(requestOptions).asBitmap()
                .load(url)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Bitmap>,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        model: Any,
                        target: Target<Bitmap>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }
                })
                .into(holder.commentListLayoutBinding.userImageview)
        }
        catch (E:Exception)
        {

        }


        holder.commentListLayoutBinding.parente.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                val intent = Intent(activity, SendCommentActivity::class.java)
                intent.putExtra("snippet",""+dangerZoneId)
                intent.putExtra("user_id", ""+arrayList.get(pos).userId)

                activity.startActivity(intent)
            }

        })
    }


    public class CommentListViewHolder(commentListLayoutBinding: CommentListItemLayoutBinding) :RecyclerView.ViewHolder(commentListLayoutBinding.root)
    {
        lateinit var commentListLayoutBinding: CommentListItemLayoutBinding ;
        init {
            this.commentListLayoutBinding = commentListLayoutBinding ;
        }

    }
}



