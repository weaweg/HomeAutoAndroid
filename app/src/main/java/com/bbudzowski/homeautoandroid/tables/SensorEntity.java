package com.bbudzowski.homeautoandroid.tables;

import java.sql.Timestamp;

public class SensorEntity {
    public String device_id;
    public String sensor_id;
    public Integer data_type;
    public Integer current_state;
    public String units;
    public String name;
    public String location;
    public Timestamp m_time;
    public Float value;

}
