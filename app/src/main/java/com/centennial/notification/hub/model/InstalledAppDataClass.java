package com.centennial.notification.hub.model;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

public class InstalledAppDataClass implements Comparator<InstalledAppDataClass> {

    private String AppName;
    private String PackageName;
    private Drawable AppIcon;


    @Override
    public int compare(InstalledAppDataClass o1, InstalledAppDataClass o2) {

        return o1.getAppName().compareToIgnoreCase(o2.getAppName());
    }

    public InstalledAppDataClass() {
    }

    public InstalledAppDataClass(String appName, String packageName, Drawable appIcon) {
        AppName = appName;
        PackageName = packageName;
        AppIcon = appIcon;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public Drawable getAppIcon() {
        return AppIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        AppIcon = appIcon;
    }

}
