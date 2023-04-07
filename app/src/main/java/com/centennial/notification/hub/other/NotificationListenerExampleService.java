package com.centennial.notification.hub.other;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.centennial.notification.hub.model.SaveNotificationData;
import com.centennial.notification.hub.Utils.Common;
import com.centennial.notification.hub.model.AppCategory;

public class NotificationListenerExampleService extends NotificationListenerService {

    byte[] AppIcon;
    byte[] SmallIcon = null;
    byte[] LargeIcon = null;
    byte[] BigImage = null;
    String title, message, TickerText, BigText;

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

//        for (StatusBarNotification sbm : this.getActiveNotifications()) {

        // Get data from installed application using package name.
        String package_name = sbn.getPackageName();
        PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(package_name, 0);
            Drawable icon = getPackageManager().getApplicationIcon(package_name);
            AppIcon = Common.getBytes(icon);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
        } catch (ClassCastException e) {
            try {
                VectorDrawable icon = (VectorDrawable) getPackageManager().getApplicationIcon(package_name);
                AppIcon = Common.getBytesFromVectore(icon);
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");


        // Get data of notifications.
        Bundle extras = sbn.getNotification().extras;
        title = extras.getString("android.title");
        message = extras.getString("android.text");

        if (sbn.getNotification().contentIntent != null) {
            PendingIntent pendingIntent = sbn.getNotification().contentIntent;
        }
        if (sbn.getNotification().actions != null) {
            Notification.Action[] actions = sbn.getNotification().actions;
        }
        if (sbn.getNotification().tickerText != null) {
            TickerText = sbn.getNotification().tickerText.toString();
        }
        if (extras.containsKey(Notification.EXTRA_SMALL_ICON) && extras.get(Notification.EXTRA_SMALL_ICON) != null) {
            try {
                int id = extras.getInt(Notification.EXTRA_SMALL_ICON);
                Context remotePackageContext = getApplicationContext().createPackageContext(package_name, 0);
                Drawable icon = remotePackageContext.getResources().getDrawable(id);
                if (icon != null) {
                    Bitmap bmp = ((BitmapDrawable) icon).getBitmap();
                    SmallIcon = Common.getBytesFromBitmap(bmp);
                }
            } catch (Exception e) {
                e.printStackTrace();
                SmallIcon = AppIcon;
                Log.e("catch small icon", "" + e.toString());
            }
        }
        if (extras.containsKey(Notification.EXTRA_LARGE_ICON) && extras.get(Notification.EXTRA_LARGE_ICON) != null) {
            try {
                Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_LARGE_ICON);
                LargeIcon = Common.getBytesFromBitmap(bmp);
            } catch (ClassCastException e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Icon bmp = (Icon) extras.get(Notification.EXTRA_LARGE_ICON);
                    Drawable drawable = bmp.loadDrawable(this);
                    LargeIcon = Common.getBytes(drawable);
                }
                Log.e("catch large icon", "" + e.toString());
            }
        }
        if (extras.containsKey(Notification.EXTRA_BIG_TEXT) && extras.get(Notification.EXTRA_BIG_TEXT) != null) {
            BigText = extras.get(Notification.EXTRA_BIG_TEXT).toString();
            if (message != null && !message.isEmpty()) {
                if (message.length() == BigText.length() && message.equals(BigText)) {
                    BigText = "";
                }
            }
        }
        if (extras.containsKey(Notification.EXTRA_PICTURE) && extras.get(Notification.EXTRA_PICTURE) != null) {
            // this bitmap contain the picture attachment
            Bitmap bmp = (Bitmap) extras.get(Notification.EXTRA_PICTURE);
            BigImage = Common.getBytesFromBitmap(bmp);
        }

        // Insert into database
        MySQLiteHelper db = new MySQLiteHelper(this);
        Object[] category_id = db.getCategory(package_name);
//        Log.e("package_name", applicationName);
//        Log.e("0", String.valueOf(category_id[0]));
//        Log.e("1", String.valueOf(category_id[1]));

        if ((int) category_id[0] == -1) {
            AppCategory category = new AppCategory();
            category.setAppName(applicationName);
            category.setAppPackageName(package_name);
            category.setAppImg(AppIcon);
            long new_cate_id = db.addCategory(category);
            if (title != null) {
                if (new_cate_id != -1) {
                    SaveNotificationData data = new SaveNotificationData();
                    data.setCategoryID((int) new_cate_id);
                    data.setTitle(title);
                    data.setMessage(message);
                    data.setBig_text(BigText);
                    data.setTicker_text(TickerText);
                    data.setBig_image(BigImage);
                    data.setSmall_icon(SmallIcon);
                    data.setLarge_icon(LargeIcon);
                    data.setDate(sbn.getPostTime());
                    Long i = db.insertSaveNotification(data);
                }
            }
        } else {
            if ((boolean) category_id[1])
                if (title != null) {
                    SaveNotificationData data = new SaveNotificationData();
                    data.setCategoryID((Integer) category_id[0]);
                    data.setTitle(title);
                    data.setMessage(message);
                    data.setBig_text(BigText);
                    data.setTicker_text(TickerText);
                    data.setBig_image(BigImage);
                    data.setSmall_icon(SmallIcon);
                    data.setLarge_icon(LargeIcon);
                    data.setDate(sbn.getPostTime());
                    Long i = db.insertSaveNotification(data);
                }
        }
        ClearVariables();
    }

    private void ClearVariables() {
        title = "";
        message = "";
        TickerText = "";
        BigText = "";
        AppIcon = null;
        SmallIcon = null;
        LargeIcon = null;
        BigImage = null;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

}
