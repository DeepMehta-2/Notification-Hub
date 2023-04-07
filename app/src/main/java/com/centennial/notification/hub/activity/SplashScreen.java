package com.centennial.notification.hub.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.centennial.notification.hub.other.MyBroadcastReceiver;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;
import com.centennial.notification.hub.Utils.GetInstalledAppList;
import com.centennial.notification.hub.model.GroupDataClass;
import com.centennial.notification.hub.model.InstalledAppDataClass;

import java.util.ArrayList;
import java.util.Calendar;

public class SplashScreen extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    int delaytime = 3000;
    SharedPreferences preferences;
    private MySQLiteHelper helper;
    private ArrayList<InstalledAppDataClass> arrayList;
    private int isFbLoad = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        preferences = getSharedPreferences("My_PREF", MODE_PRIVATE);
        helper = new MySQLiteHelper(SplashScreen.this);

        if (preferences.getString("Delete Time Interval", null) == null) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(this, MyBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 12345, myIntent, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 1);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent); // Repeat every 24 hours
        }

        new BackGroundWork().execute();
    }

    private class BackGroundWork extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            // Get installed app list
            arrayList = GetInstalledAppList.getAppList(SplashScreen.this);

            Boolean isFirstLaunch = preferences.getBoolean("isFirstLaunch", false);
            if (arrayList != null && arrayList.size() > 0) {
                if (isFirstLaunch) {
                    // Create all groups (This will run every time when app is open after first launch)
                    All();
                    delaytime = 3000;
                } else {
                    // Create default groups (This will call only on first launch)
                    CreateDefaultGroups();
                    delaytime = 7500;
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    // If the user did not turn the notification listener service on we prompt him to do so
                    if (!isNotificationServiceEnabled()) {
                        Intent intent = new Intent(SplashScreen.this, PermissionActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }, delaytime);

        }

    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     *
     * @return True if eanbled, false otherwise.
     */
    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void CreateDefaultGroups() {
        All();
        Chat();
        Social();
        Finance();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFirstLaunch", true);
        editor.putString("Delete Time Interval", "7 Days");
        editor.commit();
    }

    private void All() {
        for (InstalledAppDataClass i : arrayList) {
            GroupDataClass dataClass = new GroupDataClass();
            dataClass.setGroup_Name("All");
            dataClass.setGroup_App_Name(i.getAppName());
            dataClass.setGroup_Pkg_Name(i.getPackageName());
            dataClass.setIs_inGroup(1);
            helper.createGroup(dataClass);
        }
    }

    private void Chat() {
        for (InstalledAppDataClass i : arrayList) {
            String CheckPackageName = i.getPackageName();

            GroupDataClass dataClass = new GroupDataClass();
            dataClass.setGroup_Name("Chat");
            dataClass.setGroup_App_Name(i.getAppName());
            dataClass.setGroup_Pkg_Name(i.getPackageName());

            if (CheckPackageName.equals("com.whatsapp")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.bsb.hike")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.facebook.mlite")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.facebook.orca")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.skype.raider")) {
                dataClass.setIs_inGroup(1);
            } else {
                dataClass.setIs_inGroup(0);
            }

            helper.createGroup(dataClass);
        }
    }

    private void Social() {
        for (InstalledAppDataClass i : arrayList) {
            String CheckPackageName = i.getPackageName();

            GroupDataClass dataClass = new GroupDataClass();
            dataClass.setGroup_Name("Social");
            dataClass.setGroup_App_Name(i.getAppName());
            dataClass.setGroup_Pkg_Name(i.getPackageName());

            if (CheckPackageName.equals("com.facebook.katana")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.facebook.lite")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.snapchat.android")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.instagram.android")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.twitter.android")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.linkedin.android")) {
                dataClass.setIs_inGroup(1);
            } else {
                dataClass.setIs_inGroup(0);
            }

            helper.createGroup(dataClass);
        }
    }

    private void Finance() {
        for (InstalledAppDataClass i : arrayList) {
            String CheckPackageName = i.getPackageName();

            GroupDataClass dataClass = new GroupDataClass();
            dataClass.setGroup_Name("Finance");
            dataClass.setGroup_App_Name(i.getAppName());
            dataClass.setGroup_Pkg_Name(i.getPackageName());

            if (CheckPackageName.equals("net.one97.paytm")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.samsung.android.spay")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.paypal.android.p2pmobile")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.phonepe.app")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("in.org.npci.upiapp")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.google.android.apps.nbu.paisa.user")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.mobikwik_new")) {
                dataClass.setIs_inGroup(1);
            } else if (CheckPackageName.equals("com.freecharge.android")) {
                dataClass.setIs_inGroup(1);
            } else {
                dataClass.setIs_inGroup(0);
            }

            helper.createGroup(dataClass);
        }
    }
}
