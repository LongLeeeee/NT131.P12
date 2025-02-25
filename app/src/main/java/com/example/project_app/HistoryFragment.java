package com.example.project_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.HistoryResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private List<CardItem> cardItems;
    private CardAdapter adapter;
    private List<Object> displayItems;
    private int page = 1;
    private RecyclerView recyclerView;
    private Button next_page, previous_page;
    private ProgressBar progressBar;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        next_page = view.findViewById(R.id.next_page);
        previous_page = view.findViewById(R.id.previous_page);
        progressBar = view.findViewById(R.id.progress_bar);

        cardItems = new ArrayList<>();
        displayItems = new ArrayList<>();
        loadData(page);

        next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearData();
                page++;
                loadData(page);
            }
        });
        previous_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearData();
                page--;
                loadData(page);
            }
        });

        EditText editLocation = view.findViewById(R.id.edit_location);
        editLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        return view;
    }

    private void loadData(int page) {
        progressBar.setVisibility(View.VISIBLE);
        String id = "";
        if (MainActivity.type.equals("normal")) {
            id = MainActivity.userdata.get_id();
        }
        else if (MainActivity.type.equals("google")) {
            id = MainActivity.userSignInWithGoogle.get_id();
        }
        else if (MainActivity.type.equals("facebook")) {
            id = MainActivity.userSignInWithFacebook.get_id();
        }
        try {
            ApiService.apiService.getHistory(MainActivity.token, id, page, 10).enqueue(new Callback<HistoryResponse>() {
                @Override
                public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                    if (response.code() == 200 && response.body().getHistories() != null){
                        cardItems = response.body().getHistories();
                        if(cardItems.size() < 10){
                            next_page.setVisibility(View.GONE);
                        }
                        else {
                            next_page.setVisibility(View.VISIBLE);
                        }
                        if (page > 1){
                            previous_page.setVisibility(View.VISIBLE);
                        }
                        else {
                            previous_page.setVisibility(View.GONE);
                        }
                        Map<String, List<CardItem>> groupedItems = groupItemsByMonth(cardItems);
                        for (Map.Entry<String, List<CardItem>> entry : groupedItems.entrySet()) {
                            displayItems.add(entry.getKey());
                            displayItems.addAll(entry.getValue());
                        }
                        adapter = new CardAdapter(getActivity(), displayItems);
                        recyclerView.setAdapter(adapter);
                    }
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onFailure(Call<HistoryResponse> call, Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void filter(String text) {
        List<Object> filteredList = new ArrayList<>();
        for (CardItem item : cardItems) {
            if (item.getAddress().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private Map<String, List<CardItem>> groupItemsByMonth(List<CardItem> items) {
        Map<String, List<CardItem>> groupedItems = new HashMap<>();
        for (CardItem item : items) {
            int temp = Integer.parseInt(item.getSize());
            if (temp == 1){
                item.setSize("Small");
            }
            else if (temp == 2){
                item.setSize("Medium");
            }
            else if (temp == 3){
                item.setSize("Large");
            }
            String monthYear = item.getDate().substring(8); // Extract month and year from date
            if (!groupedItems.containsKey(monthYear)) {
                groupedItems.put(monthYear, new ArrayList<>());
            }
            groupedItems.get(monthYear).add(item);
        }
        return groupedItems;
    }
}