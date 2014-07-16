package com.alsimon.filter;

public class SimpleFilter extends AbstractFilter {

    float coefficient = 0.4f;
    float oldData[];

    @Override
    public float[] filterFloat(float[] data) {
        if (oldData == null)
            oldData = data;
        float result[] =
                soustractVectors(multiplyVector(data, coefficient), multiplyVector(oldData, 1 - coefficient));
        oldData = data;
        return result;
    }
}
