package com.centennial.notification.hub.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.centennial.notification.hub.model.InstalledAppDataClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetInstalledAppList {

    public static ArrayList<InstalledAppDataClass> getAppList(Context context) {

        ArrayList<InstalledAppDataClass> arrayList = new ArrayList<>();

        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null &&
                    !pm.getLaunchIntentForPackage(packageInfo.packageName).equals("")) {

                String appName = packageInfo.loadLabel(context.getPackageManager()).toString();
                String packageName = packageInfo.packageName;
                Drawable icon = packageInfo.loadIcon(context.getPackageManager());

                arrayList.add(new InstalledAppDataClass(appName, packageName, icon));

                // Sorting ArrayList.
                Collections.sort(arrayList, new InstalledAppDataClass());

//                Log.e("TAG", "Source dir : " + packageInfo.sourceDir);
//                Log.e("TAG", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
            }
        }

        return arrayList;
    }
}
