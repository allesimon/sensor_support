package com.alsimon.filter;

import java.util.HashMap;
import java.util.Map;

public class LowPassFilter extends AbstractFilter implements FilterUI {
    float smoothedData[];
    float timeConstant = 40;
    long lastUpdate;

    public LowPassFilter(int sampleSize) {
        super(sampleSize);
    }

    public LowPassFilter(int sampleSize, float timeConstant) {
        super(sampleSize);
        this.timeConstant = timeConstant;
    }


    public void setTimeConstant(float timeConstant) {
        this.timeConstant = timeConstant;
    }

    public float[] filterFloatDefensive(float[] data) {

        if (smoothedData == null) {
            smoothedData = data;
            lastUpdate = System.currentTimeMillis();
        }


        if (lastUpdate == 0 || timeConstant == 0) {
            for (int i = 0; i < sampleSize; i++) {
                smoothedData[i] = (data[i]);
            }
        } else {
            float dt = (float) ((System.currentTimeMillis() - lastUpdate) / 1000.0);
            float a = dt / (timeConstant + dt);
            for (int i = 0; i < sampleSize; i++) {
                smoothedData[i] = (1 - a) * smoothedData[i] + a * data[i];
            }
        }
        lastUpdate = System.currentTimeMillis();

        return smoothedData;
    }

    @Override
    public String getName() {
        return "Low Pass filter";
    }

    @Override
    public Map<String, Float> getCoefficient() {
        Map<String, Float> coefficient = new HashMap<String, Float>();
        coefficient.put("Time constant", timeConstant);
        return coefficient;
    }
}
