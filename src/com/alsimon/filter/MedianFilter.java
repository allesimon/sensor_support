package com.alsimon.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedianFilter extends AbstractFilter implements FilterUI {
    List<float[]> sampleBuffer;
    int bufferSize;
    int actualPosition;
    boolean isBufferFull;

    public MedianFilter(int sampleSize, int bufferSize) {
        super(sampleSize);
        sampleBuffer = new ArrayList<float[]>();
        for (int i = 0; i < sampleSize; i++) {
            sampleBuffer.add(new float[bufferSize]);
        }
        this.bufferSize = bufferSize;
        actualPosition = 0;
        isBufferFull = false;
    }

    public float[] filterFloatDefensive(float[] data) {
        for (int i = 0; i < sampleSize; i++) {
            sampleBuffer.get(i)[actualPosition] = data[i];
        }
        float[] sortedArray;
        for (int i = 0; i < sampleSize; i++) {
            sortedArray = sampleBuffer.get(i);
            if (isBufferFull)
                System.arraycopy(sampleBuffer.get(i), 0, sortedArray, 0, bufferSize);
            else
                System.arraycopy(sampleBuffer.get(i), 0, sortedArray, 0, actualPosition);
            Arrays.sort(sortedArray);

            float median;
            if (sortedArray.length % 2 == 0)
                median = (sortedArray[sortedArray.length / 2] + sortedArray[sortedArray.length / 2 - 1]) / 2;
            else
                median = sortedArray[sortedArray.length / 2];
            data[i] = median;
        }

        actualPosition = (actualPosition + 1) % bufferSize;
        if (actualPosition == 0)
            isBufferFull = true;
        return data;
    }

    @Override
    public String getName() {
        return "Median filter";
    }
    @Override
    public Map<String, Float> getCoefficient() {
        Map<String, Float> coefficient = new HashMap<String, Float>();
        coefficient.put("Buffer size", (float) bufferSize);
        return coefficient;
    }
}
