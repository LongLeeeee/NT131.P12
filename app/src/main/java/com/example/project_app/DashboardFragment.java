package com.example.project_app;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.transition.TransitionManager;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.HistoryResponse;
import com.example.project_app.data.userModel.Monitor;
import com.example.project_app.data.userModel.NormalUser;
import com.example.project_app.data.userModel.UserSignInWithFacebook;
import com.example.project_app.data.userModel.UserSignInWithGoogle;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardFragment extends Fragment {
    private BarChart hoursBarChart;
    private PieChart potholePieChart;
    private BarChart distanceBarChart;
    private View hoursPickerLayout;
    private View potholePickerLayout;
    private View distancePickerLayout;

    private TextView tv_address, tv_date, tv_size, tv_status, view_all;
    private String address, date, size;

    private NormalUser userdata;
    private UserSignInWithGoogle userSignInWithGoogle;
    private UserSignInWithFacebook userSignInWithFacebook;
    private ProgressBar progressBar, progressBar_hou, progressBar_pothole, progressBar_dis;
    TextView username, email;
    String usern;
    String usere;

    private TextView title_hours,title_pothole, title_distance;
    private CardView recently_card;
    private TextView tv_total_hours, tv_total_pothole, tv_total_distance;

    private LinearLayout hou_total, pothole_uploaded_total, dis_total;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        hoursBarChart = view.findViewById(R.id.hours_barChart);
        potholePieChart = view.findViewById(R.id.pothole_pieChart);
        distanceBarChart = view.findViewById(R.id.distance_barChart);
        username = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.email);
        tv_status = view.findViewById(R.id.status);
        recently_card = view.findViewById(R.id.most_recent_card);
        view_all = view.findViewById(R.id.view_all);

        tv_address = view.findViewById(R.id.address);
        tv_date = view.findViewById(R.id.date);
        tv_size = view.findViewById(R.id.Size);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar_hou = view.findViewById(R.id.progress_bar_hou);
        progressBar_dis = view.findViewById(R.id.progress_bar_dis);
        progressBar_pothole = view.findViewById(R.id.progress_bar_pothole);
        hou_total = view.findViewById(R.id.total_hou_label);
        pothole_uploaded_total = view.findViewById(R.id.total_pothole_label);
        dis_total = view.findViewById(R.id.total_distan_label);


        title_hours = view.findViewById(R.id.title_hours);
        title_pothole = view.findViewById(R.id.title_pothole);
        title_distance = view.findViewById(R.id.title_distance);

        tv_total_distance = view.findViewById(R.id.totalKms);
        tv_total_hours = view.findViewById(R.id.totalHours);
        tv_total_pothole = view.findViewById(R.id.totalPotholes);

        progressBar.setVisibility(View.VISIBLE);
        progressBar_hou.setVisibility(View.VISIBLE);
        progressBar_pothole.setVisibility(View.VISIBLE);
        progressBar_dis.setVisibility(View.VISIBLE);


        String id = "";
        if (MainActivity.type.equals("normal")){
            id = MainActivity.userdata.get_id();
        }
        else if (MainActivity.type.equals("google")){
            id = MainActivity.userSignInWithGoogle.get_id();
        }
        else if (MainActivity.type.equals("facebook")){
            id = MainActivity.userSignInWithFacebook.get_id();
        }

        try {
            ApiService.apiService.getHistory(MainActivity.token,id,1, 1).enqueue(new Callback<HistoryResponse>() {
                @Override
                public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                    if (response.body().getHistories().size() != 0){
                        view_all.setVisibility(View.VISIBLE);
                        recently_card.setVisibility(View.VISIBLE);
                        List<CardItem> historyRecently = response.body().getHistories();
                        for (int i = 0; i < response.body().getHistories().size(); i++) {
                            address = historyRecently.get(i).getAddress();
                            date = historyRecently.get(i).getDate();
                            int temp = Integer.parseInt(historyRecently.get(i).getSize());
                            if (temp == 1){
                                size = "Small";
                                historyRecently.get(i).setSize("Small");
                            }
                            else if (temp == 2){
                                size = "Medium";
                                historyRecently.get(i).setSize("Medium");
                            }
                            else if (temp == 3){
                                size = "Large";
                                historyRecently.get(i).setSize("Large");
                            }
                            tv_address.setText(historyRecently.get(i).getAddress());
                            tv_date.setText(historyRecently.get(i).getDate());
                            tv_size.setText(historyRecently.get(i).getSize());
                        }
                        progressBar.setVisibility(View.GONE);
                        tv_status.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onFailure(Call<HistoryResponse> call, Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (MainActivity.type.equals("normal")) {
            username.setText(MainActivity.userdata.getUsername());
            email.setText(MainActivity.userdata.getEmail());
        } else if (MainActivity.type.equals("facebook")) {
            username.setText(MainActivity.userSignInWithFacebook.getName());
            email.setText(MainActivity.userSignInWithFacebook.getEmail());
        } else {
            username.setText(MainActivity.userSignInWithGoogle.getUsername());
            email.setText(MainActivity.userSignInWithGoogle.getEmail());
        }

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        String selectedMonth = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String selectedYear = today.getYear() + "";
        try {
            ApiService.apiService.getHoursOfUseData(MainActivity.token, "hours", id, selectedMonth, selectedYear).enqueue(new Callback<ReceiveHoursOfUse>() {
                @Override
                public void onResponse(Call<ReceiveHoursOfUse> call, Response<ReceiveHoursOfUse> response) {
                    if (response.code() == 200){
                        double total = Math.round((response.body().getFirst_week() + response.body().getSecond_week() +response.body().getThird_week() + response.body().getFourth_week()) * 10.0) / 10.0;
                        tv_total_hours.setText(total + "");
                        progressBar_hou.setVisibility(View.GONE);
                        hou_total.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ReceiveHoursOfUse> call, Throwable throwable) {
                    progressBar_hou.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            ApiService.apiService.getPotholeDataByMonth(MainActivity.token, id, selectedMonth, selectedYear).enqueue(new Callback<PotholeDataByMonth>() {
                @Override
                public void onResponse(Call<PotholeDataByMonth> call, Response<PotholeDataByMonth> response) {
                    if (response.code() == 200) {
                        int total = response.body().getLarge() + response.body().getMedium() + response.body().getSmall();
                        tv_total_pothole.setText(total + "");
                        progressBar_pothole.setVisibility(View.GONE);
                        pothole_uploaded_total.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<PotholeDataByMonth> call, Throwable throwable) {
                    progressBar_pothole.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            ApiService.apiService.getDistance(MainActivity.token, "distance", id, selectedMonth, selectedYear).enqueue(new Callback<RecievedDistance>() {
                @Override
                public void onResponse(Call<RecievedDistance> call, Response<RecievedDistance> response) {
                    if (response.code() == 200) {
                        double total = Math.round((response.body().getFirst_week() + response.body().getSecond_week() + response.body().getThird_week() + response.body().getFourth_week()) * 100.0) / 100.0;
                        tv_total_distance.setText(total + "");
                        progressBar_dis.setVisibility(View.GONE);
                        dis_total.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onFailure(Call<RecievedDistance> call, Throwable throwable) {
                    progressBar_dis.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new HistoryFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

//
//        if (!MainActivity.type.equals("normal")){
////            potholeBarChart();
//            distanceBarChart();
//        }

        hoursPickerLayout = view.findViewById(R.id.hours_pickerlayout);
        hoursPickerLayout.setOnClickListener(v -> showDatePickerBottomSheet("hours"));

        CardView hoursCard = view.findViewById(R.id.hours_card);
        View hoursContent = view.findViewById(R.id.hours_content);
        hoursCard.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) view, new Fade());
            if (hoursContent.getVisibility() == View.GONE) {
                hoursContent.setVisibility(View.VISIBLE);
            } else {
                hoursContent.setVisibility(View.GONE);
            }
        });

        potholePickerLayout = view.findViewById(R.id.pothole_pickerlayout);
        potholePickerLayout.setOnClickListener(v -> showDatePickerBottomSheet("pothole"));

        CardView potholeCard = view.findViewById(R.id.pothole_card);
        View potholeContent = view.findViewById(R.id.pothole_content);
        potholeCard.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) view, new Fade());
            if (potholeContent.getVisibility() == View.GONE) {
                potholeContent.setVisibility(View.VISIBLE);
            } else {
                potholeContent.setVisibility(View.GONE);
            }
        });

        distancePickerLayout = view.findViewById(R.id.distance_pickerlayout);
        distancePickerLayout.setOnClickListener(v -> showDatePickerBottomSheet("distance"));

        CardView distanceCard = view.findViewById(R.id.distance_card);
        View distanceContent = view.findViewById(R.id.distance_content);
        distanceCard.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition((ViewGroup) view, new Fade());
            if (distanceContent.getVisibility() == View.GONE) {
                distanceContent.setVisibility(View.VISIBLE);
            } else {
                distanceContent.setVisibility(View.GONE);
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            userdata = (NormalUser) bundle.getSerializable("user_data");

            if (userdata != null) {

                usern = userdata.getUsername();
                usere = userdata.getEmail();
                username.setText(usern);
                email.setText(usere);


            } else {
                //Log.e("DashboardFragment", "userdata is null");
                Toast.makeText(getActivity(), "Dữ liệu người dùng không được tìm thấy", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("DashboardFragment", "Bundle is null");
        }
    }

    private TextView currentlySelectedMonth;

    private void showDatePickerBottomSheet(String type) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.fragment_bottom_sheet_dialog);

        TextView tvYear = bottomSheetDialog.findViewById(R.id.tvYear);
        ImageButton btnPreviousYear = bottomSheetDialog.findViewById(R.id.btnPreviousYear);
        ImageButton btnNextYear = bottomSheetDialog.findViewById(R.id.btnNextYear);
        Button btnClose = bottomSheetDialog.findViewById(R.id.btnClose);
        Button btnApply = bottomSheetDialog.findViewById(R.id.btnApply);
        Calendar calendar = Calendar.getInstance();

        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        tvYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        btnPreviousYear.setOnClickListener(v -> {
            calendar.add(Calendar.YEAR, -1);
            tvYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        });

        btnNextYear.setOnClickListener(v -> {
            calendar.add(Calendar.YEAR, 1);
            tvYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        });

        btnClose.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedMonth = currentlySelectedMonth.getText().toString();
                selectedMonth = selectedMonth.substring(0, 3);
                String selectedYear = tvYear.getText().toString();
                String id = "";
                if (MainActivity.type.equals("normal")){
                    id = MainActivity.userdata.get_id();
                }
                else if (MainActivity.type.equals("google")){
                    id = MainActivity.userSignInWithGoogle.get_id();
                }
                else if (MainActivity.type.equals("facebook")){
                    id = MainActivity.userSignInWithFacebook.get_id();
                }
                if (type == "hours"){
                    try {
                        ApiService.apiService.getHoursOfUseData(MainActivity.token, "hours", id, selectedMonth, selectedYear).enqueue(new Callback<ReceiveHoursOfUse>() {
                            @Override
                            public void onResponse(Call<ReceiveHoursOfUse> call, Response<ReceiveHoursOfUse> response) {
                                if (response.code() == 200){
                                    double total = Math.round((response.body().getFirst_week() + response.body().getSecond_week() +response.body().getThird_week() + response.body().getFourth_week()) * 10.0) / 10.0;
                                    tv_total_hours.setText(total + "");
                                    hoursBarChart(response.body().getFirst_week(), response.body().getSecond_week(), response.body().getThird_week(), response.body().getFourth_week());
                                    title_hours.setText(currentlySelectedMonth.getText().toString());
                                    bottomSheetDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<ReceiveHoursOfUse> call, Throwable throwable) {

                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (type == "pothole"){
                    try{
                        ApiService.apiService.getPotholeDataByMonth(MainActivity.token, id, selectedMonth, selectedYear).enqueue(new Callback<PotholeDataByMonth>() {
                            @Override
                            public void onResponse(Call<PotholeDataByMonth> call, Response<PotholeDataByMonth> response) {
                                if (response.code() == 200) {
                                    int total = response.body().getLarge() + response.body().getMedium() + response.body().getSmall();
                                    tv_total_pothole.setText(total + "");
                                    potholeBarChart(response.body().getSmall(), response.body().getMedium(), response.body().getLarge());
                                    title_pothole.setText(currentlySelectedMonth.getText().toString());
                                    bottomSheetDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<PotholeDataByMonth> call, Throwable throwable) {

                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (type == "distance"){
                    try {
                        ApiService.apiService.getDistance(MainActivity.token, "distance", id, selectedMonth, selectedYear).enqueue(new Callback<RecievedDistance>() {
                            @Override
                            public void onResponse(Call<RecievedDistance> call, Response<RecievedDistance> response) {
                                if (response.code() == 200) {
                                    double total = Math.round((response.body().getFirst_week() + response.body().getSecond_week() + response.body().getThird_week() + response.body().getFourth_week()) * 100.0) / 100.0;
                                    tv_total_distance.setText(total + "");
                                    title_distance.setText(currentlySelectedMonth.getText().toString());
                                    distanceBarChart(response.body().getFirst_week(), response.body().getSecond_week(), response.body().getThird_week(), response.body().getFourth_week());
                                    bottomSheetDialog.dismiss();
                                }
                            }
                            @Override
                            public void onFailure(Call<RecievedDistance> call, Throwable throwable) {

                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        int currentMonth = calendar.get(Calendar.MONTH);

        setMonthClickListener(bottomSheetDialog, R.id.tvJanuary, 0, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvFebruary, 1, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvMarch, 2, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvApril, 3, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvMay, 4, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvJune, 5, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvJuly, 6, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvAugust, 7, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvSeptember, 8, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvOctober, 9, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvNovember, 10, currentMonth);
        setMonthClickListener(bottomSheetDialog, R.id.tvDecember, 11, currentMonth);

        bottomSheetDialog.show();
    }

    private void setMonthClickListener(BottomSheetDialog dialog, int textViewId, int monthIndex, int currentMonth) {
        TextView textView = dialog.findViewById(textViewId);
        if (monthIndex == currentMonth) {
            textView.setBackgroundResource(R.drawable.selected_month_background);
            textView.setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.white));
            currentlySelectedMonth = textView;
        }

        textView.setOnClickListener(v -> {
            if (currentlySelectedMonth != null) {
                currentlySelectedMonth.setBackgroundColor(Color.TRANSPARENT);
                currentlySelectedMonth.setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.black));
            }

            textView.setBackgroundResource(R.drawable.selected_month_background);
            textView.setTextColor(ContextCompat.getColor(dialog.getContext(), R.color.white));
            currentlySelectedMonth = textView;
        });
    }


    private void hoursBarChart(float first, float second, float third, float fourth) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, first)); //1st week
        entries.add(new BarEntry(1, second)); //2nd week
        entries.add(new BarEntry(2, third)); //3rd week
        entries.add(new BarEntry(3, fourth)); //4th week


        BarDataSet dataSet = new BarDataSet(entries, "Hours of use");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setGradientColor(Color.parseColor("#89FBD7"), Color.parseColor("#FFAE00"));
        dataSet.setValueTextColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        hoursBarChart.setData(data);
        hoursBarChart.setFitBars(true);
        hoursBarChart.getDescription().setEnabled(false);
        hoursBarChart.getLegend().setEnabled(false);

        String[] weeks = new String[]{"1st week", "2nd week", "3rd week", "4th week"};
        XAxis xAxis = hoursBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weeks));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weeks));
        xAxis.setTextColor(Color.parseColor("#266665"));
        xAxis.setTextSize(10f);
        xAxis.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat));

        YAxis yAxisLeft = hoursBarChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setTextColor(Color.parseColor("#266665"));
        yAxisLeft.setTextSize(12f);

        hoursBarChart.getAxisRight().setEnabled(false);

        hoursBarChart.setExtraOffsets(5, 5, 5, 5);

        hoursBarChart.invalidate();
    }

    private void potholeBarChart(int small, int medium, int large) {
        boolean[] isNull = {true, true, true};
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (small != 0) {
            entries.add(new PieEntry(small, "Small")); //Small
            isNull[0] = false;
        }
        if (medium != 0) {
            entries.add(new PieEntry(medium, "Medium")); //Medium
            isNull[1] = false;
        }
        if (large != 0) {
            entries.add(new PieEntry(large, "Large")); //Large
            isNull[2] = false;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Pothole uploaded");
        if (entries.size() == 3){
            dataSet.setColors(new int[]{Color.parseColor("#14D10A"), Color.parseColor("#FFBA1B"), Color.parseColor("#EA0404")});
        }
        else if (!isNull[0] && !isNull[1] && isNull[2]){
            dataSet.setColors(new int[]{Color.parseColor("#14D10A"), Color.parseColor("#FFBA1B")});
        }
        else if (!isNull[0] && isNull[1] && !isNull[2]){
            dataSet.setColors(new int[]{Color.parseColor("#14D10A"), Color.parseColor("#EA0404")});
        }
        else if (isNull[0] && !isNull[1] && !isNull[2]){
            dataSet.setColors(new int[]{Color.parseColor("#FFBA1B"), Color.parseColor("#EA0404")});
        }
        else if (!isNull[0] && isNull[1] && isNull[2]){
            dataSet.setColors(new int[]{Color.parseColor("#14D10A")});
        }
        else if(isNull[0] && !isNull[1] && isNull[2]){
            dataSet.setColors(new int[]{ Color.parseColor("#FFBA1B")});
        }
        else if(isNull[0] && isNull[1] && !isNull[2]){
            dataSet.setColors(new int[]{Color.parseColor("#EA0404")});
        }
        dataSet.setValueTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        dataSet.setValueTextSize(10f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        potholePieChart.setData(data);
        potholePieChart.getDescription().setEnabled(false);
        potholePieChart.getLegend().setEnabled(false);

        potholePieChart.setExtraOffsets(10, 10, 10, 10);
        potholePieChart.invalidate();

    }

    private void distanceBarChart(float first, float second, float third, float fourth) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, first)); //1st week
        entries.add(new BarEntry(1, second)); //2nd week
        entries.add(new BarEntry(2, third)); //3rd week
        entries.add(new BarEntry(3, fourth)); //4th week

        BarDataSet dataSet = new BarDataSet(entries, "Distance");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setGradientColor(Color.parseColor("#89FBD7"), Color.parseColor("#FFAE00"));
        dataSet.setValueTextColor(ResourcesCompat.getColor(getResources(), R.color.green, null));
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        distanceBarChart.setData(data);
        distanceBarChart.setFitBars(true);
        distanceBarChart.getDescription().setEnabled(false);
        distanceBarChart.getLegend().setEnabled(false);

        String[] weeks = new String[]{"1st week", "2nd week", "3rd week", "4th week"};
        XAxis xAxis = distanceBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weeks));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(weeks));
        xAxis.setTextColor(Color.parseColor("#266665"));
        xAxis.setTextSize(10f);
        xAxis.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat));

        YAxis yAxisLeft = distanceBarChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setTextColor(Color.parseColor("#266665"));
        yAxisLeft.setTextSize(12f);

        distanceBarChart.getAxisRight().setEnabled(false);

        distanceBarChart.setExtraOffsets(5, 5, 5, 5);

        distanceBarChart.invalidate();
    }
}