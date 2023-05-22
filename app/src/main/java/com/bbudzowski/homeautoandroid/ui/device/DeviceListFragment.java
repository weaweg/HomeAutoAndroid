package com.bbudzowski.homeautoandroid.ui.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import java.util.List;

public class DeviceListFragment extends ListFragment<DeviceEntity> {

    private FragmentListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        DeviceListViewModel deviceListViewModel = new DeviceListViewModel(
                getResources().openRawResource(R.raw.server_ts));
                //new ViewModelProvider(this).get(DeviceViewModel.class, );

        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        final Observer<List<DeviceEntity>> deviceObserver =
                devs -> updateUI(root, devs);
        deviceListViewModel.getDevices().observe(getViewLifecycleOwner(), deviceObserver);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}