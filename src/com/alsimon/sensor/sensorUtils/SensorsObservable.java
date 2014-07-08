package com.alsimon.sensor.sensorUtils;

import android.hardware.Sensor;

public interface SensorsObservable {

    public void registerObserver(SensorsObserver observer);

    public void removeObserver(SensorsObserver observer);

    public void notifyObserversSensorAdded(Sensor sensor);

    public void notifyObserversSensorRemoved(Sensor sensor);

    public void notifyObserversDataRetrieved(Sensor sensor, float[] values, long timeStamp);

}
