package com.centennial.notification.hub.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SaveNotificationData implements Parcelable {

    private int id;
    private int categoryID;
    private String title;
    private String message;
    private String big_text;
    private String ticker_text;
    private byte[] big_image;
    private byte[] small_icon;
    private byte[] large_icon;
    private Long date;

    private double latitude;
    private double longitude;


    public SaveNotificationData() {
    }

    protected SaveNotificationData(Parcel in) {
        id = in.readInt();
        categoryID = in.readInt();
        title = in.readString();
        message = in.readString();
        big_text = in.readString();
        ticker_text = in.readString();
        big_image = in.createByteArray();
        small_icon = in.createByteArray();
        large_icon = in.createByteArray();
        latitude = in.readDouble();
        longitude = in.readDouble();
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readLong();
        }
    }

    public static final Creator<SaveNotificationData> CREATOR = new Creator<SaveNotificationData>() {
        @Override
        public SaveNotificationData createFromParcel(Parcel in) {
            return new SaveNotificationData(in);
        }

        @Override
        public SaveNotificationData[] newArray(int size) {
            return new SaveNotificationData[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBig_text() {
        return big_text;
    }

    public void setBig_text(String big_text) {
        this.big_text = big_text;
    }

    public String getTicker_text() {
        return ticker_text;
    }

    public void setTicker_text(String ticker_text) {
        this.ticker_text = ticker_text;
    }

    public byte[] getBig_image() {
        return big_image;
    }

    public void setBig_image(byte[] big_image) {
        this.big_image = big_image;
    }

    public byte[] getSmall_icon() {
        return small_icon;
    }

    public void setSmall_icon(byte[] small_icon) {
        this.small_icon = small_icon;
    }

    public byte[] getLarge_icon() {
        return large_icon;
    }

    public void setLarge_icon(byte[] large_icon) {
        this.large_icon = large_icon;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(categoryID);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(big_text);
        dest.writeString(ticker_text);
        dest.writeByteArray(big_image);
        dest.writeByteArray(small_icon);
        dest.writeByteArray(large_icon);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(date);
        }
    }
}
