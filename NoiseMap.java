package com.eaglechopper.math;

import java.util.Random;

public class NoiseMap
{
    public static final int MODE_AMPINCREASE = 0;
    public static final int MODE_AMPDECREASE = 1;
    private final NoiseConfig config;
    private NoiseAlgorithm[] octaves;
    private double[] frequencys;
    private double[] amplitudes;
    private float originX;
    private float originY;
	
    private float maxAmp;
	
    private int mode;
    
    public NoiseMap(int octaves, double persist, int seed, int mode){
    	this.config = new NoiseConfig(octaves, persist, seed);
    	this.mode = mode;
    	update(this.mode);
    }
    public NoiseMap(NoiseConfig config, int mode) {
    	this.config = config;
    	update(this.mode);
    }
    public void update(int mode) {
    	octaves=new SimplexNoise[config.numOctaves];
        frequencys=new double[config.numOctaves];
        amplitudes=new double[config.numOctaves];
        
        Random rnd = null;
        if(config.seed != 0)
        	rnd=new Random(config.seed);
        else
        	rnd = new Random();
        config.seed = rnd.nextInt();
    	
        float countMaxAmp =0;
        for(int i=0;i<octaves.length;i++){
            octaves[i]=new SimplexNoise(rnd.nextInt());

            frequencys[i] = Math.pow(2,i);
            //set amp based on mode
            
            double amp = 1;
            
            switch(mode) {
            case MODE_AMPDECREASE:
            	amp = Math.pow(config.persistance, i);
            	break;
            case MODE_AMPINCREASE:
            	amp = Math.pow(config.persistance, octaves.length -1);
            	break;
            }
            amplitudes[i] = amp;
            countMaxAmp += amplitudes[i];
        }
        this.maxAmp = countMaxAmp;
    }
  
    public void setOrigin(float originX, float originY) {
    	this.originX = originX;
    	this.originY = originY;
    }
    /*Reseeds the simplex map using the same config**/
    public void reseed(int seed) {
    	config.seed = seed;
    	update(this.mode);
    }
    public void setScale(float scale) {
    	config.scale = scale;
    }
    /**These methods take in the x & y coords and converts them too simplex noise*/
    public double get(double d, float y){

    	d+= originX;
    	y+= originY;
    	
        double result=0;

        for(int i=0;i<octaves.length;i++)
        {
        	double scaleFreq = config.scale * frequencys[i];
          result=result+octaves[i].noise(d * scaleFreq,y * scaleFreq)* amplitudes[i];
        }
        return result / maxAmp;
    }
    public double get(double x, double y, double z) {
        double result=0;
	    
        for(int i=0;i<octaves.length;i++)
        {
        	double scaleFreq = config.scale * frequencys[i];
          result=result+octaves[i].noise(x * scaleFreq,y * scaleFreq, z * scaleFreq)* amplitudes[i];
        }
        return result / maxAmp;
    }
    public float getNormalNoise(float x, float y) {
    	float noise = (float) get(x, y);
    	return (noise + 1)/2;
    }
    public float getModNoise(float x, float y) {
       return (float) Math.abs(get(x,y));		
    }
} 
