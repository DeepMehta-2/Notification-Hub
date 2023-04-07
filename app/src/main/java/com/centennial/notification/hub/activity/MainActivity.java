package com.centennial.notification.hub.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
}
