package com.bbudzowski.homeautoandroid.ui.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.Device;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import java.util.List;

public class DeviceFragment extends ListFragment<Device> {

    private FragmentListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        DeviceViewModel deviceViewModel =
                new ViewModelProvider(this).get(DeviceViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        final Observer<List<Device>> deviceObserver =
                devs -> updateUI(inflater, container, root, devs);
        deviceViewModel.getDevices().observe(getViewLifecycleOwner(), deviceObserver);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}