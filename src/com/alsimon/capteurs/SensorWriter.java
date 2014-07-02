package com.alsimon.capteurs;

import android.os.Environment;

import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;

/*
IMPORTANT: call  SensorWriter.getInstance().close();
{@link #close()}.
 */
public class SensorWriter {
    CSVWriter writer;
    byte count;

    private SensorWriter() {
        count = 10;
    }

    /**
     * IMPORTANT: call {@link #close()} in the onStop method of your Fragment/Activity  ;
     * call {@link #setWriter(au.com.bytecode.opencsv.CSVWriter)} before using the {@link #writeFloat(float[], String)} method
     */
    public static SensorWriter getInstance() {
        return SingletonHolder.instance;
    }

    public CSVWriter getWriter() {
        return writer;
    }

    public void setWriter(CSVWriter writer) {
        if (writer != null) {
            close();
        }
        this.writer = writer;
    }

    public void close() {
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFloat(float[] values, String timestamp) {
        String s[] = new String[values.length + 1];
        for (int i = 0; i < values.length; i++) {
            s[i + 1] = Float.toString(values[i]);
        }
        s[0] = timestamp;
        writer.writeNext(s);
        count--;
        if (count == 0) {
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            count = 10;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private static class SingletonHolder {
        private final static SensorWriter instance = new SensorWriter();
    }
}