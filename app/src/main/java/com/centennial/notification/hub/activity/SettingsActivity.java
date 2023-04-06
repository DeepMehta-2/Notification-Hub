package com.centennial.notification.hub.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;
import com.centennial.notification.hub.Utils.GetInstalledAppList;
import com.centennial.notification.hub.model.GroupDataClass;
import com.centennial.notification.hub.model.InstalledAppDataClass;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences pref;
    String strDeleteInterval;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pref = getSharedPreferences("My_PREF", MODE_PRIVATE);
        strDeleteInterval = pref.getString("Delete Time Interval", null);

        textView = findViewById(R.id.deleteIntervalText);

        if (strDeleteInterval != null) {
            textView.setText(strDeleteInterval);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Create Group
    public void Create_Group(View view) {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_design, null);
        alert.setView(v);

        TextView title = v.findViewById(R.id.dialog_title);
        title.setText("Create Group");
        TextView heading = v.findViewById(R.id.dialog_heading);
        heading.setText("Group Name");
        final EditText et = v.findViewById(R.id.dialog_edittext);
        et.setHint("Enter group name");
        et.setVisibility(View.VISIBLE);

        v.findViewById(R.id.dialog_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String GroupName = et.getText().toString().trim();

                if (GroupName.isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "Group name is empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Create group name.
                    MySQLiteHelper helper = new MySQLiteHelper(SettingsActivity.this);
                    ArrayList<InstalledAppDataClass> arrayList = GetInstalledAppList.getAppList(SettingsActivity.this);
                    boolean isCreated = false;
                    if (arrayList != null && arrayList.size() > 0) {
                        for (InstalledAppDataClass i : arrayList) {
                            GroupDataClass dataClass = new GroupDataClass();
                            dataClass.setGroup_Name(GroupName);
                            dataClass.setGroup_App_Name(i.getAppName());
                            dataClass.setGroup_Pkg_Name(i.getPackageName());
                            dataClass.setIs_inGroup(0);
                            isCreated = helper.createGroup(dataClass);
                        }

                        if (isCreated) {
                            MainActivity.title_arrayList.add(new GroupDataClass(GroupName));
                            MainActivity.pagerAdapter.addFrag(new MainFragmentLayout(GroupName), GroupName);
                            MainActivity.pagerAdapter.notifyDataSetChanged();
                        }

                        Intent intent = new Intent(SettingsActivity.this, InstalledAppList.class);
                        intent.putExtra("groupname", GroupName);
                        startActivity(intent);
                    }
                }
                alert.dismiss();
            }
        });

        v.findViewById(R.id.dialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }

    // Edit Group
    public void Edit_Group(View view) {
        Intent intent = new Intent(this, ShowGroupList.class);
        startActivity(intent);
    }

    // Save notification selections
    public void Save_Notification(View view) {
        Intent intent = new Intent(this, Is_SaveNotification.class);
        startActivity(intent);
    }

    // Delete Data Automatically
    public void Auto_Delete(View view) {

        final AlertDialog alert = new AlertDialog.Builder(this).create();
        final View v = LayoutInflater.from(this).inflate(R.layout.auto_delete_dialog_layout, null);
        alert.setView(v);
        alert.setTitle("Select Time Interval");
        RadioGroup rg = v.findViewById(R.id.radio_group);
        final RadioButton rb0 = v.findViewById(R.id.rb0);
        final RadioButton rb1 = v.findViewById(R.id.rb1);
        final RadioButton rb2 = v.findViewById(R.id.rb2);
        final RadioButton rb3 = v.findViewById(R.id.rb3);

        if (strDeleteInterval != null) {
            if (strDeleteInterval.equals("7 Days")) {
                rb0.setChecked(true);
            }else if (strDeleteInterval.equals("15 Days")) {
                rb1.setChecked(true);
            } else if (strDeleteInterval.equals("30 Days")) {
                rb2.setChecked(true);
            } else {
                rb3.setChecked(true);
            }
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = v.findViewById(checkedId);
                strDeleteInterval = radioButton.getText().toString();

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Delete Time Interval", strDeleteInterval);
                editor.commit();

                textView.setText(strDeleteInterval);

                if (strDeleteInterval.equals("7 Days")) {
                    rb0.setChecked(true);
                } if (strDeleteInterval.equals("15 Days")) {
                    rb1.setChecked(true);
                } else if (strDeleteInterval.equals("30 Days")) {
                    rb2.setChecked(true);
                } else {
                    rb3.setChecked(true);
                }

                alert.dismiss();
            }
        });

        alert.show();
    }

    // Delete all notifications
    public void Delete_All_Notification(View view) {

        final AlertDialog alert = new AlertDialog.Builder(this).create();
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_design, null);
        alert.setView(v);

        TextView title = v.findViewById(R.id.dialog_title);
        title.setText("Delete saved notification?");
        TextView heading = v.findViewById(R.id.dialog_heading);
        heading.setText("All saved notifications will be deleted");

        Button delete = v.findViewById(R.id.dialog_confirm_btn);
        delete.setText("Delete");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySQLiteHelper helper = new MySQLiteHelper(SettingsActivity.this);
                if (helper.DeleteNotificationTable()) {
                    Toast.makeText(SettingsActivity.this, "All notification successfully deleted", Toast.LENGTH_SHORT).show();
                }
                alert.dismiss();
            }
        });

        v.findViewById(R.id.dialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}