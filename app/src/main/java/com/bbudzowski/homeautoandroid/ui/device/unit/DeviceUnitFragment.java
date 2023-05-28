package com.bbudzowski.homeautoandroid.ui.device.unit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentUnitBinding;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.fragments.UnitFragment;

public class DeviceUnitFragment extends UnitFragment {
    private DeviceUnitViewModel model;
    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentUnitBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        model = new DeviceUnitViewModel(mainActivity, args.getString("device_id"));
        getViewModelStore().put("deviceUnit", model);
        final Observer<DeviceEntity> deviceObserver = device -> {
            root.invalidate();
            createDeviceView(root, device);
        };
        model.getDevice().observe(getViewLifecycleOwner(), deviceObserver);
        return root;
    }

    private void createDeviceView(ConstraintLayout root, DeviceEntity device) {
        ConstraintLayout view = (ConstraintLayout) root.getViewById(R.id.unit_layout);
        if(device.location == null)
            device.location = "Brak lokacji";
        addTextView(view, device.name,48f, R.color.purple_500);
        addEditTextView(view, device.location,32f, R.color.purple_500);
        constraintTextToView(view, 50);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}