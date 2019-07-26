package shrinkcom.waff.com.adapter

import android.app.Activity
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import shrinkcom.waff.com.R
import shrinkcom.waff.com.bean.Danger
import shrinkcom.waff.com.databinding.DangerListLayoutBinding
import shrinkcom.waff.com.interfaces.RecycleViewItemClickListner
import shrinkcom.waff.com.util.DeviceInfo


class DangerListAdapter() : RecyclerView.Adapter<DangerListAdapter.DangerListViewHolder>() {


    lateinit var acvity: Activity
    lateinit var dangerArrayList: ArrayList<Danger>
    lateinit var recycleViewItemClickListner: RecycleViewItemClickListner ;

    constructor(activity: Activity, dangerArrayList: ArrayList<Danger>) : this() {

        this.acvity = activity;
        this.dangerArrayList = dangerArrayList;

    }


    public fun addRecycleViewListener(recycleViewItemClickListner: RecycleViewItemClickListner)
    {
      this.recycleViewItemClickListner = recycleViewItemClickListner ;
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, pos: Int): DangerListViewHolder {

        val dangerListLayoutBinding = DataBindingUtil.inflate<DangerListLayoutBinding>(
            LayoutInflater.from(acvity),
            R.layout.danger_list_layout,
            viewGroup,
            false
        )

        return DangerListViewHolder(dangerListLayoutBinding);
    }

    override fun getItemCount(): Int {

        return dangerArrayList.size
    }

    override fun onBindViewHolder(dangerListViewholder: DangerListViewHolder, position: Int) {

        val danger = dangerArrayList.get(position)
        val width = DeviceInfo.getDeviceWidth(acvity) - 40

        var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(width / 100 * 22, width / 100 * 22);

        dangerListViewholder.dangerListLayoutBinding.dangerIv.setLayoutParams(params);

        if (position % 3 == 1) {
            var rootparams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams((width * 34 / 100.0).toInt(), (width * 36 / 100.0).toInt());
            dangerListViewholder.dangerListLayoutBinding.rootLayout.setLayoutParams(rootparams);

        } else {
            var rootparams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(((width * 33 / 100.0)).toInt(), (width * 36 / 100.0).toInt());
            dangerListViewholder.dangerListLayoutBinding.rootLayout.setLayoutParams(rootparams);

        }

        dangerListViewholder.dangerListLayoutBinding.dangerTv.setText(danger.name);

        Log.e("url" ,""+ danger.name)

        Glide.with(acvity)
            .load("https://shrinkcom.com/waff/admin/images/dangerzone/"+ danger.image)
            .into(dangerListViewholder.dangerListLayoutBinding.dangerIv);
        dangerListViewholder.dangerListLayoutBinding.dangerTv.setTextColor(acvity.getResources().getColor(shrinkcom.waff.com.R.color.white));
        dangerListViewholder.dangerListLayoutBinding.dangerTv.setTextColor(acvity.getResources().getColor(R.color.white));

        dangerListViewholder.dangerListLayoutBinding.rootLayout.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                recycleViewItemClickListner.onItemClick(position,0)
            }


        } )


    }

    public class DangerListViewHolder(dangerListLayoutBinding: DangerListLayoutBinding) :
        RecyclerView.ViewHolder(dangerListLayoutBinding.root) {

        var dangerListLayoutBinding: DangerListLayoutBinding;

        init {
            this.dangerListLayoutBinding = dangerListLayoutBinding;
        }

    }

}