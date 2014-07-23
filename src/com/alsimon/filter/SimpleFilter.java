package com.alsimon.filter;

import java.util.HashMap;
import java.util.Map;

public class SimpleFilter extends AbstractFilter implements FilterUI{

    float weightCoefficient = 0.4f;
    float oldData[];

    public SimpleFilter(int sampleSize) {
        super(sampleSize);
    }

    public float[] filterFloatDefensive(float[] data) {

        if (oldData == null)
            oldData = data;
        float result[] =
                subtractVectors(multiplyVector(data, weightCoefficient), multiplyVector(oldData, 1 - weightCoefficient));
        oldData = data;
        return result;
    }

    @Override
    public String getName() {
        return "Simple filter";
    }

    @Override
    public Map<String, Float> getCoefficient() {
        Map<String, Float> coefficient = new HashMap<String, Float>();
        coefficient.put("Weight coefficient", weightCoefficient);
        return coefficient;
    }
}
