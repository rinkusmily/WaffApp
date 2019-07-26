package shrinkcom.waff.com.adapter

import android.app.Activity
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
import shrinkcom.waff.com.bean.DangerZone
import shrinkcom.waff.com.databinding.MydangerListLayoutBinding
import shrinkcom.waff.com.interfaces.RecycleViewItemClickListner
import shrinkcom.waff.com.util.CircleBitmapTranslation
import shrinkcom.waff.com.util.GeoCoderDataParser

class MyDangerListAdapter() : RecyclerView.Adapter<MyDangerListAdapter.MyDangerViewHolder>() {

    lateinit var activity: Activity;
    lateinit var dangerZoneList: ArrayList<DangerZone>;
    lateinit var recycleViewItemClickListner:RecycleViewItemClickListner;

    constructor(activity: Activity, dangerZoneList: ArrayList<DangerZone> ,recycleViewItemClickListner:RecycleViewItemClickListner) : this() {
        this.activity = activity
        this.dangerZoneList = dangerZoneList
        this.recycleViewItemClickListner = recycleViewItemClickListner

    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): MyDangerViewHolder {

        val mydangerListLayoutBinding = DataBindingUtil.inflate<MydangerListLayoutBinding>( LayoutInflater.from(activity) , R.layout.mydanger_list_layout ,viewGroup , false )

        return MyDangerViewHolder(mydangerListLayoutBinding)
    }

    override fun getItemCount(): Int {

      return  dangerZoneList.size;

    }


    override fun onBindViewHolder(myDangerViewHolder: MyDangerViewHolder, pos: Int) {

        myDangerViewHolder.mydangerListLayoutBinding.dangerTextView.setText(dangerZoneList.get(pos).dangersName);

        try {

            val address = GeoCoderDataParser.getAddressFromLatitudeLongitude(activity, dangerZoneList.get(pos).latitude, dangerZoneList.get(pos).longitude)
            myDangerViewHolder.mydangerListLayoutBinding.dangerAddressTextview.setText(address)
            val urlstr = "https://shrinkcom.com/waff/admin/images/dangerzone/" + dangerZoneList.get(pos).image


            var requestOptions1 = RequestOptions()
            requestOptions1 = requestOptions1.error(R.drawable.carpool_home_work_illu)
            requestOptions1 = requestOptions1.placeholder(R.drawable.carpool_home_work_illu)
            requestOptions1 = requestOptions1.transform(CircleBitmapTranslation(activity))

            Glide.with(activity)
                .setDefaultRequestOptions(requestOptions1).asBitmap()
                .load(""+urlstr )
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
                .into(myDangerViewHolder.mydangerListLayoutBinding.dangerImageView)

                  myDangerViewHolder.mydangerListLayoutBinding.parentLayout.setOnClickListener(object : View.OnClickListener{
                      override fun onClick(v: View?) {

                          recycleViewItemClickListner.onItemClick(pos , 9)
                      }

                  })





        }
        catch (E:Exception)
        {

        }

    }


    public class MyDangerViewHolder(mydangerListLayoutBinding: MydangerListLayoutBinding) :
        RecyclerView.ViewHolder(mydangerListLayoutBinding.root) {
        var mydangerListLayoutBinding: MydangerListLayoutBinding;

        init {
            this.mydangerListLayoutBinding = mydangerListLayoutBinding;
        }
    }
}



