package com.eaglechopper.math;

public class NoiseConfig 
{
	public double persistance;
	public int seed; 
	public int numOctaves;
	public float scale = .3f;
	
	public NoiseConfig(int octaves, double persist, int seed) {
		this.numOctaves = octaves;
		this.persistance = persist;
		this.seed = seed;
	}
	

}
