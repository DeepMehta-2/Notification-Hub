package com.centennial.notification.hub.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.centennial.notification.hub.R;
import com.centennial.notification.hub.adapter.ViewPagerAdapter;
import com.centennial.notification.hub.model.GroupDataClass;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<GroupDataClass> title_arrayList;
    public static ViewPagerAdapter pagerAdapter;
    private MySQLiteHelper helper;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private final int REQUEST_LOCATION_PERMISSION = 1;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check if location permission is granted or not.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the location permission
            requestLocationPermission();
        }

        viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tabview);

        helper = new MySQLiteHelper(this);

        // Get All group name and show as tab view
        title_arrayList = helper.showGroupName();
        if (title_arrayList != null && title_arrayList.size() > 0) {
            viewPager.setOffscreenPageLimit(title_arrayList.size());
            pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            for (GroupDataClass dataClass : title_arrayList) {
                MainFragmentLayout mainFragment = new MainFragmentLayout(dataClass.getGroup_Name());
                pagerAdapter.addFrag(mainFragment, dataClass.getGroup_Name());
            }
            viewPager.setAdapter(pagerAdapter);
        } else {
            Toast.makeText(this, "No Groups Available", Toast.LENGTH_LONG).show();
        }
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (MainFragmentLayout.mActionMode != null)
                    MainFragmentLayout.mActionMode.finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted
            } else {
                // Permission has been denied
                Snackbar.make(viewPager, "Give location permission to use Map functions.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.testingNotification) {
            sendNotification("Testing Notification", "This is a demo notification for testing purpose");
        }
        return super.onContextItemSelected(item);
    }

    private void sendNotification(String title, String message) {
        String channelID = "MyNotificationChannel";
        NotificationChannel channel = new NotificationChannel(channelID, "My Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);

        // Set the channel description and enable lights
        channel.setDescription("Channel Description");
        channel.enableLights(true);

        // Get the NotificationManager and create the channel
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MyNotificationChannel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(0, builder.build());
    }
}
