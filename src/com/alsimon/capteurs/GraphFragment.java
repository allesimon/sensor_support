package com.alsimon.capteurs;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVWriter;

public class GraphFragment extends Fragment implements SensorEventListener {
    public static final String ARG_POSITION = "position";
    private SensorManager mSensorManager;
    private List<Sensor> mSensor;
    private GraphView graphView;
    private List<GraphViewSeries> graphSeries;
    private String[] labels = new String[]{"x", "y", "z"};
    private long timestamp;
    private int[] colors = new int[]{Color.rgb(255, 68, 68),
            Color.rgb(255, 187, 51), Color.rgb(51, 181, 29), Color.MAGENTA,
            Color.WHITE, Color.GRAY};
    private boolean pause = true;
    private List<SensorEventListener> mSensorEvListeners;
    private int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
    private boolean sensorAdded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            SensorManager mSensorManager = (SensorManager) getActivity()
                    .getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> sensors = mSensorManager
                    .getSensorList(Sensor.TYPE_ALL);
            gestionSensor(sensors.get(getArguments().getInt(ARG_POSITION)));
        }
        return inflater.inflate(R.layout.graph_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        initField();
    }

    private void initField() {
        graphSeries = new ArrayList<GraphViewSeries>();
        graphView = new LineGraphView(this.getActivity(), "");
        graphView.setShowLegend(true);
        graphView.setLegendAlign(LegendAlign.BOTTOM);
        graphView.setScrollable(true);
        graphView.setScalable(true);
        graphView.setViewPort(0, 100);
        graphView.setDisableTouch(false);
        graphView.setBackgroundColor(Color.BLACK);
        LinearLayout layout = (LinearLayout) getView()
                .findViewById(R.id.layout);
        layout.addView(graphView);
    }

    public void drawGraph(SensorEvent event) {
        float[] values = event.values;
        timestamp = event.timestamp;

        GraphViewData[] data = new GraphViewData[]{new GraphViewData(0, 0)};
        GraphViewSeries series;
        graphView.setTitle(getName(event.sensor));
        int offshift = getOffshift(event.sensor);

        for (int i = 0; i < values.length; i++) {
            series = new GraphViewSeries(getAxisLabel(i + offshift),
                    getAxisStyle(i + offshift), data);
            graphSeries.add(series);
            graphView.addSeries(series);
        }
    }

    public int getOffshift(Sensor s) {
        for (int i = 0; i < mSensor.size(); i++) {
            if (mSensor.get(i).getName().equals(s.getName()))
                return i * 3;
        }
        return 0;
    }

    private String getName(Sensor s) {
        return s.getName().replace(" Sensor", "");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        int offshift = getOffshift(event.sensor);

        if (sensorAdded) {
            drawGraph(event);
            sensorAdded = false;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss",
                        Locale.ENGLISH);
                String date = sdf.format(Calendar.getInstance().getTime());
                File file = new File(getActivity().getExternalFilesDir(null),
                        "Sensor_" + getName(event.sensor) + "_" + date + ".csv");
                SensorWriter.getInstance().setWriter(
                        new CSVWriter(new FileWriter(file, true),
                                CSVWriter.DEFAULT_SEPARATOR,
                                CSVWriter.NO_ESCAPE_CHARACTER)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean scrollToEnd;
        for (int i = 0; i < values.length; i++) {
            scrollToEnd = (i == (values.length - 1)) & pause;
            graphSeries.get(i + offshift).appendData(
                    new GraphViewData((event.timestamp - timestamp) / 1000000,
                            values[i]), scrollToEnd, 1000
            );
        }
        SensorWriter.getInstance().writeFloat(values,
                Long.toString(event.timestamp - timestamp));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void gestionSensor(Sensor s) {
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) getActivity().getSystemService(
                    Context.SENSOR_SERVICE);
        }
        if (mSensor == null) {
            mSensor = new ArrayList<Sensor>();
            mSensorEvListeners = new ArrayList<SensorEventListener>();
        }
        if (s != null) {
            mSensor.add(s);
            SensorEventListener ms = this;
            mSensorManager.registerListener(ms, s, sensorDelay);
            mSensorEvListeners.add(ms);
            sensorAdded = true;
        }
    }

    public void gestionSensor(Sensor s, boolean refreshSensors) {
        if (s == null) {
            if (mSensorEvListeners.size() != 0) {
                for (int i = 0; i < mSensor.size(); i++) {
                    mSensorManager
                            .unregisterListener(mSensorEvListeners.get(i));
                }
            }
            mSensorEvListeners = new ArrayList<SensorEventListener>();
        }
        if (refreshSensors) {
            SensorEventListener ms = this;
            for (Sensor aMSensor : mSensor) {
                mSensorManager
                        .registerListener(ms, aMSensor, sensorDelay);
                mSensorEvListeners.add(ms);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SensorWriter.getInstance().close();
    }

    public String getAxisLabel(int i) {
        return this.labels[i % labels.length];
    }

    public GraphViewSeriesStyle getAxisStyle(int i) {
        return new GraphViewSeriesStyle(colors[i % colors.length], 3);
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void changePause() {
        pause = !pause;
    }

    public void swapSensorDelay() {
        if (sensorDelay == SensorManager.SENSOR_DELAY_NORMAL) {
            sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
        } else {
            sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
        }
        gestionSensor(null);
    }
}