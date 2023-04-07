package com.centennial.notification.hub.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.centennial.notification.hub.Utils.Common;
import com.centennial.notification.hub.activity.SaveNotificationActivity;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;
import com.centennial.notification.hub.model.SaveNotificationData;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Date;


public class SaveNotificationAdapter extends RecyclerView.Adapter<SaveNotificationAdapter.MyViewHolder> {

    Context c;
    public ArrayList<SaveNotificationData> arrayList;
    public ArrayList<SaveNotificationData> selected_List = new ArrayList<>();

    public SaveNotificationAdapter(Context c, ArrayList<SaveNotificationData> arrayList, ArrayList<SaveNotificationData> selected_List) {
        this.c = c;
        this.arrayList = arrayList;
        this.selected_List = selected_List;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(c).inflate(R.layout.savenotification_row, parent, false);
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
        } else {
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
//            holder.card.setCardBackgroundColor(ContextCompat.getColor(c, R.color.black));
        } else {
            holder.checkbox.setChecked(false);
//            holder.card.setCardBackgroundColor(ContextCompat.getColor(c, R.color.background_color));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        TextView title, message, bigText, date, time;
        ImageView bigImage, btnDeleteNotification;
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
            checkbox = itemView.findViewById(R.id.checkbox);
            bigImage = itemView.findViewById(R.id.bigImage);

            btnDeleteNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MySQLiteHelper db = new MySQLiteHelper(c);
                    SaveNotificationData data = new SaveNotificationData();
                    data.setId(arrayList.get(getAdapterPosition()).getId());
                    db.deleteNotification(data);
                    arrayList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }
    }
}
