package test.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.coffeedriver.engine.DataLayerRenderer;
import com.coffeedriver.math.Coord;
import com.coffeedriver.math.Grid;
import com.coffeedriver.math.GridSquare;

import com.coffeedriver.math.NoiseMap;
import au.com.kelpie.KelpieAPI;
import au.com.kelpie.nativ.KelpieObject;
import au.com.kelpie.nativ.KelpieTile;
import au.com.kelpie.nativ.NativeDrawable;
import au.com.kelpie.nativ.RuntimeTile;

import static test.game.Tex.Group.*;

public class Sun implements NativeDrawable
{
	private Random random = new Random();
	
	
	private final static int SIZE = 100;
	private int tileSize;
	private KelpieAPI kelpie;
	
	private int[][] tileData;
	private boolean moveable[][];
	

	private Grid grid;
	
	private static final int GRASS = 0;
	private static final int WATER = 1;
	
	private RuntimeTile grass;
	private RuntimeTile water;
	private RuntimeTile def;
	private RuntimeTile player;
	
	
	private float dx = 0;
	private float dy = 0;
	private float dz = 0;
	
	
	private float speedFreq = 1f;
	
	private RandomWalk walk;
	
	
	private Player player1;
	
	double[][] mapper;
	int mapseed;
	
	float waterLevel;
	
	private NoiseMap turn;
	private RandomWalk[] walks = new RandomWalk[5000];
	private int drawLength = 1;;
	private NoiseMap heightmap;
	
	private Pixmap pixmap;
	
	private Texture texmap;
	
	private NoiseMap temp = new NoiseMap(8,.7f,0,NoiseMap.MODE_AMPDECREASE);
	private NoiseMap humid = new NoiseMap(8,.7f,0, NoiseMap.MODE_AMPDECREASE);
			
	
	
	public Sun(KelpieAPI api) {
		Random random = new Random();
		this.mapper = new double[SIZE][SIZE];
		this.mapseed = random.nextInt();
		this.kelpie = api;
		this.pixmap = new Pixmap(100,100,Format.RGB888);
		create(api);
		
		grass = (RuntimeTile) new RuntimeTile(Tex.GRASS1).create(api);
		water = (RuntimeTile) new RuntimeTile(Tex.WATER3).create(api);
		def = (RuntimeTile) new RuntimeTile(Tex.DEFAULT).create(api);
		player = (RuntimeTile) new RuntimeTile(Tex.PLAYER).create(api);
		
		turn = new NoiseMap(3, .7f, 0, NoiseMap.MODE_AMPDECREASE);
		turn.setScale(speedFreq);
		
		for(int i=0; i < walks.length; i++) {
			walks[i] = new RandomWalk(random);
		}
		
	}
	
	float min = Float.MAX_VALUE;
	float max = Float.MIN_VALUE;
	
	@Override
	public void draw(Batch batch) {
		
	if(texmap != null)
			texmap.dispose();
		
		for(int x=0; x < pixmap.getWidth(); x++) {
			for(int y=0; y < pixmap.getHeight(); y++) {
				//get and draw noise map;
				float freq = 0.001f;
				
				float t1 = (float) temp.getNormalNoise(x * freq, y * freq);
				float t2 = (float) humid.getNormalNoise(x * freq, y * freq);
				
				float noise = humid.getNormalNoise(t1, t1);
				
				min = Math.min(noise, min);
				max = Math.max(max, noise);
				
				float mid = min + (max-min);
				
				
				
				
				
				
				//float noise = (t1 * t2)/1;
				
				
						
				pixmap.setColor(noise, noise, noise, 1);
				pixmap.drawPixel(x, y);
			}
		}
		
		
	
		//DRAW TILES
		for(int x=0; x < SIZE; x++) {
			for(int y=0; y < SIZE; y++) {
				GridSquare sq = grid.getSquare(x, y);
				
				int tile = tileData[x][y];
				
				RuntimeTile rt = def;
				
				switch(tile) {
				case GRASS:
					rt = grass;
					break;
				case WATER:
					rt = water;
					
				}
				rt.draw(batch, sq.wx(), sq.wy());
			}
		}
		for(int i=0; i < 5000; i++) {
			float px = walks[i].pos.x;
			float py = walks[i].pos.y;
			
			if(walks[i].visible)
				player.draw(batch, px, py);
		}
		
		
		texmap = new Texture(pixmap);
		//batch.draw(texmap,0,0);
		//tex.dispose();
		
		
	}

