package com.bbudzowski.homeautoandroid.ui.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentGenGraphBinding;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.GraphActivity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GenGraphsFragment extends BasicFragment {
    private MainActivity mainActivity;
    private FragmentGenGraphBinding binding;
    private SensorEntity pickedSensor;
    private Handler handler;
    private Date date;
    private Calendar calendar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        binding = FragmentGenGraphBinding.inflate(inflater, container, false);
        createGenGraphView((ConstraintLayout) binding.getRoot().getViewById(R.id.graph_layout));
        ConstraintLayout root = binding.getRoot();
        ConstraintLayout view = (ConstraintLayout) root.getViewById(R.id.graph_layout);
        root.getViewById(R.id.generate_button).setOnClickListener(v -> {
            if(pickedSensor == null) {
                Snackbar.make(binding.getRoot(), "Wybierz czujnik!", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if(date == null) {
                Snackbar.make(binding.getRoot(), "Wybierz datę!", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(mainActivity, GraphActivity.class);
            intent.putExtra("date", date);
            intent.putExtra("name", pickedSensor.name);
            intent.putExtra("location", pickedSensor.device.location);
            intent.putExtra("json_desc", pickedSensor.json_desc.toString());
            intent.putExtra("device_id", pickedSensor.device_id);
            intent.putExtra("sensor_id", pickedSensor.sensor_id);
            startActivity(intent);
        });
        view.getViewById(R.id.pickdate_button).setOnClickListener(v -> {
            int yearNow = calendar.get(Calendar.YEAR);
            int monthNow = calendar.get(Calendar.MONTH);
            int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year, month, day) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                date = calendar.getTime();
                ((MaterialButton) v).setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date));
            }, yearNow, monthNow, dayNow);
            datePickerDialog.show();

        });
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                TextView sensorTextView = (TextView) view.getViewById(msg.what);
                String text = pickedSensor.name + " - " + pickedSensor.device.location;
                sensorTextView.setText(text);
            }
        };
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void createGenGraphView(ConstraintLayout view) {
        int text = addTextView(view, "Wybierz czujnik", 32f);
        int sensTextId = addTextView(view, "Kliknij tutaj",24f, R.color.teal_700);
        addSensorListOnClick(sensTextId, mainActivity.getSensors());
        ConstraintSet set = new ConstraintSet();
        set.clone(view);
        set.connect(text, ConstraintSet.TOP, view.getId(), ConstraintSet.TOP, 20);
        set.centerHorizontally(text, ConstraintSet.PARENT_ID);
        set.connect(sensTextId, ConstraintSet.TOP, text, ConstraintSet.BOTTOM, 50);
        set.centerHorizontally(sensTextId, ConstraintSet.PARENT_ID);
        set.connect(R.id.pickdate_button, ConstraintSet.TOP, sensTextId, ConstraintSet.BOTTOM, 50);
        set.applyTo(view);
    }

    private void addSensorListOnClick(int listTextId, List<SensorEntity> sensors) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Wybierz czujnik");
        builder.setCancelable(true);
        List<String> names = new ArrayList<>();
        for(SensorEntity sens : sensors)
                names.add(sens.name + " - " + sens.device.location);
        builder.setItems(names.toArray(new CharSequence[0]), (dialog, which) -> {
            pickedSensor = sensors.get(which);
            handler.sendEmptyMessage(listTextId);
        });
        AlertDialog dialog = builder.create();
        ConstraintLayout view = (ConstraintLayout) binding.getRoot().getViewById(R.id.graph_layout);
        view.getViewById(listTextId).setOnClickListener(v -> dialog.show());
    }
}