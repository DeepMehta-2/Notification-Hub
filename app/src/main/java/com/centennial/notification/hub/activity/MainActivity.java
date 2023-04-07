package com.centennial.notification.hub.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.ViewPager;

import com.centennial.notification.hub.R;
import com.centennial.notification.hub.adapter.ViewPagerAdapter;
import com.centennial.notification.hub.model.GroupDataClass;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<GroupDataClass> title_arrayList;
    public static ViewPagerAdapter pagerAdapter;
    private MySQLiteHelper helper;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewpager);
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
            sendNotification("Testing Notification","This is a demo notification for testing purpose");
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
