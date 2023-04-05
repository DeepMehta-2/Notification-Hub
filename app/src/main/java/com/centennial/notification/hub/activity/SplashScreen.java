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

import com.centennial.notification.hub.R;
import com.centennial.notification.hub.Utils.GetInstalledAppList;


public class SplashScreen extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    int delaytime = 3000;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        preferences = getSharedPreferences("My_PREF", MODE_PRIVATE);

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
}
