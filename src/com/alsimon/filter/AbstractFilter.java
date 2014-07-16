package com.alsimon.filter;

public abstract class AbstractFilter {
    /**
     * Filter the data.
     *
     * @param data contains input the data.
     * @return the filtered output data.
     */
    public abstract float[] filterFloat(float[] data);

    public float[] multiplyVector(float[] data, float coefficient) {
        float tempData[] = data;
        for (int i = 0; i < tempData.length; i++) {
            tempData[i] *= coefficient;
        }
        return tempData;
    }

    public float[] soustractVectors(float[] dataSet1, float[] dataSet2) {
        for (int i = 0; i < dataSet1.length; i++) {
            dataSet1[i] -= dataSet2[i];
        }
        return dataSet1;
    }
}
