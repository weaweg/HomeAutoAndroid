package com.bbudzowski.homeautoandroid.ui.sensor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.api.MeasurementApi;
import com.bbudzowski.homeautoandroid.api.SensorApi;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

public class SensorListFragment extends ListFragment {
    private final SensorApi sensorApi = new SensorApi();
    private final DeviceApi deviceApi = new DeviceApi();
    private final MeasurementApi measApi = new MeasurementApi();
    private List<SensorEntity> sensors;

    public SensorListFragment() {
        getSensors();
        lastUpdateTime = sensorApi.getUpdateTime();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        createUi(root);
        handler = new Handler(Looper.getMainLooper(), msg -> {
            root.removeAllViews();
            createUi(root);
            return false;
        });

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Timestamp updateTimeSensor = sensorApi.getUpdateTime();
                Timestamp updateTimeDevice = deviceApi.getUpdateTime();
                Timestamp updateTimeMeasurement = measApi.getUpdateTime();
                Timestamp updateTime;
                if(updateTimeSensor.compareTo(updateTimeDevice) > 0) {
                    if (updateTimeSensor.compareTo(updateTimeMeasurement) > 0)
                        updateTime = updateTimeSensor;
                    else
                        updateTime = updateTimeMeasurement;
                } else
                    if(updateTimeDevice.compareTo(updateTimeMeasurement) > 0)
                        updateTime = updateTimeDevice;
                    else
                        updateTime = updateTimeMeasurement;
                if (lastUpdateTime != null && lastUpdateTime.compareTo(updateTime) >= 0)
                    return;
                getSensors();
                handler.obtainMessage(0).sendToTarget();
                lastUpdateTime = updateTime;
            }
        };
        return root;
    }

    private void getSensors() {
        sensors = sensorApi.getSensors();
        for(SensorEntity sens : sensors) {
            sens.device = deviceApi.getDevice(sens.device_id);
            sens.lastMeasurement = measApi.
                    getLastMeasurementForSensor(sens.device_id, sens.sensor_id);
        }
    }

    private void createUi(ConstraintLayout root) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
