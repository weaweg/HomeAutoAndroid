package com.bbudzowski.homeautoandroid.ui.device.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.MainActivity;
import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;
import com.bbudzowski.homeautoandroid.ui.device.list.unit.DeviceUnitFragment;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceListFragment extends ListFragment {
    private MainActivity mainActivity;
    private DeviceListViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        model = new ViewModelProvider(this).get(DeviceListViewModel.class);
        model.setDevices(mainActivity);
        final Observer<List<DeviceEntity>> devicesObserver = devices -> {
            root.removeAllViews();
            genDevicesUi(root, devices);
        };
        model.getDevices().observe(getViewLifecycleOwner(), devicesObserver);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timestamp updateTime = mainActivity.getDevicesLastUpdate();
                if(updateTime == null)
                    return;
                if(updateTime.compareTo(model.getLastUpdateTime()) > 0) {
                    model.getDevices().setValue(mainActivity.getDevices());
                    model.setLastUpdateTime(updateTime);
                }
            }
        }, 0, updatePeriod);
    }

    private void genDevicesUi(ConstraintLayout root, List<DeviceEntity> devices) {
        if (devices == null || devices.size() == 0) {
            handleError(root, getString(R.string.no_results));
            return;
        }
        for (int i = 0; i < devices.size(); ++i) {
            ConstraintLayout view = createDeviceView(root, devices.get(i));
            view.setId(View.generateViewId());
            root.addView(view);
            view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT));
        }
        constraintViewsToRoot(root);
    }

    private ConstraintLayout createDeviceView(View root, DeviceEntity device) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);
        if(device.location == null)
            device.location = "Brak lokacji";
        addTextView(view, device.name,24f, R.color.teal_700);
        addTextView(view, device.location,20f, R.color.teal_700);
        constraintTextToView(view);
        view.setOnClickListener(onDeviceClick(device.device_id));
        return view;
    }

    private View.OnClickListener onDeviceClick(String device_id) {
        return view -> {
            Bundle bundle = new Bundle();
            bundle.putString("device_id", device_id);
            replaceFragment(new DeviceUnitFragment(), bundle);
        };
    }
}