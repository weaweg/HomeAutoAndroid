package com.bbudzowski.homeautoandroid.ui;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.MeasurementApi;
import com.bbudzowski.homeautoandroid.databinding.ActivityGraphBinding;
import com.bbudzowski.homeautoandroid.tables.MeasurementEntity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {
    private LineChart chart;
    private List<MeasurementEntity> measurements;
    private String unit = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        com.bbudzowski.homeautoandroid.databinding.ActivityGraphBinding binding = ActivityGraphBinding.inflate(getLayoutInflater());
        RelativeLayout root = binding.getRoot();
        Bundle extras = getIntent().getExtras();
        String device_id = extras.getString("device_id");
        String sensor_id = extras.getString("sensor_id");
        Date date = (Date) extras.getSerializable("date");
        String dateFormat = "yyyy-MM-dd";
        String start = new SimpleDateFormat(dateFormat, Locale.getDefault()).format(date);
        String end = new SimpleDateFormat(dateFormat, Locale.getDefault())
                .format(Date.from(date.toInstant().plus(1, ChronoUnit.DAYS)));
        measurements = MeasurementApi.getMeasurementsForSensor(device_id, sensor_id, start, end);
        setContentView(root);
        if (measurements.isEmpty()) {
            handleError(root);
            return;
        }
        String name = extras.getString("name");
        String location = extras.getString("location");
        try {
            JSONObject json_desc = new JSONObject(extras.getString("json_desc"));
            unit = json_desc.getString("unit");
        } catch (JSONException ignored) {
        }

        TextView title = findViewById(R.id.graph_title);
        String text = name + " - " + location;
        title.setText(text);
        createGraph(root);
    }

    private void createGraph(RelativeLayout root) {
        int purpleColor = getResources().getColor(R.color.purple_700, null);
        int tealColor = getResources().getColor(R.color.teal_700, null);
        Typeface typeFace = Typeface.create("sans-serif-black", Typeface.NORMAL);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        chart = findViewById(R.id.chart);
        String day = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).
                format(measurements.get(0).m_time);
        Description desc = chart.getDescription();
        desc.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        desc.setText(day);
        desc.setTextSize(24f);
        desc.setTextColor(purpleColor);

        chart.setMarker(new CustomMarkerView(root.getContext(), R.layout.marker_layout));
        chart.setScaleEnabled(false);

        setData(tealColor);
        Legend l = chart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(typeFace);
        xAxis.setTextSize(20f);
        chart.setExtraBottomOffset(15f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(purpleColor);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(new Date((long) value));
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTypeface(typeFace);
        leftAxis.setTextSize(16f);
        chart.setExtraLeftOffset(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setTextColor(purpleColor);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Float.valueOf(value).intValue() + unit;
            }
        });

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void setData(int lineColor) {
        ArrayList<Entry> values = new ArrayList<>();
        for (MeasurementEntity measure : measurements)
            values.add(new Entry(measure.m_time.getTime(), measure.val));

        LineDataSet set = new LineDataSet(values, "DataSet");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(lineColor);
        set.setLineWidth(2f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setHighLightColor(Color.RED);
        set.setDrawCircleHole(false);

        LineData data = new LineData(set);
        chart.setData(data);
    }

    private void handleError(RelativeLayout root) {
        root.removeAllViews();
        root.setBackground(AppCompatResources.getDrawable(root.getContext(), R.drawable.background_full));
        TextView emptyList = new TextView(root.getContext());
        emptyList.setId(View.generateViewId());
        emptyList.setText("Brak pomiarów");
        Typeface typeface = Typeface.create("sans-serif-black", Typeface.BOLD);
        emptyList.setTypeface(typeface);
        emptyList.setTextSize(32f);
        emptyList.setTextColor(getResources().getColor(R.color.purple_500, null));
        emptyList.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emptyList.setBackgroundResource(R.drawable.layout_border);
        emptyList.setPadding(50, 50, 50, 50);
        root.addView(emptyList);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        emptyList.setLayoutParams(params);
        emptyList.requestLayout();
    }
}
