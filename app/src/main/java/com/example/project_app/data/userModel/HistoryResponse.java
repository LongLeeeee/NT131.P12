package com.example.project_app.data.userModel;

import com.example.project_app.CardItem;

import java.util.List;

public class HistoryResponse {
    private int page;
    private int totalHistory;
    private List<CardItem> histories;

    public int getTotalHistory() {
        return totalHistory;
    }

    public void setTotalHistory(int totalHistory) {
        this.totalHistory = totalHistory;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<CardItem> getHistories() {
        return histories;
    }

    public void setHistories(List<CardItem> histories) {
        this.histories = histories;
    }
}
