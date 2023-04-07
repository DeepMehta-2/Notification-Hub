package com.centennial.notification.hub.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.centennial.notification.hub.Utils.Common;
import com.centennial.notification.hub.activity.MainFragmentLayout;
import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;
import com.centennial.notification.hub.activity.SaveNotificationActivity;
import com.centennial.notification.hub.model.AppCategory;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> implements InnerListAdapter.OnInnerListItemClickListener {

    Context c;
    public ArrayList<AppCategory> arrayList;
    public ArrayList<AppCategory> selected_List = new ArrayList<>();
    private static int i1 = 0;
    private static boolean isFb;

    public CategoryAdapter(Context c, ArrayList<AppCategory> arrayList, ArrayList<AppCategory> selected_List) {
        this.c = c;
        this.arrayList = arrayList;
        this.selected_List = selected_List;
    }

    public void ClearAdapter() {
        arrayList.clear();
        selected_List.clear();
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.category_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.app_Name.setText(arrayList.get(position).getAppName());
        holder.app_img.setImageBitmap(Common.getImage(arrayList.get(position).getAppImg()));

        holder.innerRecyclerView.setHasFixedSize(true);
        holder.innerRecyclerView.setLayoutManager(new LinearLayoutManager(c));
        holder.innerRecyclerView.setAdapter(new InnerListAdapter(c, arrayList.get(position).getDataArrayList(), this, position));

        if (arrayList.get(position).getDataArrayList().size() > 5) {
            holder.readMore.setVisibility(View.VISIBLE);
        } else {
            holder.readMore.setVisibility(View.GONE);
        }

        // For multi select row in recycler view
        if (MainFragmentLayout.isMultiSelect) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.btnDeleteCategory.setVisibility(View.GONE);
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.btnDeleteCategory.setVisibility(View.VISIBLE);
        }

        // For multi select row in recycler view
        if (selected_List.contains(arrayList.get(position))) {
            holder.checkbox.setChecked(true);
//            holder.mainCardLayout.setCardBackgroundColor(ContextCompat.getColor(c, R.color.black));
        } else {
            holder.checkbox.setChecked(false);
//            holder.mainCardLayout.setCardBackgroundColor(ContextCompat.getColor(c, R.color.background_color));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void OnInnerListItemClick(int position) {
        Intent i = new Intent(c, SaveNotificationActivity.class);
        i.putExtra("categoryID", arrayList.get(position).getCategoryID());
        i.putExtra("appName", arrayList.get(position).getAppName());
        i.putExtra("appIcon", arrayList.get(position).getAppImg());
//        i.putParcelableArrayListExtra("data", arrayList.get(position).getDataArrayList());
        c.startActivity(i);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView app_img;
        TextView app_Name;
        RecyclerView innerRecyclerView;
        CardView mainCardLayout;
        Button btnDeleteCategory, readMore;
        CheckBox checkbox;

        public MyViewHolder(View itemView) {
            super(itemView);

            app_img = itemView.findViewById(R.id.app_img);
            app_Name = itemView.findViewById(R.id.app_name);
            checkbox = itemView.findViewById(R.id.checkbox);
            readMore = itemView.findViewById(R.id.readMore);
            innerRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
            mainCardLayout = itemView.findViewById(R.id.mainCardLayout);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategory);

            mainCardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(c, SaveNotificationActivity.class);
                    i.putExtra("categoryID", arrayList.get(getAdapterPosition()).getCategoryID());
                    i.putExtra("appName", arrayList.get(getAdapterPosition()).getAppName());
                    i.putExtra("appIcon", arrayList.get(getAdapterPosition()).getAppImg());
//                    i.putParcelableArrayListExtra("data", arrayList.get(getAdapterPosition()).getDataArrayList());
                    c.startActivity(i);
                }
            });

            btnDeleteCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final AlertDialog alert = new AlertDialog.Builder(c).create();
                    View v = LayoutInflater.from(c).inflate(R.layout.dialog_design, null);
                    alert.setView(v);

                    TextView title = v.findViewById(R.id.dialog_title);
                    title.setText("Delete notification?");
                    TextView heading = v.findViewById(R.id.dialog_heading);
                    heading.setText("Selected notifications will be deleted");

                    Button delete = v.findViewById(R.id.dialog_confirm_btn);
                    delete.setText("Delete");
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MySQLiteHelper db = new MySQLiteHelper(c);
                            db.deleteCategory(arrayList.get(getAdapterPosition()).getCategoryID());
                            arrayList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());

                            Toast.makeText(c, "Notification successfully deleted", Toast.LENGTH_SHORT).show();
                            alert.dismiss();
                        }
                    });

                    v.findViewById(R.id.dialog_cancel_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                        }
                    });

                    alert.show();

                }
            });
        }
    }
}
