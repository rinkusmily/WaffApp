package shrinkcom.waff.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import shrinkcom.waff.com.R;
import shrinkcom.waff.com.model.NotiModel;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
 
    private List<NotiModel> notiModels;
 
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
 
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textMessage);

        }
    }
 
 
    public NotificationAdapter(List<NotiModel> notiModels) {
        this.notiModels = notiModels;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotiModel notiModel = notiModels.get(position);
        holder.title.setText(notiModel.getNotiMessage());

    }
 
    @Override
    public int getItemCount() {
        return notiModels.size();
    }
}