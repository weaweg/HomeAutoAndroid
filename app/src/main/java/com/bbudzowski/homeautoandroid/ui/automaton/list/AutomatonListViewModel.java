package com.bbudzowski.homeautoandroid.ui.automaton.list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.api.AutomatonApi;
import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;

import java.sql.Timestamp;
import java.util.List;

public class AutomatonListViewModel extends ViewModel {
    private final MutableLiveData<List<AutomatonEntity>> automatons;
    private Timestamp lastUpdateTime;

    public AutomatonListViewModel() {
        automatons = new MutableLiveData<>();
        automatons.setValue(AutomatonApi.getAutomatons());
        lastUpdateTime = AutomatonApi.getUpdateTime();
    }
    public MutableLiveData<List<AutomatonEntity>> getAutomatons() {
        return automatons;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
