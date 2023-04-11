package com.centennial.notification.hub.adapter;

import android.content.Context;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;

import com.centennial.notification.hub.model.GroupDataClass;
import com.centennial.notification.hub.model.InstalledAppDataClass;

import java.util.ArrayList;

public class InstalledAppAdapter extends RecyclerView.Adapter<InstalledAppAdapter.MyViewHolder> {

    Context c;
    ArrayList<InstalledAppDataClass> list;
    String group_name;

    public InstalledAppAdapter(Context c, ArrayList<InstalledAppDataClass> list, String group_name) {
        this.c = c;
        this.list = list;
        this.group_name = group_name;
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

        if (group_name != null && !group_name.isEmpty()) {
            MySQLiteHelper helper = new MySQLiteHelper(c);
            boolean b = helper.checkGrouplist(group_name, list.get(position).getPackageName());
            if (b) {
                holder.appName_Switch.setChecked(true);
            } else {
                holder.appName_Switch.setChecked(false);
            }
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

                    if (appName_Switch.isChecked()) {

                        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(c);
                        GroupDataClass dataClass = new GroupDataClass();
                        dataClass.setGroup_Name(group_name);
                        dataClass.setGroup_Pkg_Name(list.get(getAdapterPosition()).getPackageName());
                        dataClass.setIs_inGroup(1);
                        boolean isUpdate = sqLiteHelper.updateAppGroupStatus(dataClass);

                        if (!isUpdate) {
                            sqLiteHelper.createGroup(dataClass);
                        }
                    } else {
                        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(c);
                        GroupDataClass dataClass = new GroupDataClass();
                        dataClass.setGroup_Name(group_name);
                        dataClass.setGroup_Pkg_Name(list.get(getAdapterPosition()).getPackageName());
                        dataClass.setIs_inGroup(0);
                        boolean isDeleteable = sqLiteHelper.updateAppGroupStatus(dataClass);

                        if (!isDeleteable) {
                            appName_Switch.setChecked(true);
                            Toast.makeText(c, "You can't delete the last item from the group", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }
}
