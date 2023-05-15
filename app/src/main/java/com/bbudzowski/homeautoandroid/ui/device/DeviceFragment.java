package com.bbudzowski.homeautoandroid.ui.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentDeviceBinding;
import com.bbudzowski.homeautoandroid.tables.Device;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DeviceFragment extends Fragment {

    private FragmentDeviceBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DeviceViewModel deviceViewModel =
                new ViewModelProvider(this).get(DeviceViewModel.class);

        binding = FragmentDeviceBinding.inflate(inflater, container, false);
        ViewGroup root = binding.getRoot();
        final Observer<List<Device>> deviceObserver = devices -> {
            for(Device dev : devices) {
                TextView tmp;
                View view = inflater.inflate(R.layout.box_layout, container);
                tmp = view.findViewById(R.id.name);
                tmp.setText(dev.name);
                tmp = view.findViewById(R.id.location);
                tmp.setText(dev.location);
                root.addView(view);
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