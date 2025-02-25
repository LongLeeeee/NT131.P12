package com.example.project_app;

public class PotholeDataByMonth {
    private int medium;
    private int small;
    private int large;

    public int getMedium() {
        return medium;
    }

    public PotholeDataByMonth(int medium, int small, int large) {
        this.medium = medium;
        this.small = small;
        this.large = large;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getSmall() {
        return small;
    }

    public void setSmall(int small) {
        this.small = small;
    }

    public int getLarge() {
        return large;
    }

    public void setLarge(int large) {
        this.large = large;
    }
}
