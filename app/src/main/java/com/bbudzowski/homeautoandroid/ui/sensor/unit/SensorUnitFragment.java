package com.bbudzowski.homeautoandroid.ui.sensor.unit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.SensorApi;
import com.bbudzowski.homeautoandroid.databinding.FragmentUnitBinding;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.ui.fragments.BasicFragment;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class SensorUnitFragment extends BasicFragment {
    protected FragmentUnitBinding binding;
    private SensorUnitViewModel model;
    private int editNameId;
    private int editValId;
    private int lineValId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        String deviceId = args.getString("device_id");
        String sensorId = args.getString("sensor_id");
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentUnitBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        model = new SensorUnitViewModel(mainActivity, deviceId, sensorId);
        getViewModelStore().put("sensorUnit", model);
        final Observer<SensorEntity> sensorObserver = sensor ->
                createSensorView((ConstraintLayout) root.getViewById(R.id.unit_layout), sensor);
        model.getSensor().observe(getViewLifecycleOwner(), sensorObserver);
        root.getViewById(R.id.save_button).setOnClickListener((view) ->
                onSaveClick((ConstraintLayout) root.getViewById(R.id.unit_layout)));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createSensorView(ConstraintLayout root, SensorEntity sens) {
        root.removeAllViews();
        addTextView(root, sens.device.name, 48f, R.color.purple_700);
        addTextView(root, sens.device.location, 32f, R.color.purple_700);
        editNameId = addEditTextView(root, sens.name, 28f, false);
        if (!sens.discrete) {
            String unit = "";
            String val = "brak wartości";
            if (sens.json_desc != null)
                try {
                    unit = sens.json_desc.getString("unit");
                } catch (JSONException e) {
                    unit = "";
                }

            if (sens.current_val != null)
                val = sens.current_val.toString();
            addTextView(root, val + unit, 24f);

        } else {
            String state = "-";
            if (sens.current_val != null)
                if (sens.current_val == 0)
                    state = "OFF";
                else
                    state = "ON";
            editValId = addTextView(root, state, 24f, R.color.teal_700);
            root.getViewById(editValId).setOnClickListener((view) -> {
                TextView textView = (TextView) view;
                if (textView.getText().toString().equals("OFF"))
                    textView.setText("ON");
                else if (textView.getText().toString().equals("ON"))
                    textView.setText("OFF");
            });
        }
        String time = "brak czasu pomiaru";
        if (sens.m_time != null)
            time = new SimpleDateFormat("HH:mm - dd.MM.yyyy", Locale.getDefault()).format(sens.m_time);
        addTextView(root, time, 24f);
        constraintTextToView(root, 10);
    }

    public void onSaveClick(ConstraintLayout view) {
        SensorEntity sensor = model.getSensor().getValue();
        EditText editText = (EditText) view.getViewById(editNameId);
        String nameText = editText.getText().toString();
        if (sensor.discrete) {
            String valText = ((TextView) view.getViewById(editValId)).getText().toString();
            if (valText.equals("OFF"))
                sensor.current_val = 0f;
            else if (valText.equals("ON"))
                sensor.current_val = 1f;
        }
        if (!nameText.equals("")) {
            sensor.name = nameText;
            if (SensorApi.updateSensor(sensor) == 200) {
                Snackbar.make(binding.getRoot(), "Zaktualizowano urządzenie", Snackbar.LENGTH_SHORT).show();
                previousFragment();
                return;
            }
        }
        Snackbar.make(binding.getRoot(), "Aktualizacja nieudana", Snackbar.LENGTH_SHORT).show();
    }
}
