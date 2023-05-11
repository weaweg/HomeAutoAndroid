package com.bbudzowski.homeautoandroid.ui.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.databinding.FragmentDeviceBinding;
import com.bbudzowski.homeautoandroid.tables.Device;

import java.util.List;

public class DeviceFragment extends Fragment {

    private FragmentDeviceBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DeviceViewModel deviceViewModel =
                new ViewModelProvider(this).get(DeviceViewModel.class);

        binding = FragmentDeviceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Observer<List<Device>> deviceObserver = new Observer<List<Device>>() {

            @Override
            public void onChanged(final List<Device> device) {

            }
        };
        deviceViewModel.getDevices().observe(getViewLifecycleOwner(), deviceObserver);
        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}