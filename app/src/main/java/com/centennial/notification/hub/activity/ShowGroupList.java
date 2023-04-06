package com.centennial.notification.hub.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.MenuItem;
import android.widget.Toast;


import com.centennial.notification.hub.adapter.GroupListAdapter;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;

import com.centennial.notification.hub.model.GroupDataClass;

import java.util.ArrayList;

public class ShowGroupList extends AppCompatActivity {

    private RecyclerView groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group_list);

        groupList = findViewById(R.id.groupList);
        groupList.setLayoutManager(new LinearLayoutManager(this));

        // Put divider in recyclerview (between cardviews)
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(groupList.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.divider));
        groupList.addItemDecoration(dividerItemDecoration);

        final SwipeRefreshLayout swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Update();
                swiperefresh.setRefreshing(false);
            }
        });

        Update();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void Update() {

        MySQLiteHelper helper = new MySQLiteHelper(this);
        ArrayList<GroupDataClass> arrayList = helper.showGroupName();

        if (arrayList != null && arrayList.size() > 0) {
            GroupListAdapter adapter = new GroupListAdapter(this, arrayList);
            groupList.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No Groups Available", Toast.LENGTH_LONG).show();
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
