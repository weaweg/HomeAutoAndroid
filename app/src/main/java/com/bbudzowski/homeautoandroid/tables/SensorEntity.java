package com.bbudzowski.homeautoandroid.tables;


import org.json.JSONObject;

import java.sql.Timestamp;

public class SensorEntity {
    public String device_id;
    public String sensor_id;
    public Boolean discrete;
    public String name;
    public Float current_val;
    public Timestamp m_time;
    public JSONObject json_desc;
    public DeviceEntity device;
}
