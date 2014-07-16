package com.alsimon.filter;

public class LowPassFilter extends AbstractFilter {
    float smoothedData[];
    float smoothingCoefficient = 40;
    long lastUpdate;


    public void setSmoothingCoefficient(float smoothingCoefficient) {
        this.smoothingCoefficient = smoothingCoefficient;
    }

    @Override
    public float[] filterFloat(float[] data) {
        if (smoothedData == null) {
            smoothedData = data;
            lastUpdate = System.currentTimeMillis();
        }

        long elapsedTime = System.currentTimeMillis() - lastUpdate;

        for (int i = 0; i < data.length; i++) {
            smoothedData[i] += elapsedTime * (data[i] - smoothedData[i]) / smoothingCoefficient;
        }


        lastUpdate = System.currentTimeMillis();
        return smoothedData;
    }
}
