package com.centennial.notification.hub.other;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import com.centennial.notification.hub.R;

import static android.content.Context.MODE_PRIVATE;

public class MyBroadcastReceiver extends BroadcastReceiver {

    int affetedRow;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences pref = context.getSharedPreferences("My_PREF", MODE_PRIVATE);
        String deleteInterval = pref.getString("Delete Time Interval", null);

        if (deleteInterval != null && !deleteInterval.equals("Never")) {
            MySQLiteHelper helper = new MySQLiteHelper(context);
            Calendar cal = Calendar.getInstance();
            if (deleteInterval.equals("7 Days")) {
                cal.add(Calendar.DATE, -7);
                affetedRow = helper.DeleteData(cal.getTimeInMillis());
            }else if (deleteInterval.equals("15 Days")) {
                cal.add(Calendar.DATE, -15);
                affetedRow = helper.DeleteData(cal.getTimeInMillis());
            } else if (deleteInterval.equals("30 Days")) {
                cal.add(Calendar.DATE, -30);
                affetedRow = helper.DeleteData(cal.getTimeInMillis());
            }
            helper.close();
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("NotificationHub")
                        .setContentText("Affected Row : " + affetedRow);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }
}
