package com.bbudzowski.homeautoandroid.ui.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.databinding.FragmentUnitBinding;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.UnitFragment;

public class DeviceUnitFragment extends UnitFragment<DeviceEntity> {

    private FragmentUnitBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        DeviceUnitViewModel deviceUnitViewModel =
                new ViewModelProvider(this).get(DeviceUnitViewModel.class);

        binding = FragmentUnitBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        final Observer<DeviceEntity> deviceObserver =
                dev -> updateUI(root, dev);
        deviceUnitViewModel.getDevice().observe(getViewLifecycleOwner(), deviceObserver);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}