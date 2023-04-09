package com.centennial.notification.hub.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.centennial.notification.hub.R;
import com.centennial.notification.hub.Utils.GetInstalledAppList;
import com.centennial.notification.hub.model.GroupDataClass;
import com.centennial.notification.hub.model.InstalledAppDataClass;
import com.centennial.notification.hub.other.MyBroadcastReceiver;
import com.centennial.notification.hub.other.MySQLiteHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class SplashScreen extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    SharedPreferences preferences;
    private MySQLiteHelper helper;
    private ArrayList<InstalledAppDataClass> arrayList;

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

            boolean isAppLaunchedFirstTime = preferences.getBoolean("isAppLaunchedFirstTime", true);
            if (arrayList != null && arrayList.size() > 0) {
                if (isAppLaunchedFirstTime) {
                    CreateDefaultGroups();
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
            }, 3000);

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
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
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
        editor.putBoolean("isAppLaunchedFirstTime", false);
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

            switch (CheckPackageName) {
                case "com.whatsapp":
                case "com.facebook.mlite":
                case "com.facebook.orca":
                case "com.skype.raider":
                    dataClass.setIs_inGroup(1);
                    break;
                default:
                    dataClass.setIs_inGroup(0);
                    break;
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

            switch (CheckPackageName) {
                case "com.facebook.katana":
                case "com.facebook.lite":
                case "com.snapchat.android":
                case "com.instagram.android":
                case "com.twitter.android":
                case "com.linkedin.android":
                    dataClass.setIs_inGroup(1);
                    break;
                default:
                    dataClass.setIs_inGroup(0);
                    break;
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

            switch (CheckPackageName) {
                case "net.one97.paytm":
                case "com.freecharge.android":
                case "com.samsung.android.spay":
                case "com.paypal.android.p2pmobile":
                case "com.phonepe.app":
                case "com.google.android.apps.nbu.paisa.user":
                case "com.google.android.apps.walletnfcrel":
                case "com.mobikwik_new":
                    dataClass.setIs_inGroup(1);
                    break;
                default:
                    dataClass.setIs_inGroup(0);
                    break;
            }

            helper.createGroup(dataClass);
        }
    }
}