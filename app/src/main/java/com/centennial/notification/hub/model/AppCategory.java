package com.centennial.notification.hub.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Comparator;


public class AppCategory implements Comparator<AppCategory> {
    @Override
    public int compare(AppCategory o1, AppCategory o2) {
        ArrayList<SaveNotificationData> dataArrayList1 = o1.getDataArrayList();
        ArrayList<SaveNotificationData> dataArrayList2 = o2.getDataArrayList();

        Long dateTime1 = dataArrayList1.get(0).getDate();
        Long dateTime2 = dataArrayList2.get(0).getDate();

        if (dateTime1.compareTo(dateTime2) >= 0) {
            return -1;
        }
        return 1;
    }

    int categoryID;
    String AppName;
    String AppPackageName;
    byte[] AppImg;
    Long Cate_Date;
    ArrayList<SaveNotificationData> dataArrayList;

    public AppCategory() {
    }

    public AppCategory(int categoryID, String appName, String appPackageName, byte[] appImg, ArrayList<SaveNotificationData> dataArrayList) {
        this.categoryID = categoryID;
        AppName = appName;
        AppPackageName = appPackageName;
        AppImg = appImg;
        this.dataArrayList = dataArrayList;
    }

    protected AppCategory(Parcel in) {
        categoryID = in.readInt();
        AppName = in.readString();
        AppPackageName = in.readString();
        AppImg = in.createByteArray();
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public String getAppPackageName() {
        return AppPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        AppPackageName = appPackageName;
    }

    public byte[] getAppImg() {
        return AppImg;
    }

    public void setAppImg(byte[] appImg) {
        AppImg = appImg;
    }

    public ArrayList<SaveNotificationData> getDataArrayList() {
        return dataArrayList;
    }

    public void setDataArrayList(ArrayList<SaveNotificationData> dataArrayList) {
        this.dataArrayList = dataArrayList;
    }

    public Long getCate_Date() {
        return Cate_Date;
    }

    public void setCate_Date(Long cate_Date) {
        Cate_Date = cate_Date;
    }
}
