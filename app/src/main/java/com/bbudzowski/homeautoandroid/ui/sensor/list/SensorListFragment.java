package com.bbudzowski.homeautoandroid.ui.sensor.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.MainActivity;
import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.MeasurementApi;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SensorListFragment extends ListFragment {
    private MainActivity mainActivity;
    private SensorListViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        model = new ViewModelProvider(this).get(SensorListViewModel.class);
        model.setSensors(mainActivity);
        final Observer<List<SensorEntity>> sensorObserver = sensors -> {
            root.removeAllViews();
            setSensors(sensors);
            createSensorsUi(root, sensors);
        };
        model.getSensors().observe(getViewLifecycleOwner(), sensorObserver);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timestamp tmp = model.getLastUpdateTime();
                if(mainActivity.getDevicesLastUpdate().compareTo(tmp) > 0)
                    tmp = mainActivity.getDevicesLastUpdate();
                if(mainActivity.getSensorsLastUpdate().compareTo(tmp) > 0)
                    tmp = mainActivity.getSensorsLastUpdate();




                    model.getSensors().setValue(mainActivity.getSensors());
                    updateTimeTmp = mainActivity.getSensorsLastUpdate();
                }
                if(tmp)

            }
        }, 0, updatePeriod);
    }

    private void setSensors(List<SensorEntity> sensors) {
        for(SensorEntity sens : sensors) {
            //sens.device = mainActivity.getDevices().
            sens.lastMeasurement = MeasurementApi.
                    getLastMeasurementForSensor(sens.device_id, sens.sensor_id);
        }
    }

    private void createSensorsUi(ConstraintLayout root, List<SensorEntity> sensors) {
        if (sensors == null || sensors.size() == 0) {
            handleError(root, getString(R.string.no_results));
            return;
        }
        for (int i = 0; i < sensors.size(); ++i) {
            ConstraintLayout view = createSensorView(root, sensors.get(i));
            view.setId(("box" + i).hashCode());
            root.addView(view);
            view.setLayoutParams(new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        }
        constraintViewsToRoot(root);
    }

    private ConstraintLayout createSensorView(View root, SensorEntity sens) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);
        if(sens.device.location == null)
            sens.device.location = "Brak lokacji";
        String sensName = sens.device.location;
        if(sens.device.name != null)
            sensName = sensName + " - " +sens.device.name;
        addTextView(view, sensName, "sensName", 24f, R.color.teal_700);
        if(sens.data_type == 0) {
            if(sens.lastMeasurement != null) {
                addTextView(view, sens.lastMeasurement.val.toString() + sens.units,
                        "sensVal", 24f, R.color.teal_200);
                String mTime = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss",
                        Locale.getDefault()).format(sens.lastMeasurement.m_time);
                addTextView(view, mTime, "sensTime", 24f, R.color.teal_200);
            }
        } else {
            String currentState;
            if(sens.current_state == 0)
                currentState = "Wyłączony";
            else
                currentState = "Włączony";
            addTextView(view, currentState,"sensState", 24f, R.color.teal_200);
        }
        constraintTextToView(view);
        view.setOnClickListener(onSensorClick(sens.device_id, sens.sensor_id));
        return view;
    }

    private View.OnClickListener onSensorClick(String device_id, String sensor_id) {
        return v -> {
            Bundle bundle = new Bundle();
            bundle.putString("device_id", device_id);
            bundle.putString("sensor_id", sensor_id);
            //replaceFragment(new SensorUnitFragment(), bundle);
        };
    }
}
