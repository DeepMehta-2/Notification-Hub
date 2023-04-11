package com.centennial.notification.hub.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.centennial.notification.hub.adapter.SaveNotificationAdapter;
import com.centennial.notification.hub.Utils.RecyclerItemClickListener;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;
import com.centennial.notification.hub.Utils.EndlessRecyclerOnScrollListener;
import com.centennial.notification.hub.model.SaveNotificationData;

import java.util.ArrayList;

public class SaveNotificationActivity extends AppCompatActivity {

    private int categoryID;
    private TextView NoData;
    private RecyclerView notification_recycler;
    private int CurrentPage = 1, pagesize = 25, visibleThreshold = 5;
    private SaveNotificationAdapter adapter;
    ArrayList<SaveNotificationData> arr = new ArrayList<>();
    Long DataCount = Long.valueOf(0);

    // For multi select row in recycler view
    ActionMode mActionMode;
    CheckBox checkBox;
    Menu context_menu;
    ArrayList<SaveNotificationData> multiselect_list = new ArrayList<>();
    public static boolean isMultiSelect = false;

    String appName;
    byte[] appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_notification);

        categoryID = getIntent().getExtras().getInt("categoryID");
        appName = getIntent().getExtras().getString("appName", getResources().getString(R.string.app_name));
        appIcon = getIntent().getExtras().getByteArray("appIcon");

        setTitle("   " + appName);
        if (appIcon != null) {

            BitmapDrawable d = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(appIcon, 0, appIcon.length),
                            80, 80, true));
            getSupportActionBar().setIcon(d);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NoData = findViewById(R.id.NoData);
        notification_recycler = findViewById(R.id.notification_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        notification_recycler.setLayoutManager(linearLayoutManager);

        // For multi select row in recycler view
        notification_recycler.addOnItemTouchListener(new RecyclerItemClickListener(this, notification_recycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    multi_select(position);
                } else {
//                    Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<SaveNotificationData>();
                    isMultiSelect = true;
                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }

                    MySQLiteHelper helper = new MySQLiteHelper(SaveNotificationActivity.this);
                    DataCount = helper.getNotificationCount(categoryID);

                }
                multi_select(position);
            }
        }));

        // For get limited data (get other data on scroll down)
        notification_recycler.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager, visibleThreshold) {
            @Override
            public void onLoadMore(int current_page) {
                if (current_page > CurrentPage) {
                    CurrentPage = current_page;
//                    if (Service.CheckNet(HomeActivity.this)) {
                    new FetchData(pagesize).execute();
//                    } else {
//                        Toast.makeText(SaveNotificationActivity.this, getResources().getString(R.string.MsgCheckConnection), Toast.LENGTH_LONG).show();
//                    }
                }
            }
        });

        new FetchData(pagesize).execute();
    }

    private class FetchData extends AsyncTask<String, String, String> {

        ProgressDialog pd = null;
        int pagesize;

        public FetchData(int pagesize1) {
            pagesize = pagesize1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (CurrentPage == 1) {
                pd = new ProgressDialog(SaveNotificationActivity.this, R.style.CustomProgressBar);
                pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                pd.setCancelable(false);
                pd.show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            MySQLiteHelper helper = new MySQLiteHelper(SaveNotificationActivity.this);
            ArrayList<SaveNotificationData> arr1 = helper.getAllSaveNotificationsPageWise(categoryID, CurrentPage, pagesize);
            if (arr1 != null && arr1.size() > 0) {
                arr.addAll(arr1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (arr.size() <= 0 || arr == null || arr.isEmpty()) {
                NoData.setVisibility(View.VISIBLE);
                notification_recycler.setVisibility(View.GONE);
            } else {
                NoData.setVisibility(View.GONE);
                notification_recycler.setVisibility(View.VISIBLE);
                if (adapter == null) {
                    adapter = new SaveNotificationAdapter(SaveNotificationActivity.this, arr, multiselect_list, appName, appIcon);
                    notification_recycler.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    // For multi select row in recycler view
    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(arr.get(position))) {
                multiselect_list.remove(arr.get(position));
            } else {
                multiselect_list.add(arr.get(position));
            }
            if (multiselect_list.size() > 0) {
                mActionMode.setTitle("" + multiselect_list.size());
            } else {
                mActionMode.setTitle("");
                mActionMode.finish();
            }

            if (multiselect_list.size() == DataCount) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            refreshAdapter();
        }
    }

    public void refreshAdapter() {
        adapter.selected_List = multiselect_list;
        adapter.arrayList = arr;
        adapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.multi_select_menu_bar, menu);
            context_menu = menu;

            MenuItem mnItem = menu.findItem(R.id.action_select_all);
            View actionView = MenuItemCompat.getActionView(mnItem);
            checkBox = actionView.findViewById(R.id.action_check);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        if (arr.size() != DataCount) {
                            multiselect_list.clear();
                            new FetchDataForDelete().execute();
//                            MySQLiteHelper helper = new MySQLiteHelper(SaveNotificationActivity.this);
//                            ArrayList<SaveNotificationData> arr1 = helper.getAllSaveNotifications(categoryID, "5000");
//                            if (arr1 != null && arr1.size() > 0) {
//                                arr = arr1;
//                                multiselect_list.addAll(arr);
//                                refreshAdapter();
//                            }
                        } else {
                            multiselect_list.clear();
                            multiselect_list.addAll(arr);
                            refreshAdapter();
                        }
                    } else {
                        multiselect_list.clear();
                        refreshAdapter();
                    }
                    if (multiselect_list.size() > 0) {
                        mActionMode.setTitle("" + multiselect_list.size());
                    } else {
                        mActionMode.setTitle("");
                    }
                }
            });

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete_all:
//                    alertDialogHelper.showAlertDialog("", "Delete Contact", "DELETE", "CANCEL", 1, false);

                    if (multiselect_list.size() > 0) {
                        MySQLiteHelper db = new MySQLiteHelper(SaveNotificationActivity.this);
                        db.deleteMultipleNotification(multiselect_list);

                        for (int i = 0; i < multiselect_list.size(); i++) {
                            if (arr.contains(multiselect_list.get(i))) {
                                arr.remove(multiselect_list.get(i));
                            }
                        }

                        if (mActionMode != null) {
                            mActionMode.finish();
                        }

                        onDestroyActionMode(mode);

                        Toast.makeText(SaveNotificationActivity.this, "Selected item successfully deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SaveNotificationActivity.this, "No item selected", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<SaveNotificationData>();
            refreshAdapter();
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchDataForDelete extends AsyncTask<String, String, String> {

        ProgressDialog pd = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SaveNotificationActivity.this, R.style.CustomProgressBar);
            pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pd.setCancelable(false);
            pd.show();
        }

        ArrayList<SaveNotificationData> arr1;

        @Override
        protected String doInBackground(String... strings) {
            MySQLiteHelper helper = new MySQLiteHelper(SaveNotificationActivity.this);
            arr1 = helper.getAllSaveNotifications(categoryID, "5000");

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (arr1 != null && arr1.size() > 0) {
                arr = arr1;
                multiselect_list.addAll(arr);
                refreshAdapter();
            }

            if (multiselect_list.size() > 0) {
                mActionMode.setTitle("" + multiselect_list.size());
            } else {
                mActionMode.setTitle("");
            }

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }
}
