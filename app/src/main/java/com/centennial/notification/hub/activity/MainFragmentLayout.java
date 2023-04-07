package com.centennial.notification.hub.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.centennial.notification.hub.adapter.CategoryAdapter;
import com.centennial.notification.hub.Utils.EndlessRecyclerOnScrollListener;
import com.centennial.notification.hub.Utils.RecyclerItemClickListener;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;

import com.centennial.notification.hub.model.AppCategory;

import java.util.ArrayList;


public class MainFragmentLayout extends Fragment {

    private String GroupName;
    private RecyclerView recyclerView;
    private TextView textView;
    ArrayList<AppCategory> arrayList = new ArrayList<>();
    CategoryAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private int CurrentPage = 1, CurrentPageAdvertise = 1, pagesize = 8, visibleThreshold = 3;

    // For multi select row in recycler view
    public static ActionMode mActionMode;
    Menu context_menu;
    ArrayList<AppCategory> multiselect_list = new ArrayList<>();
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

        final SwipeRefreshLayout swiperefresh = v.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    adapter.ClearAdapter();
                }
                CurrentPage = 1;
                recyclerView.clearOnScrollListeners();
                new FetchData(pagesize).execute();
                swiperefresh.setRefreshing(false);
                RecyclerOnScroll();

            }
        });

        RecyclerOnScroll();

        new FetchData(pagesize).execute();

        // For multi select row in recycler view
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
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
                    multiselect_list = new ArrayList<AppCategory>();
                    isMultiSelect = true;
                    if (mActionMode == null) {
                        mActionMode = ((MainActivity) getContext()).startActionMode(mActionModeCallback);
                    }
                }
                multi_select(position);
            }
        }));

        return v;
    }

    private void RecyclerOnScroll() {
        // For get limited data (get other data on scroll down)
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager, visibleThreshold) {
            @Override
            public void onLoadMore(int current_page) {
                if (current_page > CurrentPage) {
                    CurrentPage = current_page;
                    new FetchData(pagesize).execute();
                }
            }
        });
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
                pd = new ProgressDialog(getActivity(), R.style.CustomProgressBar);
                pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                pd.show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            MySQLiteHelper db = new MySQLiteHelper(getActivity());
            ArrayList<AppCategory> arrayList_1 = db.getAllCategory(GroupName, CurrentPage, pagesize);
            if (arrayList_1 != null && arrayList_1.size() > 0) {
                arrayList.addAll(arrayList_1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (arrayList.size() <= 0 || arrayList == null || arrayList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                if (adapter == null) {
                    adapter = new CategoryAdapter(getActivity(), arrayList, multiselect_list);
                    recyclerView.setAdapter(adapter);
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
            if (multiselect_list.contains(arrayList.get(position))) {
                multiselect_list.remove(arrayList.get(position));
            } else {
                multiselect_list.add(arrayList.get(position));
            }
            if (multiselect_list.size() > 0) {
                mActionMode.setTitle("" + multiselect_list.size());
            } else {
                mActionMode.setTitle("");
                mActionMode.finish();
            }
            refreshAdapter();
        }
    }

    public void refreshAdapter() {
        adapter.selected_List = multiselect_list;
        adapter.arrayList = arrayList;
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
            CheckBox checkBox = actionView.findViewById(R.id.action_check);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        multiselect_list.clear();
                        multiselect_list.addAll(arrayList);
                        refreshAdapter();
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

                    if (multiselect_list.size() > 0) {
                        MySQLiteHelper db = new MySQLiteHelper(getActivity());
                        db.deleteMultipleCategory(multiselect_list);

                        for (int i = 0; i < multiselect_list.size(); i++) {
                            if (arrayList.contains(multiselect_list.get(i))) {
                                arrayList.remove(multiselect_list.get(i));
                            }
                        }

                        if (mActionMode != null) {
                            mActionMode.finish();
                        }

                        onDestroyActionMode(mode);

                        Toast.makeText(getActivity(), "Selected item successfully deleted", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "No item selected", Toast.LENGTH_SHORT).show();
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
            multiselect_list = new ArrayList<AppCategory>();
            refreshAdapter();
        }
    };
}
