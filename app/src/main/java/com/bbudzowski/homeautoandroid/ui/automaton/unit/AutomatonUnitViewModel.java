package com.bbudzowski.homeautoandroid.ui.automaton.unit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;

public class AutomatonUnitViewModel extends ViewModel {
    private final MutableLiveData<AutomatonEntity> automaton;

    public AutomatonUnitViewModel(MainActivity mainActivity, String name, boolean isNew) {
        automaton = new MutableLiveData<>();
        if (!isNew)
            automaton.setValue(mainActivity.getAutomaton(name));
        else {
            AutomatonEntity auto = new AutomatonEntity();
            auto.name = "Nowy automat";
            auto.sens = new SensorEntity();
            auto.sens.device = new DeviceEntity();
            auto.sens.name = "Wybierz";
            auto.sens.device.location = "czujnik";
            auto.val_top = 0f;
            auto.val_bot = 0f;
            auto.acts = new SensorEntity();
            auto.acts.device = new DeviceEntity();
            auto.acts.name = "Wybierz";
            auto.acts.device.location = "czujnik";
            auto.state_up = 1;
            auto.state_down = 0;
            automaton.setValue(auto);
        }
    }

    public MutableLiveData<AutomatonEntity> getAutomaton() {
        return automaton;
    }
}
