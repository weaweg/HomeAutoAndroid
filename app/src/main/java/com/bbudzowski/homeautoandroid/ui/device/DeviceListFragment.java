package com.bbudzowski.homeautoandroid.ui.device;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceListFragment extends ListFragment {
    private final DeviceApi deviceApi = new DeviceApi();
    private List<DeviceEntity> devices;

    public DeviceListFragment() {
        devices = deviceApi.getDevices();
        lastUpdateTime = deviceApi.getUpdateTime();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        createUi(root);
        handler = new Handler(Looper.getMainLooper(), msg -> {
            root.removeAllViews();
            createUi(root);
            return false;
        });
        timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer");
                Timestamp updateTime = deviceApi.getUpdateTime();
                if (updateTime == null ||
                        (lastUpdateTime != null && lastUpdateTime.compareTo(updateTime) >= 0))
                    return;
                devices = deviceApi.getDevices();
                handler.obtainMessage(0).sendToTarget();
                lastUpdateTime = updateTime;
            }
        };
        return root;
    }

    private void createUi(ConstraintLayout root) {
        if (devices == null || devices.size() == 0) {
            handleError(root, getString(R.string.no_results));
            return;
        }
        for (int i = 0; i < devices.size(); ++i) {
            ConstraintLayout view = createDeviceView(root, devices.get(i));
            view.setId(("box" + i).hashCode());
            root.addView(view);
            view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT));
        }
        constraintViewsToRoot(root);
    }

    private ConstraintLayout createDeviceView(View root, DeviceEntity device) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);
        if(device.location == null)
            device.location = "Brak lokacji";
        addTextView(view, device.location, "devLoc", 24f, R.color.teal_700);
        addTextView(view, device.name, "devName", 24f, R.color.teal_700);
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