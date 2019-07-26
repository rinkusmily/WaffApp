package shrinkcom.waff.com.adapter;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import shrinkcom.waff.com.R;
import shrinkcom.waff.com.bean.Danger;
import shrinkcom.waff.com.interfaces.RecycleViewItemClickListner;


import java.util.ArrayList;



/**
 * Created by administrator on 31/7/17.
 */

public class SpinnerItemAdapter extends BaseAdapter implements android.widget.SpinnerAdapter
{


    ArrayList<String> spinnerArrayList ;
    Activity activity ;
    RecycleViewItemClickListner recycleViewItemClickListner ;






    public SpinnerItemAdapter(final Activity activity )
    {
        this.activity = activity ;


    }

    public void inislizedRecycleItemClickListener(RecycleViewItemClickListner recycleViewItemClickListner , ArrayList<Danger>dangers)
    {
        this.recycleViewItemClickListner = recycleViewItemClickListner ;
        spinnerArrayList = new ArrayList<>();


        for (Danger danger:dangers)
        {
            spinnerArrayList.add(danger.getName());
        }
    }


    public void inislizedRecycleItemClickListener(RecycleViewItemClickListner recycleViewItemClickListner , ArrayList<String>dangers , int a)
    {
        this.recycleViewItemClickListner = recycleViewItemClickListner ;
        spinnerArrayList = new ArrayList<>();
        spinnerArrayList.addAll(dangers);


    }



    @Override
    public int getCount() {
        return spinnerArrayList.size();
    }

    @Override
    public String getItem(int position) {
        return spinnerArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHolder viewHolder ;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.spinner_layout , null);
            viewHolder.text1 = (TextView) convertView.findViewById(R.id.title_tv);
            viewHolder.devider_line =  convertView.findViewById(R.id.devider_line);
            viewHolder.parent_layout =  convertView.findViewById(R.id.parent_layout);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);

        viewHolder.text1.setGravity(Gravity.CENTER_VERTICAL);

        viewHolder.devider_line.setVisibility(View.GONE);

        viewHolder.text1.setText(spinnerArrayList.get(position));




        return convertView;
    }
    public class ViewHolder  {
        TextView text1 ;
        LinearLayout devider_line ,parent_layout;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.spinner_layout , null);
            viewHolder.text1 = convertView.findViewById(R.id.title_tv);
            viewHolder.devider_line =  convertView.findViewById(R.id.devider_line);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.devider_line.setVisibility(View.VISIBLE);

        viewHolder.text1.setText(spinnerArrayList.get(position));


        return convertView;    }
}

