package com.centennial.notification.hub.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centennial.notification.hub.other.MySQLiteHelper;
import com.centennial.notification.hub.R;
import com.centennial.notification.hub.activity.InstalledAppList;
import com.centennial.notification.hub.activity.MainActivity;
import com.centennial.notification.hub.model.GroupDataClass;

import java.util.ArrayList;


public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder> {

    Context c;
    ArrayList<GroupDataClass> arrayList;

    public GroupListAdapter(Context c, ArrayList<GroupDataClass> arrayList) {
        this.c = c;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.grouplist_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.group_Name.setText(arrayList.get(position).getGroup_Name());
        if (arrayList.get(position).getGroup_Name().equals("All")) {
            holder.deleteGroup.setVisibility(View.GONE);
        } else {
            holder.deleteGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView group_Name;
        ImageView renameGroup, deleteGroup;
        LinearLayout group_list_row;

        public MyViewHolder(View itemView) {
            super(itemView);

            group_Name = itemView.findViewById(R.id.group_Name);
            renameGroup = itemView.findViewById(R.id.renameGroup);
            deleteGroup = itemView.findViewById(R.id.deleteGroup);
            group_list_row = itemView.findViewById(R.id.group_list_row);

            group_list_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String groupname = arrayList.get(getAdapterPosition()).getGroup_Name();

                    Intent i = new Intent(c, InstalledAppList.class);
                    i.putExtra("groupname", groupname);
                    c.startActivity(i);

                }
            });

            renameGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog alert = new AlertDialog.Builder(c).create();
                    View v = LayoutInflater.from(c).inflate(R.layout.dialog_design, null);
                    alert.setView(v);

                    TextView title = v.findViewById(R.id.dialog_title);
                    title.setText("Rename Group");
                    TextView heading = v.findViewById(R.id.dialog_heading);
                    heading.setText("Group Name");
                    final EditText et = v.findViewById(R.id.dialog_edittext);
                    et.setHint("Enter new group name");
                    et.setVisibility(View.VISIBLE);

                    v.findViewById(R.id.dialog_confirm_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String oldName = arrayList.get(getAdapterPosition()).getGroup_Name();
                            String newName = et.getText().toString().trim();

                            if (newName.isEmpty()) {
                                Toast.makeText(c, "Group name is empty", Toast.LENGTH_SHORT).show();
                            } else {
                                MySQLiteHelper helper = new MySQLiteHelper(c);
                                if (helper.UpdateGroupName(arrayList.get(getAdapterPosition()).getGroup_Name(), newName)) {
                                    arrayList.set(getAdapterPosition(), new GroupDataClass(newName));
                                    notifyItemChanged(getAdapterPosition());

                                    Toast.makeText(c, "Group name updated successfully", Toast.LENGTH_SHORT).show();

//                                                // Change the values of mainactivity.
                                    int position = 0;
                                    for (int i = 0; i < MainActivity.title_arrayList.size(); i++) {
                                        if (MainActivity.title_arrayList.get(i).getGroup_Name().equals(oldName)) {
                                            position = i;
//                                                        Log.e("position", String.valueOf(i));
                                        }
                                    }
                                    MainActivity.title_arrayList.set(position, new GroupDataClass(newName));
                                    MainActivity.pagerAdapter.renameGroupName(position, newName);
                                    MainActivity.pagerAdapter.notifyDataSetChanged();
                                }

                            }
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


            deleteGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final AlertDialog alert = new AlertDialog.Builder(c).create();
                    View v = LayoutInflater.from(c).inflate(R.layout.dialog_design, null);
                    alert.setView(v);

                    TextView title = v.findViewById(R.id.dialog_title);
                    title.setText("Delete Group?");
                    TextView heading = v.findViewById(R.id.dialog_heading);
                    heading.setText("Selected group will be deleted.");

                    Button delete = v.findViewById(R.id.dialog_confirm_btn);
                    delete.setText("Delete");
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // remove tab from main activity
                            int position = 0;
                            for (int i = 0; i < MainActivity.title_arrayList.size(); i++) {
                                if (MainActivity.title_arrayList.get(i).getGroup_Name().equals(arrayList.get(getAdapterPosition()).getGroup_Name())) {
                                    position = i;
                                }
                            }

                            MySQLiteHelper helper = new MySQLiteHelper(c);
                            boolean isDeleted = helper.RemoveGroup(arrayList.get(getAdapterPosition()).getGroup_Name());

                            if (isDeleted) {
                                arrayList.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());

                                MainActivity.title_arrayList.remove(position);
                                MainActivity.pagerAdapter.removeFrag(position);
                                MainActivity.pagerAdapter.notifyDataSetChanged();
                            }
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
