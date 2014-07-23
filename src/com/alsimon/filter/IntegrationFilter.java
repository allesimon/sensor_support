package com.alsimon.filter;

import java.util.HashMap;
import java.util.Map;

public class IntegrationFilter extends AbstractFilter implements FilterUI {
    private final float NANO;
    private float[] currentValue;
    private long lastUpdate = 0;

    public IntegrationFilter(int sampleSize) {
        super(sampleSize);
        currentValue = new float[sampleSize];
        NANO = (float) Math.pow(10, -9);
    }

    @Override
    public float[] filterFloatDefensive(float[] data) {
        long now = System.nanoTime();
        if (lastUpdate == 0)
            lastUpdate = now;
        double dt = (now - lastUpdate) * NANO;
        lastUpdate = now;
        for (int i = 0; i < data.length; i++) {
            currentValue[i] += data[i] * dt;
        }
        return currentValue;
    }


    @Override
    public String getName() {
        return "Integration filter";
    }

    @Override
    public Map<String, Float> getCoefficient() {
        return new HashMap<String, Float>();
    }
}
