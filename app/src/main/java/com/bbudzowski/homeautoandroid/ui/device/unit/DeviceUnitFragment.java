package com.bbudzowski.homeautoandroid.ui.device.unit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.databinding.FragmentUnitBinding;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.ui.fragments.BasicFragment;
import com.google.android.material.snackbar.Snackbar;

public final class DeviceUnitFragment extends BasicFragment {
    protected FragmentUnitBinding binding;
    private DeviceUnitViewModel model;
    private String deviceId;
    private int editLocationId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        deviceId = args.getString("device_id");
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentUnitBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        model = new DeviceUnitViewModel(mainActivity, deviceId);
        getViewModelStore().put("deviceUnit", model);
        final Observer<DeviceEntity> deviceObserver = device ->
                createDeviceView((ConstraintLayout) root.getViewById(R.id.unit_layout), device);
        model.getDevice().observe(getViewLifecycleOwner(), deviceObserver);
        root.getViewById(R.id.save_button).setOnClickListener((view) ->
                onSaveClick((ConstraintLayout) root.getViewById(R.id.unit_layout)));
        root.getViewById(R.id.delete_button).setOnClickListener((view) ->
                onDeleteClick((ConstraintLayout) root.getViewById(R.id.unit_layout)));
        root.getViewById(R.id.delete_button).setVisibility(View.VISIBLE);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createDeviceView(ConstraintLayout root, DeviceEntity device) {
        root.removeAllViews();
        addTextView(root, device.name, 48f, R.color.purple_700);
        editLocationId = addEditTextView(root, device.location, 32f, false);
        constraintTextToView(root, 30);
    }

    public void onSaveClick(ConstraintLayout view) {
        DeviceEntity device = model.getDevice().getValue();
        EditText editText = (EditText) view.getViewById(editLocationId);
        String locationText = editText.getText().toString();
        if (!locationText.equals("")) {
            device.location = locationText;
            if (DeviceApi.updateDevice(device) == 200) {
                Snackbar.make(binding.getRoot(), "Zaktualizowano urządzenie", Snackbar.LENGTH_SHORT).show();
                previousFragment();
                return;
            }
        }
        Snackbar.make(binding.getRoot(), "Aktualizacja nieudana", Snackbar.LENGTH_SHORT).show();
    }

    public void onDeleteClick(ConstraintLayout view) {
        DeviceEntity device = model.getDevice().getValue();
        if (DeviceApi.deleteDevice(device.device_id) == 200) {
            Snackbar.make(binding.getRoot(), "Usunięto urządzenie", Snackbar.LENGTH_SHORT).show();
            previousFragment();
            return;
        }
        Snackbar.make(binding.getRoot(), "Aktualizacja nieudana", Snackbar.LENGTH_SHORT).show();
    }
}