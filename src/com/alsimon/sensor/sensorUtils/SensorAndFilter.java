package com.alsimon.sensor.sensorUtils;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;

import com.alsimon.filter.AbstractFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorAndFilter {
    private Sensor sensor;
    private List<AbstractFilter> filters;
    private SensorEventListener sensorEventListener;

    public SensorAndFilter(Sensor sensor, AbstractFilter... filters) {
        this.sensor = sensor;
        this.filters = new ArrayList<AbstractFilter>();
        this.filters.addAll(Arrays.asList(filters));
    }

    public SensorEventListener getSensorEventListener() {
        return sensorEventListener;
    }

    public void setSensorEventListener(SensorEventListener sensorEventListener) {
        this.sensorEventListener = sensorEventListener;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public List<AbstractFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<AbstractFilter> filters) {
        this.filters = filters;
    }
}
