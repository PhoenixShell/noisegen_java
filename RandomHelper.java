package com.eaglechopper.math;

import java.util.Random;

public class RandomHelper
{
	//double and float randoms
	public static float newFloat(float min, float max, Random rand){
		float random = min + rand.nextFloat() * (max - min);
		return random;
	}
	public static double newDouble(double min, double max, Random rand){
		double random = min + rand.nextDouble() * (max - min);
		return random;
	}
	
	//int and long randoms
	public static long newLong(long min, long max, Random rand){
		return rand.nextInt((int) (max - min)) + min;
	}
	
	public static int newInt(int min, int max, Random rand) {
		return rand.nextInt(max - min) + min;
	}
	
	//fill arrays
	public static void fillBool(boolean[] array, Random rand) {
		for(int i=0; i < array.length; i++) {
			array[i] = rand.nextBoolean();
		}
	}
	public static void fillInt(int[] array, int min, int max, Random rand) {
		for(int i=0; i < array.length; i++) {
			array[i] = newInt(min, max, rand);
		}
	}
	public static void fillFloat(float[] array, float min, float max, Random rand) {
		for(int i=0; i < array.length; i++) {
			array[i] = newFloat(min, max, rand);
		}
	}
	public static void fillDouble(double[] array, double min, double max, Random rand) {
		for(int i=0; i < array.length; i++) {
			array[i] = newDouble(min, max, rand);
		}
	}
	public static void fillLong(long[] array, long min, long max, Random rand) {
		for(int i=0; i < array.length; i++) {
			array[i] = newLong(min, max, rand);
		}
	}
}
