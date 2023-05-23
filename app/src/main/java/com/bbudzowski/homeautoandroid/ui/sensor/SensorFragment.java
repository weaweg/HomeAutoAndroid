package com.bbudzowski.homeautoandroid.ui.sensor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import java.util.List;

public class SensorFragment extends ListFragment<SensorEntity> {

    private FragmentListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SensorViewModel sensorViewModel =
                new ViewModelProvider(this).get(SensorViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        final Observer<List<SensorEntity>> sensorObserver =
                sens -> updateUI(root, sens);
        sensorViewModel.getSensors().observe(getViewLifecycleOwner(), sensorObserver);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
