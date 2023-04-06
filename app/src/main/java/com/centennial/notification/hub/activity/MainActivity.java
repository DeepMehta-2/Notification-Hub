package com.centennial.notification.hub.activity;

import android.content.SharedPreferences;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ListView;

import com.centennial.notification.hub.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import com.centennial.notification.hub.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ViewPagerAdapter pagerAdapter;

    private SharedPreferences preferences;
    private Toolbar toolbar;
    ArrayList<String> navigation_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("My_PREF", MODE_PRIVATE);

        Boolean isPermission = preferences.getBoolean("isPermission", false);
        if (!isPermission) {
        }

        navigation_items = new ArrayList<>();
        //adding menu items for naviations
        navigation_items.add("Home");
        navigation_items.add("Settings");
        navigation_items.add("Rate");
        navigation_items.add("Share");
        navigation_items.add("More Apps");
        navigation_items.add("Exit");

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabview);

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

}
