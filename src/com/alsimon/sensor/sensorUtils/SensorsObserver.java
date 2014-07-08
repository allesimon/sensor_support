package com.alsimon.sensor.sensorUtils;

import android.hardware.Sensor;

public interface SensorsObserver {

    public void onSensorAdded(Sensor sensor);

    public void onSensorRemoved(Sensor sensor);

    public void onDataRetrieved(Sensor sensor, float[] values, long timeStamp);
}
