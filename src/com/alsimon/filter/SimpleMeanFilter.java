package com.alsimon.filter;

public class SimpleMeanFilter extends AbstractFilter {
    private int count;
    private float[] sum;
    private boolean dataInit = false;

    @Override
    public float[] filterFloat(float[] data) {
        if (!dataInit) {
            count = 0;
            sum = new float[data.length];
            dataInit = true;
        }
        count++;
        for (int i = 0; i < data.length; i++) {
            sum[i] += data[i];
        }
        float[] dataTemp = sum;
        for (int i = 0; i < dataTemp.length; i++) {
            dataTemp[i] /= count;
        }
        return dataTemp;
    }
}