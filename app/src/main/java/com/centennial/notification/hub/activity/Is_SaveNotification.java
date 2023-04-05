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
import com.centennial.notification.hub.R;
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

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
