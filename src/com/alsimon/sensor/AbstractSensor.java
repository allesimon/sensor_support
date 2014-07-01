package com.alsimon.sensor;

import com.alsimon.filter.AbstractFilter;

import android.hardware.SensorEventListener;

public abstract class AbstractSensor implements SensorEventListener {
	String name;

	public abstract void addFilter(AbstractFilter f);

	public AbstractSensor() {
		name = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
