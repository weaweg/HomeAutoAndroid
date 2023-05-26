package com.bbudzowski.homeautoandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bbudzowski.homeautoandroid.api.AutomatonApi;
import com.bbudzowski.homeautoandroid.api.DeviceApi;
import com.bbudzowski.homeautoandroid.api.SensorApi;
import com.bbudzowski.homeautoandroid.databinding.ActivityMainBinding;
import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private Timer updateTimer;
    private List<DeviceEntity> devices;
    private List<SensorEntity> sensors;
    private List<AutomatonEntity> automatons;
    private Timestamp devicesLastUpdate;
    private Timestamp sensorsLastUpdate;
    private Timestamp automatonsLastUpdate;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.bbudzowski.homeautoandroid.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.hide();
        binding.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_devices, R.id.nav_sensors, R.id.nav_load)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                navController.popBackStack();
                navController.navigate(R.id.nav_devices);
            }
        };
        Executors.newSingleThreadExecutor().execute(this::getAllData);
    }

    private void getAllData () {
        devices = DeviceApi.getDevices();
        sensors = SensorApi.getSensors();
        automatons = AutomatonApi.getAutomatons();
        devicesLastUpdate = DeviceApi.getUpdateTime();
        if(devicesLastUpdate == null) devicesLastUpdate = new Timestamp(0);
        sensorsLastUpdate = SensorApi.getUpdateTime();
        if(sensorsLastUpdate == null) sensorsLastUpdate = new Timestamp(0);
        automatonsLastUpdate = AutomatonApi.getUpdateTime();
        if(automatonsLastUpdate == null) automatonsLastUpdate = new Timestamp(0);
        handler.sendEmptyMessage(0);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Timestamp updateTimeDevice = DeviceApi.getUpdateTime();
                Timestamp updateTimeSensor = SensorApi.getUpdateTime();
                Timestamp updateTimeAutomaton = AutomatonApi.getUpdateTime();

                if (updateTimeDevice.compareTo(devicesLastUpdate) > 0) {
                    devices = DeviceApi.getDevices();
                    devicesLastUpdate = updateTimeDevice;
                }
                if (updateTimeSensor.compareTo(sensorsLastUpdate) > 0) {
                    sensors = SensorApi.getSensors();
                    sensorsLastUpdate = updateTimeSensor;
                }
                if (updateTimeAutomaton.compareTo(automatonsLastUpdate) > 0) {
                    automatons = AutomatonApi.getAutomatons();
                    automatonsLastUpdate = updateTimeAutomaton;
                }

            }
        };
        updateTimer = new Timer();
        updateTimer.schedule(timerTask, 0, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        updateTimer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public List<DeviceEntity> getDevices() {
        return devices;
    }

    public DeviceEntity getDevice(String device_id) {
        for (DeviceEntity device : devices) {
            if (device.device_id.equals(device_id))
                return device;
        }
        return null;
    }

    public List<SensorEntity> getSensors() {
        return sensors;
    }

    public SensorEntity getSensor(String device_id, String sensor_id) {
        for (SensorEntity sensor : sensors) {
            if (sensor.device_id.equals(device_id) && sensor.sensor_id.equals(sensor_id))
                return sensor;
        }
        return null;
    }

    public List<AutomatonEntity> getAutomatons() {
        return automatons;
    }

    public AutomatonEntity getAutomaton(String name) {
        for (AutomatonEntity automaton : automatons) {
            if (automaton.name.equals(name))
                return automaton;
        }
        return null;
    }

    public Timestamp getDevicesLastUpdate() {
        return devicesLastUpdate;
    }

    public Timestamp getSensorsLastUpdate() {
        return sensorsLastUpdate;
    }

    public Timestamp getAutomatonsLastUpdate() {
        return automatonsLastUpdate;
    }

    public Timestamp getLastUpdateTime() {
        Timestamp updateTime = devicesLastUpdate;
        if(sensorsLastUpdate.compareTo(updateTime) > 0)
            updateTime = sensorsLastUpdate;
        if(automatonsLastUpdate.compareTo(updateTime) > 0)
            updateTime = automatonsLastUpdate;
        return updateTime;
    }

}