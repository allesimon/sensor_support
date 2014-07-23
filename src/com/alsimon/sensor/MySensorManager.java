package com.alsimon.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.alsimon.filter.AbstractFilter;
import com.alsimon.sensor.sensorUtils.SensorAndFilter;
import com.alsimon.sensor.sensorUtils.SensorsObservable;
import com.alsimon.sensor.sensorUtils.SensorsObserver;
import com.alsimon.utils.Logg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MySensorManager implements SensorsObservable {
    private android.hardware.SensorManager mSensorManager;
    private int mSensorDelay = android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
    private List<SensorAndFilter> mSensor;
    private List<SensorsObserver> mSensorsObservers;
    /**
     * (android.hardware.SensorEvent).timestamp is not consistent across devices: some use UNIX time, other SystemClock.upTimeMillis, other seemingly random values
     * {@see <a href="https://code.google.com/p/android/issues/detail?id=7981"> AOSP issue tracker: timestamp incorrectly populated </a>  }.
     */
    private long mTimestamp;

    private MySensorManager() {
    }

    public static MySensorManager getInstance() {
        return SingletonHolder.instance;
    }

    public void addSensor(Sensor sensor, AbstractFilter... filters) {
        SensorAndFilter sensorAndFilter = new SensorAndFilter(sensor, filters);
        sensorAndFilter.setSensorEventListener(new mSensorEventListener());
        mSensorManager.registerListener(sensorAndFilter.getSensorEventListener(), sensor, mSensorDelay);
        mSensor.add(sensorAndFilter);
        notifyObserversSensorAdded(sensor);
    }

    public void removeSensor(Sensor sensor) {
        int position = getSensorPosition(sensor);
        mSensorManager.unregisterListener(mSensor.get(position).getSensorEventListener());
        mSensor.remove(position);
    }

    public void removeFilter(int sensorNumber, int filterNumber) {
        try {
            mSensor.get(sensorNumber).getFilters().remove(filterNumber);
        } catch (Exception e) {
            Logg.e(sensorNumber, filterNumber);
        }
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

    public List<Sensor> getSensorInUse() {
        List<Sensor> sensorsTemp = new ArrayList<Sensor>();
        for (SensorAndFilter saf : mSensor) {
            sensorsTemp.add(saf.getSensor());
        }
        return sensorsTemp;
    }

    public void swapSensorDelay() {
        List<SensorRate> sensorRates = new ArrayList<SensorRate>(Arrays.asList(SensorRate.values()));
        Logg.e(mSensorDelay);
        for (int i = 0; i < sensorRates.size(); i++) {
            Logg.e(sensorRates.get(i).getRate());
            if (sensorRates.get(i).getRate() == mSensorDelay) {
                mSensorDelay = sensorRates.get((i + 1) % sensorRates.size()).getRate();
                Logg.e(mSensorDelay);
                break;
            }
        }
        Logg.e(mSensorDelay);
        unregisterListener();
        for (SensorAndFilter sensorAndFilter : mSensor) {
            sensorAndFilter.setSensorEventListener(new mSensorEventListener());
            mSensorManager.registerListener(sensorAndFilter.getSensorEventListener(), sensorAndFilter.getSensor(), mSensorDelay);
        }
    }

    public void initialize(android.hardware.SensorManager sensorManager) {
        mSensorManager = sensorManager;
        mSensor = new ArrayList<SensorAndFilter>();
        mSensorsObservers = new ArrayList<SensorsObserver>();
    }

    public void unregisterListener() {
        Logg.printFullTrace();
        for (SensorAndFilter sensorAndFilter : mSensor) {
            mSensorManager.unregisterListener(sensorAndFilter.getSensorEventListener());
        }
    }

    public void registerListener() {
        Logg.printFullTrace();
        for (SensorAndFilter sensorAndFilter : mSensor) {
            mSensorManager.registerListener(sensorAndFilter.getSensorEventListener(), sensorAndFilter.getSensor(), mSensorDelay);
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
        Logg.e();
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

    /**
     * @param timeStamp the timeStamp format returned is "#0.###"
     */
    @Override
    public void notifyObserversDataRetrieved(Sensor sensor, float[] values, long timeStamp) {
        if (mTimestamp == 0)
            mTimestamp = timeStamp;
        float timeTemp = timeStamp - mTimestamp;
        timeTemp = ((int) (timeTemp / 100000));
        timeTemp /= 10000;
        //FIXME
        for (SensorsObserver so : mSensorsObservers) {
            so.onDataRetrieved(sensor, values, timeTemp);
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

    public Sensor getSensorAtPosition(int position) {
        return mSensor.get(position).getSensor();
    }

    public List<AbstractFilter> getFiltersForSensor(int position) {
        if (position > mSensor.size())
            return null;
        return mSensor.get(position).getFilters();
    }

    private static class SingletonHolder {

        private final static MySensorManager instance = new MySensorManager();
    }

    private class mSensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            SensorAndFilter saf;
            try {
                saf = mSensor.get(getSensorPosition(sensorEvent.sensor));
                for (AbstractFilter filter : saf.getFilters()) {
                    values = filter.filterFloat(values);
                }
                notifyObserversDataRetrieved(sensorEvent.sensor, values, sensorEvent.timestamp);
            } catch (ArrayIndexOutOfBoundsException e) {
                Logg.e("Sensor removedd");
                e.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            //nothing yet
        }
    }
}
