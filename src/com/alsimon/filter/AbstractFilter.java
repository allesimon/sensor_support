package com.alsimon.filter;

public abstract class AbstractFilter {
    /**
     * Filter the data.
     *
     * @param data contains input the data.
     * @return the filtered output data.
     */
    public abstract float[] filterFloat(float[] data);
}
