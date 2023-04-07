package com.centennial.notification.hub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.centennial.notification.hub.R;
import com.centennial.notification.hub.model.SaveNotificationData;

import java.util.ArrayList;

class InnerListAdapter extends RecyclerView.Adapter<InnerListAdapter.MyViewHolder> {

    Context context;
    ArrayList<SaveNotificationData> arrayList;
    OnInnerListItemClickListener objOnInnerListItemClickListener;
    int position;

    public InnerListAdapter(Context context, ArrayList<SaveNotificationData> arrayList, OnInnerListItemClickListener objOnInnerListItemClickListener, int position) {
        this.context = context;
        this.arrayList = arrayList;
        this.objOnInnerListItemClickListener = objOnInnerListItemClickListener;
        this.position = position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.inner_row_design, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.titleText.setText(arrayList.get(position).getTitle());

        if (arrayList.get(position).getMessage() != null && !arrayList.get(position).getMessage().isEmpty()) {
            holder.messageText.setText(arrayList.get(position).getMessage());
            holder.messageText.setVisibility(View.VISIBLE);
        } else {
            holder.messageText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (arrayList.size() > 5) {
            return 5;
        } else {
            return arrayList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, messageText;
        LinearLayout llInnerRow;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            messageText = itemView.findViewById(R.id.messageText);
            llInnerRow = itemView.findViewById(R.id.llInnerRow);

            llInnerRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objOnInnerListItemClickListener.OnInnerListItemClick(position);
                }
            });
        }
    }

    public interface OnInnerListItemClickListener {
        void OnInnerListItemClick(int position);
    }
}
