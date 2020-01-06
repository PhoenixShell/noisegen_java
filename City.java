package test.game;

import java.util.Random;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.coffeedriver.libgdx.FlexiCam;
import com.coffeedriver.math.Coord;
import com.coffeedriver.math.Grid;
import com.coffeedriver.math.NoiseMap;
import au.com.kelpie.KelpieAPI;
import au.com.kelpie.nativ.KelpieObject;
import au.com.kelpie.nativ.NativeDrawable;

public class City implements NativeDrawable
{
	
	private final int SIZE = 100;
	private final int tSize = 16;
	private float[][] map;
	
	private Grid grid = new Grid(SIZE,SIZE,4,new Coord(0,0));
	
	
	private KelpieAPI api;
	private final Random random;
	
	private ShapeRenderer shape;
	private FlexiCam cam;
	
	NoiseMap simplex = new NoiseMap(16,.7f,0, NoiseMap.MODE_AMPDECREASE);
	final float scale = 0.01f;
	
	
	public City(KelpieAPI api, FlexiCam cam) {
		random = new Random();
		this.cam = cam;
		create(api);
	}
	
	
	
	private int[] ANGLES = new int[]{90,270};
	private Vector2 vector = new Vector2(1,0);
	
	
	
	private void loadMap() {
		
	}
	
	
	
	
	@Override
	public KelpieObject create(KelpieAPI api)
	{
		this.map = new float[SIZE][SIZE];
		this.shape = new ShapeRenderer();
		this.simplex.setScale(scale);
		for(int x=0; x < SIZE; x++) {
			for(int y=0; y < SIZE; y++) {
				
				map[x][y] = simplex.getNormalNoise(x, y);
				
			
				
				//renderData[x][y] = Tex.Group.G_GRASS[random.nextInt(Tex.Group.G_GRASS.length)].ref;
			}
		}
		
		this.api = api;
		
		loadMap();
		
		
		
		
		
		return this;
	}

	@Override
	public void draw(Batch batch) {

		int cellX = 50;
		int cellY = 50;
		
		
		shape.begin(ShapeType.Filled);
		float c = random.nextFloat();
		shape.setColor(c,c,c,1);
		
		for(int i=0; i < 50; i++) {
			
			shape.rect(cellX * 4, cellY * 4, 4, 4);
			
			float u = map[cellX][cellY+1];
			float r = map[cellX + 1][cellY];
			//float d = map[cellX][cellY-1];
			
			float min = Math.min(u, r);
			//min = Math.min(min, d);
			
			if(min == u)
				cellY +=1;
			if(min == r)
				cellX +=1;
		
			
			
		}
		shape.end();
		
		
		
	}
	@Override
	public void update(float delta) {
		shape.setProjectionMatrix(cam.combined);
		
	}

	@Override
	public float getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
