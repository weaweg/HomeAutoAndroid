package com.bbudzowski.homeautoandroid.tables;

public class AutomatonEntity {
    public String name;
    public String device_id_sens;
    public String sensor_id_sens;
    public Float val_top;
    public Float val_bot;
    public String device_id_acts;
    public String sensor_id_acts;
    public Integer state_up;
    public Integer state_down;
    public SensorEntity sens;
    public SensorEntity acts;
}
