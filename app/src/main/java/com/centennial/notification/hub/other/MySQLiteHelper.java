package com.centennial.notification.hub.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.centennial.notification.hub.model.AppCategory;
import com.centennial.notification.hub.model.GroupDataClass;
import com.centennial.notification.hub.model.SaveNotificationData;

import java.util.ArrayList;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "NotificationHub";

    // table name
    private static final String TABLE_APP_CATEGORY = "app_category";
    private static final String TABLE_SAVE_NOTIFICATION = "save_notification";
    private static final String TABLE_GROUP = "tbl_group";

    // column names (app_category)
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_APP_NAME = "app_name";
    private static final String KEY_APP_PKGNAME = "app_pkgname";
    private static final String KEY_APP_IMG = "app_img";
    private static final String KEY_APP_IS_SAVE = "is_save";
    private static final String KEY_CATE_DATE = "cate_date";

    // column names (save_notification)
    private static final String KEY_ID = "id";
    private static final String KEY_CATE_ID = "cate_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_BIG_TEXT = "big_text";
    private static final String KEY_TICKER_TEXT = "ticker_text";
    private static final String KEY_BIG_IMAGE = "big_image";
    private static final String KEY_SMALL_ICON = "small_icon";
    public static final String KEY_LARGE_ICON = "large_icon";
    private static final String KEY_DATE = "date";

    // column names (Groups)
    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_GROUP_NAME = "group_name";
    private static final String KEY_GROUP_APP_NAME = "app_name";
    private static final String KEY_GROUP_PKG_NAME = "app_pkg_name";
    private static final String KEY_IS_IN_GROUP = "is_in_group";

    private static final String[] COLUMNS = {KEY_ID, KEY_TITLE, KEY_MESSAGE};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL statement to create table
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_APP_CATEGORY +
                "( " + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_APP_NAME + " TEXT ,"
                + KEY_APP_PKGNAME + " TEXT," + KEY_APP_IMG + " BLOB, " + KEY_APP_IS_SAVE + " INTEGER NOT NULL DEFAULT 1, "
                + KEY_CATE_DATE + " LONG NOT NULL DEFAULT 0 )";

        String SAVE_NOTIFICATION_TABLE = "CREATE TABLE " + TABLE_SAVE_NOTIFICATION +
                "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CATE_ID + " INTEGER," + KEY_TITLE + " TEXT,"
                + KEY_MESSAGE + " TEXT," + KEY_BIG_TEXT + " TEXT," + KEY_TICKER_TEXT + " TEXT,"
                + KEY_BIG_IMAGE + " BLOB," + KEY_SMALL_ICON + " BLOB," + KEY_LARGE_ICON + " BLOB," + KEY_DATE + " LONG )";

        String CREATE_GROUP = "CREATE TABLE " + TABLE_GROUP +
                "( " + KEY_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_GROUP_NAME + " TEXT ,"
                + KEY_GROUP_APP_NAME + " TEXT," + KEY_GROUP_PKG_NAME + " TEXT,"
                + KEY_IS_IN_GROUP + " INTEGER NOT NULL DEFAULT 0 )";

        // create table
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(SAVE_NOTIFICATION_TABLE);
        db.execSQL(CREATE_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);

        // create new table
        this.onCreate(db);
    }

    //---------------------------------------------------------------------//
    public long addCategory(AppCategory category) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_APP_NAME, category.getAppName());
        values.put(KEY_APP_PKGNAME, category.getAppPackageName());
        values.put(KEY_APP_IMG, category.getAppImg());
