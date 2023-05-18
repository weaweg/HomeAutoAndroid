package com.bbudzowski.homeautoandroid.tables;

import java.sql.Timestamp;

public class MeasurementEntity {
    public Long id;
    public String device_id;
    public String sensor_id;
    public Timestamp m_time;
    public Float val;
}
