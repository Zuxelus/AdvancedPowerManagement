package com.zuxelus.apm.utils;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class MovingAverage {
	private double packets[];
	private int delays[];
	private int position;
	private int delay;
	private int window;
	private double average;
	private double delta;

	public MovingAverage(int size) {
		packets = new double[size];
		delays = new int[size];
		position = 0;
		delay = 1;
		window = size;
		average = 0F;
		delta = 0F;
		for (int i = 0; i < size; i++) {
			packets[i] = 0;
			delays[i] = 600;
		}
	}

	public void tick(double value) {
		if (value > 0 || delay >= 600) {
			position++;
			if (position >= packets.length)
				position = 0;
			packets[position] = value;
			delays[position] = delay;
			delay = 1;
			window = sumDelays();
			double newAvg = sumPackets() / window;
			delta = newAvg - average;
			average = newAvg;
		} else {
			delay++;
			if (delays.length * delay > window) {
				window++;
				average = sumPackets() / window;
			}
		}
	}

	protected int sumDelays() {
		return IntStream.of(delays).sum();
	}

	protected double sumPackets() {
		return DoubleStream.of(packets).sum();
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double value) {
		average = value;
	}

	public int getWindow() {
		return window;
	}
}
