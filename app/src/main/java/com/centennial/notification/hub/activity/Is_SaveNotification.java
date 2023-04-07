package com.centennial.notification.hub.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;


import com.centennial.notification.hub.adapter.Is_SaveNotificationAdapter;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;
import com.centennial.notification.hub.Utils.Common;
import com.centennial.notification.hub.Utils.GetInstalledAppList;
import com.centennial.notification.hub.model.AppCategory;
import com.centennial.notification.hub.model.InstalledAppDataClass;

import java.util.ArrayList;

public class Is_SaveNotification extends AppCompatActivity {

    public static TextView savedCountTextView;
    public static int SavedCount;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is__save_notification);

        savedCountTextView = findViewById(R.id.savedCount);

        recyclerView = findViewById(R.id.installedAppRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Put divider in recyclerview (between cardviews)
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        final SwipeRefreshLayout swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetData().execute();
                swiperefresh.setRefreshing(false);
            }
        });

        new GetData().execute();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class GetData extends AsyncTask<String, String, String> {

        ProgressDialog pd = null;
        ArrayList<InstalledAppDataClass> arrayList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Is_SaveNotification.this, R.style.CustomProgressBar);
            pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            SavedCount = 0;

            arrayList = GetInstalledAppList.getAppList(Is_SaveNotification.this);

            if (arrayList != null && arrayList.size() > 0) {

                for (InstalledAppDataClass dataClass : arrayList) {

                    MySQLiteHelper helper = new MySQLiteHelper(Is_SaveNotification.this);
                    int i = helper.CheckIsSaved(dataClass.getPackageName());
                    if (i == 1) {
                        SavedCount += 1;
                    } else if (i == 2) {
                        AppCategory category = new AppCategory();
                        category.setAppName(dataClass.getAppName());
                        category.setAppPackageName(dataClass.getPackageName());
                        category.setAppImg(Common.getBytes(dataClass.getAppIcon()));
                        Long id = helper.addCategory(category);
                        SavedCount += 1;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            savedCountTextView.setText("Apps saved " + SavedCount + "/" + arrayList.size());

            Is_SaveNotificationAdapter adapter = new Is_SaveNotificationAdapter(Is_SaveNotification.this, arrayList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
