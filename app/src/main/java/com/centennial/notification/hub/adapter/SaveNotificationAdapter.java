package com.centennial.notification.hub.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.centennial.notification.hub.R;
import com.centennial.notification.hub.Utils.Common;
import com.centennial.notification.hub.activity.MapsActivity;
import com.centennial.notification.hub.activity.SaveNotificationActivity;
import com.centennial.notification.hub.model.SaveNotificationData;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Date;


public class SaveNotificationAdapter extends RecyclerView.Adapter<SaveNotificationAdapter.MyViewHolder> {

    Context context;
    public ArrayList<SaveNotificationData> arrayList;
    public ArrayList<SaveNotificationData> selected_List = new ArrayList<>();

    String appName;
    byte[] appIcon;

    public SaveNotificationAdapter(Context context, ArrayList<SaveNotificationData> arrayList, ArrayList<SaveNotificationData> selected_List, String appName, byte[] appIcon) {
        this.context = context;
        this.arrayList = arrayList;
        this.selected_List = selected_List;
        this.appName = appName;
        this.appIcon = appIcon;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.savenotification_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        SaveNotificationData data = arrayList.get(position);

        Date timestamp = new Date(arrayList.get(position).getDate());
//        String date = DateFormat.format("dd/MM/yy hh:mm a", timestamp).toString();
        String date = DateFormat.format("dd/MM/yy", timestamp).toString();
        String time = DateFormat.format("hh:mm a", timestamp).toString();
        holder.date.setText(date);
        holder.time.setText(time);

        holder.title.setText(arrayList.get(position).getTitle());

//        Log.e("test latitude", String.valueOf(arrayList.get(position).getLatitude()));
//        Log.e("test longitude", String.valueOf(arrayList.get(position).getLongitude()));

        if (data.getMessage() != null && !data.getMessage().isEmpty()) {
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(data.getMessage());

        } else {
            holder.message.setVisibility(View.GONE);
        }

        if (data.getBig_text() != null && !data.getBig_text().isEmpty()) {
            holder.bigText.setVisibility(View.VISIBLE);
            holder.bigText.setText(data.getBig_text());
        } else {
            holder.bigText.setVisibility(View.GONE);
        }

        if (data.getLarge_icon() != null) {
            holder.smallIconImg.setImageBitmap(Common.getImage(data.getLarge_icon()));
        } else if (data.getSmall_icon() != null) {
            holder.smallIconImg.setImageBitmap(Common.getImage(data.getSmall_icon()));
        }

        if (data.getBig_image() != null) {
            holder.bigImage.setVisibility(View.VISIBLE);
            holder.bigImage.setImageBitmap(Common.getImage(data.getBig_image()));
        } else {
            holder.bigImage.setVisibility(View.GONE);
        }

        // For multi select row in recycler view
        if (SaveNotificationActivity.isMultiSelect) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.btnDeleteNotification.setVisibility(View.GONE);
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.btnDeleteNotification.setVisibility(View.VISIBLE);
        }

        // For multi select row in recycler view
        if (selected_List.contains(arrayList.get(position))) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        TextView title, message, bigText, date, time;
        ImageView bigImage, btnDeleteNotification, mapLocation;
        CircularImageView smallIconImg;
        CheckBox checkbox;

        public MyViewHolder(View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.card);
            title = itemView.findViewById(R.id.title);
            message = itemView.findViewById(R.id.message);
            bigText = itemView.findViewById(R.id.bigText);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            smallIconImg = itemView.findViewById(R.id.smallIconImg);
            btnDeleteNotification = itemView.findViewById(R.id.btnDeleteNotification);
            mapLocation = itemView.findViewById(R.id.mapLocation);
            checkbox = itemView.findViewById(R.id.checkbox);
            bigImage = itemView.findViewById(R.id.bigImage);

            btnDeleteNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MySQLiteHelper db = new MySQLiteHelper(context);
                    SaveNotificationData data = new SaveNotificationData();
                    data.setId(arrayList.get(getAdapterPosition()).getId());
                    db.deleteNotification(data);
                    arrayList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            mapLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("appName", appName);
                    intent.putExtra("appIcon", appIcon);
                    intent.putExtra("latitude", arrayList.get(getAdapterPosition()).getLatitude());
                    intent.putExtra("longitude", arrayList.get(getAdapterPosition()).getLongitude());
                    context.startActivity(intent);
                }
            });
        }
    }
}
