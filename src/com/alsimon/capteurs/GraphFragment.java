package com.alsimon.capteurs;

import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alsimon.sensor.MySensorManager;
import com.alsimon.sensor.sensorUtils.SensorsObserver;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GraphFragment extends Fragment implements SensorsObserver {
    private GraphView mGraphView;
    private List<GraphViewSeries> mGraphSeries;
    private String[] labels = new String[]{"x", "y", "z"};
    private int[] colors = new int[]{Color.RED,
            Color.YELLOW, Color.GREEN, Color.MAGENTA,
            Color.WHITE, Color.GRAY};
    private boolean pause = true;
    private int maxNumberOfValues = 1000;
    private DecimalFormat mDecimalFormatXaxis;
    private DecimalFormat mDecimalFormatYaxis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.graph_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        initField();
    }

    private void initField() {
        mDecimalFormatXaxis = new DecimalFormat("#0.##");
        mDecimalFormatYaxis = new DecimalFormat("#0.###");
        mGraphSeries = new ArrayList<GraphViewSeries>();
        mGraphView = new LineGraphView(this.getActivity(), "");
        mGraphView.setShowLegend(true);
        mGraphView.setLegendAlign(LegendAlign.BOTTOM);
        mGraphView.setScrollable(true);
        mGraphView.setScalable(true);
        mGraphView.setViewPort(0, 100);
        mGraphView.setDisableTouch(false);
        mGraphView.setBackgroundColor(Color.BLACK);
        mGraphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return mDecimalFormatXaxis.format(value);
                } else {
                    return mDecimalFormatYaxis.format(value);
                }
            }
        });
        LinearLayout layout = (LinearLayout) getView()
                .findViewById(R.id.layout);
        layout.addView(mGraphView);
        MySensorManager.getInstance().registerObserver(this);
    }

    public void drawGraph(Sensor sensor) {
        GraphViewData[] data = new GraphViewData[]{new GraphViewData(0, 0)};
        GraphViewSeries series;
        mGraphView.setTitle(getSensorName(sensor));
        int offset = getOffset(sensor);
        //FIXME find a way to get Axis number and change "3"
        for (int i = 0; i < 3; i++) {
            series = new GraphViewSeries(getAxisLabel(i + offset),
                    getAxisStyle(i + offset), data);
            mGraphSeries.add(series);
            mGraphView.addSeries(series);
        }
    }

    public int getOffset(Sensor s) {
        return MySensorManager.getInstance().getSensorPosition(s) * 3;
    }

    private String getSensorName(Sensor s) {
        return s.getName().replace(" Sensor", "").replace(" ", "_");
    }


    @Override
    public void onDataRetrieved(Sensor sensor, float[] values, float timeStamp) {
        boolean scrollToEnd;
        for (int i = 0; i < values.length; i++) {
            scrollToEnd = (i == (values.length - 1)) & pause;
            mGraphSeries.get(i + getOffset(sensor)).appendData(
                    new GraphViewData(timeStamp,
                            values[i]), scrollToEnd, maxNumberOfValues
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
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

    @Override
    public void onSensorAdded(Sensor s) {
        drawGraph(s);
    }

    @Override
    public void onSensorRemoved(Sensor s) {
        int offset = getOffset(s);
        for (int i = offset; i < offset + 3; i++) {
            mGraphSeries.remove(i);
            mGraphView.removeSeries(i);
            mGraphView.setShowLegend(true);
        }
    }
}