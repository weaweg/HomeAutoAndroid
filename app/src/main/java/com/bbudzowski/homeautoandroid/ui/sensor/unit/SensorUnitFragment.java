package com.bbudzowski.homeautoandroid.ui.sensor.unit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.databinding.FragmentUnitBinding;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.ui.device.unit.DeviceUnitViewModel;
import com.bbudzowski.homeautoandroid.ui.fragments.BasicFragment;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SensorUnitFragment extends BasicFragment {
    protected FragmentUnitBinding binding;
    private SensorUnitViewModel model;
    private String deviceId;
    private String sensorId;
    private int editLocationId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        deviceId = args.getString("device_id");
        sensorId = args.getString("sensor_id");
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentUnitBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        model = new SensorUnitViewModel(mainActivity, deviceId, sensorId);
        getViewModelStore().put("sensorUnit", model);
        final Observer<SensorEntity> sensorObserver = sensor ->
                createSensorView((ConstraintLayout) root.getViewById(R.id.unit_layout), sensor);
        model.getSensor().observe(getViewLifecycleOwner(), sensorObserver);
        root.getViewById(R.id.save_button).setOnClickListener((view) ->
                onSaveClick((ConstraintLayout) root.getViewById(R.id.unit_layout)));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timestamp updateTime = mainActivity.getSensorsLastUpdate();
                if(updateTime.compareTo(model.getLastUpdateTime()) > 0) {
                    model.getSensor().postValue(mainActivity.getSensor(deviceId, sensorId));
                    model.setLastUpdateTime(updateTime);
                }
            }
        }, 0, updatePeriod);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createSensorView(ConstraintLayout root, SensorEntity sens) {
        root.removeAllViews();
        addTextView(root, sens.device.name,48f);
        addTextView(root, sens.device.location,32f);
        addEditTextView(root, sens.name, 32f);
        String unit = "";
        String val = "brak wartości";
        if(!sens.discrete) {
            if(sens.json_desc != null)
                try { unit = sens.json_desc.getString("unit");
                } catch (JSONException e) { unit = ""; }

            if(sens.current_val != null)
                val = sens.current_val.toString();

        } else
        if(sens.current_val != null && sens.json_desc != null) {
            val = sens.current_val.toString();
            try {
                unit = sens.json_desc.getString(val);
            } catch (JSONException e) { unit = ""; }
        }
        String discrete = "Wartości ciągłe";
        if(sens.discrete)
            discrete = "Wartości dyskretne";
        addTextView(root, discrete + " - " + val + unit,24f);
        String time = "brak czasu pomiaru";
        if(sens.m_time != null)
            time = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(sens.m_time);
        addTextView(root, time, 24f);



        constraintTextToView(root, 10);
    }

    public void onSaveClick(ConstraintLayout view) {
        /*SensorEntity sensor = model.getSensor().getValue();
        EditText editText = (EditText) view.getViewById(editLocationId);
        String locationText = editText.getText().toString();
        if(!locationText.equals("")) {
            device.location = locationText;
            if(DeviceApi.updateDevice(device) == 200) {
                //model.getDevice().setValue(device);
                Snackbar.make(binding.getRoot(), "Zaktualizowano urządzenie", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }*/
        Snackbar.make(binding.getRoot(), "Aktualizacja nieudana", Snackbar.LENGTH_SHORT).show();
    }
}
