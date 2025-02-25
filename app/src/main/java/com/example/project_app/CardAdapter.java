package com.example.project_app;

import android.content.Context;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;
    private List<Object> items;

    public CardAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }
    public void updateList(List<Object> newList) {
        items = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.header.setText((String) items.get(position));
        } else {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            CardItem cardItem = (CardItem) items.get(position);

            itemHolder.address.setText(cardItem.getAddress());
            itemHolder.date.setText(cardItem.getDate());
            itemHolder.size.setText(cardItem.getSize());
            itemHolder.status.setText(cardItem.getStatus());

            if ("Large".equals(cardItem.getSize())) {
                itemHolder.size.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else if ("Small".equals(cardItem.getSize())) {
                itemHolder.size.setTextColor(ContextCompat.getColor(context, R.color.light_green));
            } else {
                itemHolder.size.setTextColor(ContextCompat.getColor(context, R.color.yellow));
            }

            if ("Rejected".equals(cardItem.getStatus())) {
                itemHolder.status.setBackground(ContextCompat.getDrawable(context, R.drawable.background_rejected));
                itemHolder.status.setTextColor(ContextCompat.getColor(context, R.color.pink));
            } else if ("Pending".equals(cardItem.getStatus())) {
                itemHolder.status.setBackground(ContextCompat.getDrawable(context, R.drawable.background_pending));
                itemHolder.status.setTextColor(ContextCompat.getColor(context, R.color.dark_yellow));
            } else {
                itemHolder.status.setBackground(ContextCompat.getDrawable(context, R.drawable.background_accepted));
                itemHolder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
            }

            itemHolder.item_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailHistory.class);
                    intent.putExtra("item_id", cardItem.getId_pothole());
                    intent.putExtra("item_address", cardItem.getAddress());
                    intent.putExtra("item_date", cardItem.getDate());
                    intent.putExtra("item_size", cardItem.getSize());
                    intent.putExtra("item_status", cardItem.getStatus());
                    intent.putExtra("item_time", cardItem.getTime());
                    context.startActivity(intent);
                }
            });
        }
    }

    public void clearData(){
        if (items != null){
            items.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        HeaderViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView address, date, size, status;
        FrameLayout item_detail;

        ItemViewHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            date = itemView.findViewById(R.id.date);
            size = itemView.findViewById(R.id.Size);
            status = itemView.findViewById(R.id.status);
            item_detail = itemView.findViewById(R.id.item_detail);
        }
    }
}