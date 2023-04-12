package com.centennial.notification.hub.adapter;

import android.content.Context;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;
import com.centennial.notification.hub.Utils.Common;
import com.centennial.notification.hub.activity.Is_SaveNotification;
import com.centennial.notification.hub.model.AppCategory;
import com.centennial.notification.hub.model.InstalledAppDataClass;

import java.util.ArrayList;

public class Is_SaveNotificationAdapter extends RecyclerView.Adapter<Is_SaveNotificationAdapter.MyViewHolder> {

    Context c;
    ArrayList<InstalledAppDataClass> list;

    public Is_SaveNotificationAdapter(Context c, ArrayList<InstalledAppDataClass> list) {
        this.c = c;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.installed_app_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.appName_Switch.setText(list.get(position).getAppName());
        holder.appIcon.setImageDrawable(list.get(position).getAppIcon());

        MySQLiteHelper helper = new MySQLiteHelper(c);
        int i = helper.CheckIsSaved(list.get(position).getPackageName());
        if (i == 0) {
            holder.appName_Switch.setChecked(false);
        } else if (i == 1) {
            holder.appName_Switch.setChecked(true);
        } else if (i == 2) {
            AppCategory category = new AppCategory();
            category.setAppName(list.get(position).getAppName());
            category.setAppPackageName(list.get(position).getPackageName());
            category.setAppImg(Common.getBytes(list.get(position).getAppIcon()));
            Long id = helper.addCategory(category);

            holder.appName_Switch.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        SwitchCompat appName_Switch;
        ImageView appIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.installed_appIcon);
            appName_Switch = itemView.findViewById(R.id.installed_appName_switch);
            appName_Switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int isSavedValue;

                    if (appName_Switch.isChecked()) {
                        isSavedValue = 1;
                        Is_SaveNotification.SavedCount += 1;
                    } else {
                        isSavedValue = 0;
                        Is_SaveNotification.SavedCount -= 1;
                    }

                    MySQLiteHelper sqLiteHelper = new MySQLiteHelper(c);
                    String.valueOf(sqLiteHelper.UpdateIsSave(list.get(getAdapterPosition()).getPackageName(), isSavedValue));

                    Is_SaveNotification.savedCountTextView.setText("Apps saved " + Is_SaveNotification.SavedCount + "/" + getItemCount());
                }
            });
        }
    }
}
