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
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import org.json.JSONException;

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
                Timestamp updateTime = mainActivity.getSensorsLastUpdate();
                if(mainActivity.getDevicesLastUpdate().compareTo(updateTime) > 0)
                    updateTime = mainActivity.getDevicesLastUpdate();
                if(updateTime.compareTo(model.getLastUpdateTime()) > 0) {
                    model.getSensors().setValue(model.genSensorsData(mainActivity));
                    model.setLastUpdateTime(updateTime);
                }
            }
        }, 0, updatePeriod);
    }

    private void createSensorsUi(ConstraintLayout root, List<SensorEntity> sensors) {
        if (sensors == null || sensors.size() == 0) {
            handleError(root, getString(R.string.no_results));
            return;
        }
        for (int i = 0; i < sensors.size(); ++i) {
            ConstraintLayout view = createSensorView(root, sensors.get(i));
            view.setId(View.generateViewId());
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
        if(sens.name == null)
            sens.name = "Nowe urządzenie";
        String sensTitle = sens.name + " - " + sens.device.location;
        addTextView(view, sensTitle,24f, R.color.teal_700);
        if(!sens.discrete) {
            String unit;
            try { unit = sens.json_desc.getString("unit");
            } catch (JSONException e) { unit = ""; }
            addTextView(view, sens.current_val.toString() + unit,
                    24f, R.color.teal_200);
            String mTime = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss",
                    Locale.getDefault()).format(sens.m_time);
            addTextView(view, mTime,24f, R.color.teal_200);
        } else {
            String stateDesc;
            try { stateDesc = sens.json_desc.getString(sens.current_val.toString());
            } catch (JSONException e) { stateDesc = ""; }
            addTextView(view, stateDesc,24f, R.color.teal_200);
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
