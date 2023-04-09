package com.centennial.notification.hub.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;


import static android.content.Context.MODE_PRIVATE;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences pref = context.getSharedPreferences("My_PREF", MODE_PRIVATE);
        String deleteInterval = pref.getString("Delete Time Interval", null);

        if (deleteInterval != null && !deleteInterval.equals("Never")) {
            MySQLiteHelper helper = new MySQLiteHelper(context);
            Calendar cal = Calendar.getInstance();
            switch (deleteInterval) {
                case "7 Days":
                    cal.add(Calendar.DATE, -7);
                    helper.DeleteData(cal.getTimeInMillis());
                    break;
                case "15 Days":
                    cal.add(Calendar.DATE, -15);
                    helper.DeleteData(cal.getTimeInMillis());
                    break;
                case "30 Days":
                    cal.add(Calendar.DATE, -30);
                    helper.DeleteData(cal.getTimeInMillis());
                    break;
            }
            helper.close();
        }
    }
}
