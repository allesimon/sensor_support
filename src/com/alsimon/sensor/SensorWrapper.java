package com.alsimon.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.alsimon.filter.AbstractFilter;
import com.alsimon.sensor.sensorUtils.SensorAndFilter;
import com.alsimon.sensor.sensorUtils.SensorsObservable;
import com.alsimon.sensor.sensorUtils.SensorsObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SensorWrapper implements SensorsObservable {
    private SensorManager mSensorManager;
    private int mSensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
    private List<SensorAndFilter> mSensor;
    private List<SensorsObserver> mSensorsObservers;


    private SensorWrapper() {
    }

    public static SensorWrapper getInstance() {
        return SingletonHolder.instance;
    }

    public void addSensor(Sensor sensor, AbstractFilter... filters) {
        SensorAndFilter sensorAndFilter = new SensorAndFilter(sensor, filters);
        sensorAndFilter.setSensorEventListener(new mSensorEventListener());
        mSensorManager.registerListener(sensorAndFilter.getSensorEventListener(), sensor, SensorRate.NORMAL.getRate());
        mSensor.add(sensorAndFilter);
        notifyObserversSensorAdded(sensor);
    }

    public void removeSensor(Sensor sensor) {
        int position = getSensorPosition(sensor);
        mSensorManager.unregisterListener(mSensor.get(position).getSensorEventListener());
        mSensor.remove(position);
    }

    public List<Sensor> getSensorList() {
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        List<Sensor> sensorsTemp = new ArrayList<Sensor>();
        sensorsTemp.addAll(sensors);
        Collections.sort(sensorsTemp, new Comparator<Sensor>() {
            @Override
            public int compare(Sensor sensor, Sensor sensor2) {
                return sensor.getType() - sensor2.getType();
            }
        });
        return sensorsTemp;
    }

    public void swapSensorDelay() {
        if (mSensorDelay == SensorManager.SENSOR_DELAY_NORMAL) {
            mSensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
        } else {
            mSensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
        }
    }

    public void initialize(SensorManager sensorManager) {
        mSensorManager = sensorManager;
        mSensor = new ArrayList<SensorAndFilter>();
        mSensorsObservers = new ArrayList<SensorsObserver>();
    }

    public void unregisterListener() {
        for (SensorAndFilter sensorAndFilter : mSensor) {
            mSensorManager.unregisterListener(sensorAndFilter.getSensorEventListener());
        }
    }

    @Override
    public void registerObserver(SensorsObserver observer) {
        mSensorsObservers.add(observer);
    }

    @Override
    public void removeObserver(SensorsObserver observer) {
        mSensorsObservers.remove(observer);
    }

    @Override
    public void notifyObserversSensorAdded(Sensor sensor) {
        for (SensorsObserver so : mSensorsObservers) {
            so.onSensorAdded(sensor);
        }
    }

    @Override
    public void notifyObserversSensorRemoved(Sensor sensor) {
        for (SensorsObserver so : mSensorsObservers) {
            so.onSensorRemoved(sensor);
        }
    }

    @Override
    public void notifyObserversDataRetrieved(Sensor sensor, float[] values, long timeStamp) {
        for (SensorsObserver so : mSensorsObservers) {
            so.onDataRetrieved(sensor, values, timeStamp);
        }
    }

    /**
     * @return the position of the Sensor in the List of SensorAndFilter
     */
    public int getSensorPosition(Sensor sensor) {
        int i = 0;
        for (SensorAndFilter sensorAndFilter : mSensor) {
            if (sensorAndFilter.getSensor().equals(sensor)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private static class SingletonHolder {

        private final static SensorWrapper instance = new SensorWrapper();
    }

    private class mSensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            SensorAndFilter saf = mSensor.get(getSensorPosition(sensorEvent.sensor));
            for (AbstractFilter filter : saf.getFilters()) {
                filter.filterFloat(values);
            }
            notifyObserversDataRetrieved(sensorEvent.sensor, values, sensorEvent.timestamp);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            //nothing yet
        }
    }
}
