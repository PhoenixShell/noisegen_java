package com.eaglechopper.math;

public interface NoiseAlgorithm 
{
	public double noise(double x, double y);
	public double noise(double x, double y, double z);
	public void setSeed(long seed);
}
