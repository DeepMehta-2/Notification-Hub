package com.centennial.notification.hub.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.centennial.notification.hub.adapter.InstalledAppAdapter;

import com.centennial.notification.hub.R;
import com.centennial.notification.hub.Utils.GetInstalledAppList;
import com.centennial.notification.hub.model.InstalledAppDataClass;

import java.util.ArrayList;

public class InstalledAppList extends AppCompatActivity {

    private String groupname;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_app_list);

        recyclerView = findViewById(R.id.installedAppRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Put divider in recyclerview (between cardviews)
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        if (getIntent().getExtras() != null) {
            groupname = getIntent().getExtras().getString("groupname");
            setTitle(groupname);
        } else {
            setTitle("Create Group");
        }

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
            pd = new ProgressDialog(InstalledAppList.this, R.style.CustomProgressBar);
            pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            arrayList = GetInstalledAppList.getAppList(InstalledAppList.this);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            if (arrayList != null && arrayList.size() > 0) {
                InstalledAppAdapter adapter = new InstalledAppAdapter(InstalledAppList.this, arrayList, groupname);
                recyclerView.setAdapter(adapter);
            }

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
