package com.bbudzowski.homeautoandroid.ui.sensor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import java.util.List;

public class SensorListFragment extends ListFragment {

    private FragmentListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SensorListViewModel sensorListViewModel =
                new ViewModelProvider(this).get(SensorListViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        final Observer<List<SensorEntity>> sensorObserver = sensors -> {
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
    };
        sensorListViewModel.getSensors().observe(getViewLifecycleOwner(), sensorObserver);
        return root;
    }

    private ConstraintLayout createSensorView(View root, SensorEntity sens) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);
        addTextView(view, sens.device.name, "sensName");
        if(sens.device.location == null)
            sens.device.location = "brak lokacji";
        addTextView(view, sens.device.location, "sensLoc");
        if(sens.data_type == 0) {
            if(sens.lastMeasurement != null) {
                addTextView(view, sens.lastMeasurement.val.toString(), "sensVal");
                addTextView(view, sens.lastMeasurement.m_time.toString(), "sensTime");
            }
        } else {
            addTextView(view, sens.current_state.toString(), "sensState");
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
