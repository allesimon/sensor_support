package com.alsimon.sensor;

import android.hardware.SensorManager;

public enum SensorRate {
    NORMAL(SensorManager.SENSOR_DELAY_NORMAL, "Normal"),
    UI(SensorManager.SENSOR_DELAY_UI, "UI"),
    GAME(SensorManager.SENSOR_DELAY_GAME, "Game"),
    FASTEST(SensorManager.SENSOR_DELAY_FASTEST, "Fastest");
    private final int rate;
    private final String name;

    SensorRate(int rate, String name) {

        this.rate = rate;
        this.name = name;
    }

    public int getRate() {
        return rate;
    }

}
