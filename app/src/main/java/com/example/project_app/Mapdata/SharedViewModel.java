package com.example.project_app.Mapdata;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<LocationSearch> locationData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSharing = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isAlert = new MutableLiveData<>();


    public void setLocation(LocationSearch locationSearch) {
        locationData.setValue(locationSearch);
    }

    public LiveData<LocationSearch> getLocation() {
        return locationData;
    }
    public LiveData<Boolean> getIsSharing() {
        return isSharing;
    }

    // Setter cho isSharing
    public void setIsSharing(boolean sharing) {
        isSharing.setValue(sharing);
    }

    // Getter cho isAlert
    public LiveData<Boolean> getIsAlert() {
        return isAlert;
    }

    // Setter cho isAlert
    public void setIsAlert(boolean alert) {
        isAlert.setValue(alert);
    }
}
