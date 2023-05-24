package com.bbudzowski.homeautoandroid.tables;


public class SensorEntity {
    public String device_id;
    public String sensor_id;
    public Integer data_type;
    public Integer current_state;
    public String units;
    public DeviceEntity device;
    public MeasurementEntity lastMeasurement;

}
