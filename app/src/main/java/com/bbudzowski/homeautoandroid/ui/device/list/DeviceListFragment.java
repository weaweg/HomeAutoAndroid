package com.bbudzowski.homeautoandroid.ui.device.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.lifecycle.Observer;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.ui.fragments.BasicFragment;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public final class DeviceListFragment extends BasicFragment {
    private FragmentListBinding binding;
    private DeviceListViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentListBinding.inflate(inflater, container, false);
        ScrollView root = binding.getRoot();
        model = new DeviceListViewModel(mainActivity);
        getViewModelStore().put("deviceList", model);
        final Observer<List<DeviceEntity>> devicesObserver =
                devices -> genDevicesUi(root.findViewById(R.id.units_list), devices);
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
                if (updateTime.compareTo(model.getLastUpdateTime()) > 0) {
                    model.getDevices().postValue(mainActivity.getDevices());
                    model.setLastUpdateTime(updateTime);
                }
            }
        }, 0, updatePeriod);
    }

    @Override
    public void onPause() {
        super.onPause();
        updateTimer.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void genDevicesUi(ConstraintLayout root, List<DeviceEntity> devices) {
        root.removeAllViews();
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

    private ConstraintLayout createDeviceView(ConstraintLayout root, DeviceEntity device) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);
        addTextView(view, device.name,32f, R.color.purple_700);
        addTextView(view, device.location,24f);
        constraintTextToView(view, 0);
        view.setOnClickListener(onDeviceClick(device.device_id));
        return view;
    }

    private View.OnClickListener onDeviceClick(String device_id) {
        return view -> {
            Bundle bundle = new Bundle();
            bundle.putString("device_id", device_id);
            replaceFragment(R.id.nav_device, bundle);
        };
    }
}