//        values.put(KEY_APP_IS_SAVE, 1); By Default 1 , No need to give value

        long id = db.insert(TABLE_APP_CATEGORY, // table
                null,
                values); // key/value -> keys = column names/ values = column values

        db.close();

        return id;
    }

    public long insertSaveNotification(SaveNotificationData notificationData) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_CATE_ID, notificationData.getCategoryID());
            values.put(KEY_TITLE, notificationData.getTitle());
            values.put(KEY_MESSAGE, notificationData.getMessage());
            values.put(KEY_BIG_TEXT, notificationData.getBig_text());
            values.put(KEY_TICKER_TEXT, notificationData.getTicker_text());
            values.put(KEY_BIG_IMAGE, notificationData.getBig_image());
            values.put(KEY_SMALL_ICON, notificationData.getSmall_icon());
            values.put(KEY_LARGE_ICON, notificationData.getLarge_icon());
            values.put(KEY_DATE, notificationData.getDate());

            long id = db.insert(TABLE_SAVE_NOTIFICATION, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values

            // Update the date of category table...//
            ContentValues cv = new ContentValues();
            cv.put(KEY_CATE_DATE, notificationData.getDate());
            db.update(TABLE_APP_CATEGORY, cv, KEY_CATEGORY_ID + " = ?", new String[]{String.valueOf(notificationData.getCategoryID())});

            db.close();

            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Object[] getCategory(String PkgName) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_APP_CATEGORY, // a. table
                        new String[]{KEY_CATEGORY_ID, KEY_APP_NAME, KEY_APP_IS_SAVE}, // b. column names
                        KEY_APP_PKGNAME + " = ?", // c. selections
                        new String[]{PkgName}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                boolean b = (cursor.getInt(2) == 1);
                cursor.close();
                return new Object[]{id, b};
            } else {
                cursor.close();
                return new Object[]{-1, false};
            }
        }
        cursor.close();
        return new Object[]{-1, false};
    }


    public ArrayList<SaveNotificationData> getAllSaveNotifications(int cate_id, String datalimit) {

        ArrayList<SaveNotificationData> saveNotificationData = new ArrayList<>();
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_SAVE_NOTIFICATION, // a. table
                        new String[]{KEY_ID, KEY_CATE_ID, KEY_TITLE, KEY_MESSAGE,
                                KEY_BIG_TEXT, KEY_TICKER_TEXT,
                                KEY_BIG_IMAGE, KEY_SMALL_ICON, KEY_LARGE_ICON, KEY_DATE}, // b. column names
                        KEY_CATE_ID + " = ?", // c. selections
                        new String[]{String.valueOf(cate_id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        KEY_DATE + " DESC", // g. order by
                        datalimit); // h. limit

        // 3. go over each row, build book and add it to list
        if (cursor.moveToFirst()) {
            do {
                SaveNotificationData data = new SaveNotificationData();
                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setCategoryID(Integer.parseInt(cursor.getString(1)));
                data.setTitle(cursor.getString(2));
                data.setMessage(cursor.getString(3));
                data.setBig_text(cursor.getString(4));
                data.setTicker_text(cursor.getString(5));
                data.setBig_image(cursor.getBlob(6));
                data.setSmall_icon(cursor.getBlob(7));
                data.setLarge_icon(cursor.getBlob(8));
                data.setDate(Long.parseLong(cursor.getString(9)));

                // Add category to categories.
                saveNotificationData.add(data);

            } while (cursor.moveToNext());
        } else {
//            Log.e("Empty", "null");
        }
        cursor.close();
        return saveNotificationData;
    }

    public ArrayList<SaveNotificationData> getAllSaveNotificationsPageWise(int cate_id, int currentpage, int datalimit) {

        ArrayList<SaveNotificationData> saveNotificationData = new ArrayList<>();
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_SAVE_NOTIFICATION, // a. table
                        new String[]{KEY_ID, KEY_CATE_ID, KEY_TITLE, KEY_MESSAGE,
                                KEY_BIG_TEXT, KEY_TICKER_TEXT,
                                KEY_BIG_IMAGE, KEY_SMALL_ICON, KEY_LARGE_ICON, KEY_DATE}, // b. column names
                        KEY_CATE_ID + " = ?", // c. selections
                        new String[]{String.valueOf(cate_id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        KEY_DATE + " DESC", // g. order by
                        ((currentpage - 1) * datalimit) + "," + datalimit); // h. limit


        // 3. go over each row, build book and add it to list
        if (cursor.moveToFirst()) {
            do {
                SaveNotificationData data = new SaveNotificationData();
                data.setId(Integer.parseInt(cursor.getString(0)));
                data.setCategoryID(Integer.parseInt(cursor.getString(1)));
                data.setTitle(cursor.getString(2));
                data.setMessage(cursor.getString(3));
                data.setBig_text(cursor.getString(4));
                data.setTicker_text(cursor.getString(5));
                data.setBig_image(cursor.getBlob(6));
                data.setSmall_icon(cursor.getBlob(7));
                data.setLarge_icon(cursor.getBlob(8));
                data.setDate(Long.parseLong(cursor.getString(9)));

                // Add category to categories.
                saveNotificationData.add(data);

            } while (cursor.moveToNext());
        } else {
            Log.e("Empty", "null");
        }
        cursor.close();
        return saveNotificationData;
    }

    // Get All Categories
    public ArrayList<AppCategory> getAllCategory(String GroupName, int currentpage, int datalimit) {
        ArrayList<AppCategory> categories = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        String groupQuery = ("SELECT " + KEY_GROUP_PKG_NAME + " FROM " + TABLE_GROUP + " WHERE " + KEY_GROUP_NAME + " = ? AND "
                + KEY_IS_IN_GROUP + " = ? ");
        Cursor cursor1 = db.rawQuery(groupQuery, new String[]{GroupName, String.valueOf(1)});

        String packageNames = null;

        if (cursor1.moveToFirst()) {

            StringBuffer packages = new StringBuffer("('");
            int i = 0;

            do {
                String pkgName = cursor1.getString(cursor1.getColumnIndex(KEY_GROUP_PKG_NAME));
                packages.append(pkgName).append("'");
                i++;
                if (i < cursor1.getCount()) {
                    packages.append(",").append("'");
                }

            } while (cursor1.moveToNext());

            packageNames = packages.append(")").toString();
//            Log.e("packages", packageNames);
        }

        if (packageNames != null) {

            String q = ("SELECT * from " + TABLE_APP_CATEGORY + " WHERE " + KEY_APP_PKGNAME + " IN " + packageNames
                    + " ORDER BY " + KEY_CATE_DATE + " DESC LIMIT " + datalimit + " OFFSET " + ((currentpage - 1) * datalimit));

            //        String query = ("SELECT c.* FROM " + TABLE_APP_CATEGORY + " c, " + TABLE_GROUP +
//                " g WHERE g." + KEY_GROUP_PKG_NAME + " = '" + GroupName + "' AND c." + KEY_APP_PKGNAME + " = g." + KEY_GROUP_PKG_NAME);

            // 1. build the query
//        String query = ("SELECT * FROM " + TABLE_APP_CATEGORY + " ORDER BY " + KEY_CATEGORY_ID + " DESC");

            // 2. get reference to writable DB
            Cursor cursor = db.rawQuery(q, null);

            // 3. go over each row, build book and add it to list
            if (cursor.moveToFirst()) {
                do {
                    AppCategory category = new AppCategory();
                    category.setCategoryID(cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_ID)));
//                category.setCategoryID(Integer.parseInt(cursor.getString(0)));
                    category.setAppName(cursor.getString(1));
                    category.setAppPackageName(cursor.getString(2));
                    category.setAppImg(cursor.getBlob(3));

                    // Calling getAllSaveNotifications method to get all notification data.
                    ArrayList<SaveNotificationData> arrayList = this
                            .getAllSaveNotifications(category.getCategoryID(), "6");

                    if (arrayList != null && arrayList.size() > 0) {

                        category.setDataArrayList(arrayList);

                        // Add category to categories.
                        categories.add(category);
                    }

                } while (cursor.moveToNext());
            } else {
                Log.e("Sqlite: Empty", "null");
            }
            cursor.close();

            // Sorting appcategory comparator on date. (Check model class for more code)
//            Collections.sort(categories, new AppCategory());

            return categories;
        }
        return null;
    }

    // Deleting single notification
    public void deleteNotification(SaveNotificationData saveNotificationData) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_SAVE_NOTIFICATION,
                KEY_ID + " = ?",
                new String[]{String.valueOf(saveNotificationData.getId())});

        // 3. close
        db.close();
    }

    // Deleting category
    public void deleteCategory(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_SAVE_NOTIFICATION,
                KEY_CATE_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.delete(TABLE_APP_CATEGORY,
                KEY_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(id)});
        // 3. close
        db.close();
    }

    public boolean DeleteNotificationTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedrow = db.delete(TABLE_SAVE_NOTIFICATION, null, null);
        int deletedrow1 = db.delete(TABLE_APP_CATEGORY, null, null);
        db.close();
        return deletedrow > 0;
    }

    //------------------  Groups  ----------------------//
    public boolean createGroup(GroupDataClass dataClass) {

        SQLiteDatabase db = this.getWritableDatabase();

        String quary = "Select * from " + TABLE_GROUP + " where " + KEY_GROUP_NAME + " = '" + dataClass.getGroup_Name() + "' and "
                + KEY_GROUP_PKG_NAME + " = '" + dataClass.getGroup_Pkg_Name() + "'";

        Cursor cursor = db.rawQuery(quary, null);

        if (cursor.moveToFirst()) {

            cursor.close();
            db.close();
            return false;

        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_GROUP_NAME, dataClass.getGroup_Name());
            values.put(KEY_GROUP_APP_NAME, dataClass.getGroup_App_Name());
            values.put(KEY_GROUP_PKG_NAME, dataClass.getGroup_Pkg_Name());
            values.put(KEY_IS_IN_GROUP, dataClass.getIs_inGroup());

            long id = db.insert(TABLE_GROUP, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
//            Log.e("id", String.valueOf(id));

            cursor.close();
            db.close();
            return true;
        }
    }

}