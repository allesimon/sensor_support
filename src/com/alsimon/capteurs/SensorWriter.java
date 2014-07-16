package com.alsimon.capteurs;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Environment;

import com.alsimon.sensor.sensorUtils.SensorsObserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

public class SensorWriter implements SensorsObserver {
    Map<Sensor, CSVWriter> mWriter;
    File mExternalFileDir;
    byte count;

    private SensorWriter() {
        count = 10;
    }

    /**
     * IMPORTANT: call {@link #close()} in the onStop/onPause method of your Fragment/Activity  ;
     * call {@link #initWriter(android.app.Activity)} before using the {@link #writeFloat(android.hardware.Sensor, float[], float)} method
     */
    public static SensorWriter getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public void onSensorAdded(Sensor sensor) {

    }

    @Override
    public void onSensorRemoved(Sensor sensor) {

    }

    @Override
    public void onDataRetrieved(Sensor sensor, float[] values, float timeStamp) {
        writeFloat(sensor, values, timeStamp);
    }

    public boolean isWriting() {
        return mWriter != null;
    }

    public CSVWriter getWriter(Sensor sensor) {
        if (mWriter != null)
            for (Map.Entry<Sensor, CSVWriter> entry : mWriter.entrySet()) {
                if (entry.getKey().equals(sensor))
                    return entry.getValue();
            }
        createWriter(sensor);
        return getWriter(sensor);
    }

    private void createWriter(Sensor sensor) {
        if (mWriter == null) {
            mWriter = new HashMap<Sensor, CSVWriter>();
        }
        if (isExternalStorageWritable()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss",
                    Locale.ENGLISH);
            String date = sdf.format(Calendar.getInstance().getTime());
            File file = new File(mExternalFileDir,
                    sensor.getName().replace(" ", "-") + "_" + date + ".csv");
            try {
                mWriter.put(sensor, new CSVWriter(new FileWriter(file, true),
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_ESCAPE_CHARACTER));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void initWriter(Activity activity) {
        mExternalFileDir = activity.getExternalFilesDir(null);
    }

    public void close() {
        try {
            for (CSVWriter writer : mWriter.values()) {
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFloat(Sensor sensor, float[] values, float timestamp) {
        String s[] = new String[values.length + 1];
        for (int i = 0; i < values.length; i++) {
            s[i + 1] = Float.toString(values[i]);
        }
        s[0] = Float.toString(timestamp);
        getWriter(sensor).writeNext(s);
        count--;
        if (count == 0) {
            try {
                getWriter(sensor).flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            count = 10;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static class SingletonHolder {
        private final static SensorWriter instance = new SensorWriter();
    }
}