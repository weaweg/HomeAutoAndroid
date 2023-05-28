package com.bbudzowski.homeautoandroid.ui.sensor.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.automaton.list.AutomatonListViewModel;
import com.bbudzowski.homeautoandroid.ui.fragments.ListFragment;

import org.json.JSONException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        ScrollView root = binding.getRoot();
        model = new SensorListViewModel(mainActivity);
        getViewModelStore().put("sensorList", model);
        final Observer<List<SensorEntity>> sensorObserver =
                sensors -> createSensorsUi(root.findViewById(R.id.units_list), sensors);
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
                    model.getSensors().postValue(mainActivity.getSensors());
                    model.setLastUpdateTime(updateTime);
                }
            }
        }, 0, updatePeriod);
    }

    private void createSensorsUi(ConstraintLayout root, List<SensorEntity> sensors) {
        root.removeAllViews();
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

    private ConstraintLayout createSensorView(ConstraintLayout root, SensorEntity sens) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);
        if(sens.device.location == null)
            sens.device.location = "Brak lokacji";
        if(sens.name == null)
            sens.name = "Nowe urządzenie";
        addTextView(view, sens.name,24f, R.color.purple_500);
        String txt = sens.device.location + " - ";
        if(!sens.discrete) {
            String unit;
            if(sens.json_desc != null)
                try { unit = sens.json_desc.getString("unit");
                } catch (JSONException e) { unit = ""; }
            else
                unit = "";
            String val = "---";
            if(sens.current_val != null)
                val = sens.current_val.toString();
            addTextView(view, txt + val + unit,
                    24f, R.color.purple_500);
            String status;
            int colorId;
            if(sens.m_time == null)
                sens.m_time = new Timestamp(0);
            Timestamp hourBefore = new Timestamp(Instant.now().
                    minus(1, ChronoUnit.HOURS).toEpochMilli());
            if(hourBefore.compareTo(sens.m_time) > 0) {
                status = "OFFLINE";
                colorId = R.color.red;
            }
            else {
                status = "ONLINE";
                colorId = R.color.green;
            }
            addTextView(view, status,24f, colorId);
        } else {
            String stateDesc;
            try { stateDesc = sens.json_desc.getString(sens.current_val.toString());
            } catch (JSONException e) { stateDesc = sens.current_val.toString(); }
            addTextView(view, txt + stateDesc,24f, R.color.purple_500);
        }
        constraintTextToView(view, 0);
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
