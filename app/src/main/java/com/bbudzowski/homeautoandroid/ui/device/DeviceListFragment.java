package com.bbudzowski.homeautoandroid.ui.device;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import java.util.List;

public class DeviceListFragment extends ListFragment {

    private FragmentListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        DeviceListViewModel deviceListViewModel =
                new ViewModelProvider(this).get(DeviceListViewModel.class);

        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        final Observer<List<DeviceEntity>> deviceObserver = devices -> {
            if (devices == null || devices.size() == 0) {
                handleError(root, getString(R.string.no_results));
                return;
            }
            for (int i = 0; i < devices.size(); ++i) {
                ConstraintLayout view = createDeviceView(root, devices.get(i));
                view.setId(("box" + i).hashCode());
                root.addView(view);
                view.setLayoutParams(new ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            }
            constraintViewsToRoot(root);
        };
        deviceListViewModel.getDevices().observe(getViewLifecycleOwner(), deviceObserver);
        return root;
    }

    private ConstraintLayout createDeviceView(View root, DeviceEntity device) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);
        addTextView(view, device.name, "devName");
        if(device.location == null)
            device.location = "brak lokacji";
        addTextView(view, device.location, "devLoc");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}