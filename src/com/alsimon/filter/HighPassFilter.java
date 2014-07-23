package com.alsimon.filter;

import java.util.HashMap;
import java.util.Map;

public class HighPassFilter extends AbstractFilter implements FilterUI {
    float smoothedData[];
    float oldInputData[];
    float timeConstant = 40;
    long lastUpdate;

    public HighPassFilter(int sampleSize, float timeConstant) {
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
            oldInputData = data;
        }


        if (lastUpdate == 0 || timeConstant == 0) {
            System.arraycopy(data, 0, smoothedData, 0, sampleSize);
        } else {
            float dt = (float) ((System.currentTimeMillis() - lastUpdate) / 1000.0);
            float a = timeConstant / (timeConstant + dt);
            for (int i = 0; i < sampleSize; i++) {
                smoothedData[i] = a * (smoothedData[i] + data[i] - oldInputData[i]);
            }
        }
        oldInputData = data;
        lastUpdate = System.currentTimeMillis();

        return smoothedData;
    }

    @Override
    public String getName() {
        return "High pass filter";
    }

    @Override
    public Map<String, Float> getCoefficient() {
        Map<String, Float> coefficient = new HashMap<String, Float>();
        coefficient.put("Time constant", timeConstant);
        return coefficient;
    }
}