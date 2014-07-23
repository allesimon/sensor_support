package com.alsimon.filter;

public abstract class AbstractFilter {
    protected final int sampleSize;

    protected AbstractFilter(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    protected AbstractFilter() {
        this.sampleSize = 3;
    }

    /**
     * Filter the data.
     *
     * @param data contains input data, unless another class overwrite it, @paramData
     * @return the filtered output data.
     */
    public float[] filterFloat(float[] data) {
        return filterFloatDefensive(copyVector(data));
    }

    public abstract float[] filterFloatDefensive(float[] data);

    public float[] multiplyVector(float[] data, float coefficient) {
        for (int i = 0; i < data.length; i++) {
            data[i] *= coefficient;
        }
        return data;
    }

    public float[] subtractVectors(float[] dataSet1, float[] dataSet2) {
        for (int i = 0; i < dataSet1.length; i++) {
            dataSet1[i] -= dataSet2[i];
        }
        return dataSet1;
    }

    public float[] copyVector(float[] data) {
        float tempData[] = new float[data.length];
        System.arraycopy(data, 0, tempData, 0, sampleSize);
        return tempData;
    }
}
