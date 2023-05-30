package com.bbudzowski.homeautoandroid.ui;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.MeasurementApi;
import com.bbudzowski.homeautoandroid.databinding.ActivityGraphBinding;
import com.bbudzowski.homeautoandroid.tables.MeasurementEntity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GraphActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private ActivityGraphBinding binding;
    private Date date;
    private String device_id;
    private String sensor_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        binding = ActivityGraphBinding.inflate(getLayoutInflater());
        RelativeLayout root = binding.getRoot();
        Bundle extras = getIntent().getExtras();
        device_id = extras.getString("device_id");
        sensor_id = extras.getString("sensor_id");
        date = (Date) extras.getSerializable("date");
        Timestamp start = new Timestamp(date.getTime());
        Timestamp end = new Timestamp(
                Timestamp.from(date.toInstant().plus(1, ChronoUnit.DAYS)).getTime());
        List<MeasurementEntity> measurements = MeasurementApi.
                getMeasurementsForSensor(device_id, sensor_id, start, end);
        setContentView(root);
        if(measurements.isEmpty()) {
            root.removeAllViews();
            handleError(binding.getRoot());
            return;
        }
        createGraph();
    }

    private void createGraph() {
        LineChart chart = new LineChart(binding.getRoot().getContext());
        SeekBar seekBarX = new SeekBar(binding.getRoot().getContext());
        seekBarX.setOnSeekBarChangeListener(this);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // add data
        seekBarX.setProgress(100);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        //xAxis.setTypeface(tfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
            @Override
            public String getFormattedValue(float value) {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(170f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void handleError(RelativeLayout root) {
        TextView emptyList = new TextView(root.getContext());
        emptyList.setId(View.generateViewId());
        emptyList.setText("Brak pomiarów");
        Typeface typeface = Typeface.create("sans-serif-black", Typeface.BOLD);
        emptyList.setTypeface(typeface);
        emptyList.setTextSize(32f);
        emptyList.setTextColor(getResources().getColor(R.color.purple_500, null));
        emptyList.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        root.addView(emptyList);
        emptyList.setLayoutParams(
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