	@Override
	public void update(float delta) {
		
		//loadMap();
		for(int i=0; i < walks.length; i++) {
			int x = (int) (walks[i].pos.x / 16);
			int y = (int) (walks[i].pos.y / 16);
			
			if(x >=0 && x < SIZE && y >= 0 && y < SIZE)
				walks[i].update(turn,heightmap,waterLevel);
		}
		
		
		
		//waterLevel = 0;
		
		waterLevel =(float) (50 + ((Math.sin(System.currentTimeMillis() * 0.0001) * .5)));
		loadMap();
		
	}

	@Override
	public KelpieObject create(KelpieAPI kelpie)
	{
		this.kelpie = kelpie;
		this.tileSize = kelpie.getGraphics().getTileSize();
		this.moveable= new boolean[SIZE][SIZE];
		this.tileData = new int[SIZE][SIZE];
		
		
		this.grid = new Grid(SIZE, SIZE, tileSize, new Coord(0,0));
		this.player1 = (Player) new Player().create(kelpie);
		
		loadMap();
	
		return this;
	}
	
	private void loadMap()
	{
		long now = System.currentTimeMillis();
		
		
		//Random random = new Random(now / 2000);
		float maxElevation = 100;

		
		final float cloudLevel = 70;
		final float fertileLevel = 54;
		final float persist = 1f;
		
		float heightScale = .001f;
		
		final float SNOW_HEIGHT = 55;
		 
		
		
		
		
		//NoiseMap zone = new NoiseMap(8,.3f,random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		//zone.setScale(.1f);
		
		
		
	
		
		heightmap = new NoiseMap(16,persist,1, NoiseMap.MODE_AMPDECREASE);
		heightmap.setScale(heightScale);
		
		/*
		NoiseMap treeMap = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		treeMap.setScale(1 * heightScale);
		
		NoiseMap beachMap = new NoiseMap(6,.5f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		beachMap.setScale(1 * heightScale);
	
		
		NoiseMap tallGrass = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		tallGrass.setScale(1 * heightScale);
		
		NoiseMap road = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		road.setScale(1 * heightScale);
		
		NoiseMap river = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		river.setScale(.0001f * heightScale);
		
		NoiseMap hay = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		hay.setScale(1 * heightScale);
		*/
		//long seed = System.nanoTime() / 1000000;
		
		
		
		
		//tiles
		for(int x=0; x < SIZE; x++) {
			for(int y=0; y < SIZE; y++) {
				
			
			
				GridSquare sq = grid.getSquare(x, y);
				//heightMap[x][y] = ((map.get(x + dx, y + dy) +1)/2) * maxElevation;
				
				mapper[x][y] = (heightmap.get(x * heightScale, y * heightScale, 0)+1)/2     * maxElevation;
			
				double currentHeight = mapper[x][y];
				
				float freq = 0.1f;
				
				float t1 = (float) temp.getNormalNoise(x * freq, y * freq);
				
				float t2 = (float) humid.getNormalNoise((float)temp.get(x, y), (float)temp.get(x, y));
				//float noise = ((t1 * t2)/1) * maxElevation;
				float noise = t2;
				
				//heightMap[x][y] = noise;
				currentHeight = mapper[x][y];
				
				//System.out.println(currentHeight);
				
				
	
				//waterlevel
				if(currentHeight <= waterLevel) {
					tileData[x][y] = WATER;
				}
				//grassland
				else
				{
					moveable[x][y] = true;
					int grassId = G_GRASS[random.nextInt(G_GRASS.length)].ref;
					tileData[x][y] = GRASS;
				}
			}
		}
		//render = new DataLayerRenderer(map.export(0, 0, REGION_SIZE, REGION_SIZE));
	}
	
	@Override
	public float getWidth() {
		return SIZE * tileSize;
	}

	@Override
	public float getHeight() {
		return SIZE * tileSize;
	}
}
