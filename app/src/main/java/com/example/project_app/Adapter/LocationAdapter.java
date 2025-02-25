package com.example.project_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_app.Mapdata.LocationSearch;
import com.example.project_app.Mapdata.OnLocationClickListener;
import com.example.project_app.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>{
    private final List<LocationSearch> locationSearchList;
    private final OnLocationClickListener listener;

    public LocationAdapter(List<LocationSearch> locationSearches, OnLocationClickListener listener){

        this.locationSearchList = locationSearches;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        LocationSearch locationSearch = locationSearchList.get(i);
        viewHolder.nameTextView.setText(locationSearch.getName());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLocationClick(locationSearch);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return locationSearchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.locationName);
        }
    }
}

