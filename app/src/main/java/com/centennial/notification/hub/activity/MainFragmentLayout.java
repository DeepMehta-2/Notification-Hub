package com.centennial.notification.hub.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.centennial.notification.hub.R;


public class MainFragmentLayout extends Fragment {

    private String GroupName;
    private RecyclerView recyclerView;
    private TextView textView;

    LinearLayoutManager linearLayoutManager;
    private int CurrentPage = 1, CurrentPageAdvertise = 1, pagesize = 8, visibleThreshold = 3;

    // For multi select row in recycler view
    public static ActionMode mActionMode;
    Menu context_menu;
    public static boolean isMultiSelect = false;

    public MainFragmentLayout() {
    }

    @SuppressLint("ValidFragment")
    public MainFragmentLayout(String groupName) {
        GroupName = groupName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_main_fragment, container, false);

        textView = v.findViewById(R.id.No_Notifications_tv);

        recyclerView = v.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return v;
    }
}
