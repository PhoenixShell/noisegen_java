package test.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.coffeedriver.engine.DataLayerRenderer;
import com.coffeedriver.libgdx.BaseInputProcessor;
import com.coffeedriver.math.CoffeeMath;
import com.coffeedriver.math.Coord;
import com.coffeedriver.math.Grid;
import com.coffeedriver.math.GridPattern;
import com.coffeedriver.math.GridPattern.OffsetPoint;
import com.coffeedriver.math.GridSquare;
import com.coffeedriver.math.MatrixPointer;
import com.coffeedriver.math.RandomHelper;
import com.coffeedriver.math.NoiseMap;
import au.com.kelpie.KelpieAPI;
import au.com.kelpie.nativ.KelpieObject;
import au.com.kelpie.nativ.KelpieTile;
import au.com.kelpie.nativ.NativeDrawable;
import test.game.Tex.Group;

import static test.game.Tex.Group.*;

public class DawnMap implements NativeDrawable
{
	private static final String TAG_GRASS = "grass";
	
	public void mouseClick(float x, float y) {
		int cellX = (int)x/16;
		int cellY = (int)y/16;
		
		System.out.printf("x: %.2f,y: %.2f\n", x,y);
		
		tileData[cellX][cellY].setVisible(false);
		
		System.out.println(transition[cellX][cellY]);
	}

	
	static final int BU = 	1 << 0;
	static final int BL = 	1 << 1;
	static final int BD = 	1 << 2;
	static final int BR = 	1 << 3;
	
	
	private Random random = new Random();
	
	
	private final static int REGION_SIZE = 700;
	private int tileSize;
	private KelpieAPI kelpie;
	
	private KelpieTile[][] tileData;
	private boolean moveable[][];
	
	private int[][] transition;
	private boolean[][] filled;
	
	private Grid grid;
	private BitmapFont font= new BitmapFont();
	
	
	private Array<NativeDrawable> trees;
	private Array<NativeDrawable> stumps;
	private Array<NativeDrawable> trim;
	
	private Player player1;
	
	double[][] heightMap;
	NoiseMap map;
	float waterLevel;
	
	DataLayerRenderer render;
	
	NoiseMap turn;
	RandomWalk walk;
	
	MatrixPointer pointer;
	
	public DawnMap(KelpieAPI api) {
		this.kelpie = api;
		this.pointer = new MatrixPointer(REGION_SIZE);
		this.pointer.position(100, 100);
		create(api);
	}
	
	
	@Override
	public void draw(Batch batch) {
	
		//DRAW TILES
		
		
		for(int x=0; x < REGION_SIZE; x++) {
			for(int sy= REGION_SIZE -1; sy > 0; sy--) {
				GridSquare sq = grid.getSquare(x, sy);
				
				
				tileData[x][sy].draw(batch);
				int tValue = transition[x][sy];
				
				
				
				//kelpie.drawId(batch, tileData[x][y], sq.getWorldX(), sq.getWorldY());
			}
		}
		//DRAW OBJECTS
		for(NativeDrawable t : trim)
			t.draw(batch);
		
		player1.draw(batch);

		for(NativeDrawable st : stumps)
			st.draw(batch);
		
		for(NativeDrawable ob : trees)
			ob.draw(batch);
		
		
		font.draw(batch,"1", 0,16);
		
		
	
		//render.draw(batch, new ShapeRenderer());
		
		
	}
	int[] coord = new int[2];
	
	@Override
	public void update(float delta) {
		
		
		/*
		boolean[] state = pointer.newState();
		pointer.getState(state);
		
		for(int i=0; i < state.length; i++) {
			pointer.getPoint(i, coord);
			if(state[i])
				tileData[coord[0]][coord[1]].setVisible(false);
		}
		pointer.translate(-1, 0);
		
		
		player1.updateMove(random,delta, moveable);
		walk.update(turn, map, waterLevel);
		//loadMap();
		 * 
		 */
	}

	@Override
	public KelpieObject create(KelpieAPI kelpie)
	{
		this.turn = new NoiseMap(3, .7f, 0, NoiseMap.MODE_AMPDECREASE);
		this.turn.setScale(1f);
		
		
		this.transition = new int[REGION_SIZE][REGION_SIZE];
		this.trim = new Array<NativeDrawable>(100);
		this.kelpie = kelpie;
		this.tileSize = kelpie.getGraphics().getTileSize();
		this.moveable= new boolean[REGION_SIZE][REGION_SIZE];
		this.tileData = new KelpieTile[REGION_SIZE][REGION_SIZE];
		this.trees = new Array<>();
		this.stumps = new Array<>();
		this.grid = new Grid(REGION_SIZE, REGION_SIZE, tileSize, new Coord(0,0));
		this.player1 = (Player) new Player().create(kelpie);
		
		this.walk = new RandomWalk(random);
		
		
		this.filled = new boolean[REGION_SIZE][REGION_SIZE];
		
		loadMap();
		smoothStep();
		updateTransition();
		
		
		
		
		return this;
	}
	
	private void loadMap() {
		Random random = new Random();
		float maxElevation = 100;
		waterLevel = 48;
		final float cloudLevel = 70;
		final float fertileLevel = 54;
		final float persist = 1f;
		float scale = .0000005f;
		final float SNOW_HEIGHT = 55;
		
		final float pixScale = .0003f;
		
		int tileSize =kelpie.getGraphics().getTileSize();
		scale = pixScale / (tileSize * tileSize);
		
		NoiseMap zone = new NoiseMap(8,.3f,random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		zone.setScale(.1f);
		
		
		
	
		
		map = new NoiseMap(16,persist,random.nextInt(), NoiseMap.MODE_AMPDECREASE);
		map.setScale(scale);
		
		NoiseMap treeMap = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		treeMap.setScale(.008f);
		
		NoiseMap beachMap = new NoiseMap(6,.5f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		beachMap.setScale(.01f);
	
		
		NoiseMap tallGrass = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		tallGrass.setScale(.05f);
		
		NoiseMap road = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		road.setScale(0.0005f);
		
		NoiseMap river = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		river.setScale(0.0005f);
		
		NoiseMap hay = new NoiseMap(6,.7f, random.nextInt(),NoiseMap.MODE_AMPDECREASE);
		hay.setScale(0.01f);
		
		
		heightMap = new double[REGION_SIZE][REGION_SIZE];
		
		//tiles
		for(int x=0; x < REGION_SIZE; x++) {
			for(int y=0; y < REGION_SIZE; y++) {
				
				
				int currentZone = (int)(zone.getNormalNoise(x*.01f, y * .01f)/.2f);
				
				
				
				
				
				GridSquare sq = grid.getSquare(x, y);
				heightMap[x][y] = ((map.get(x, y) +1)/2) * maxElevation;
				//maxElevation;
				float noise = (float) map.get(x, y);
				
				double currentHeight = heightMap[x][y];
				float r = 0.0040f;
				
				boolean isRoad = road.get(x, y) <= r && road.get(x, y) >= -r;
				boolean hasTree = false;
				
				//boolean isRiver = river.get(x, y);
				
				if(isRoad && currentHeight > waterLevel -1.5)
				{
					tileData[x][y] = new KelpieTile(Tex.WATER3.ref, sq.wx(), sq.wy(), kelpie,1);
				}
				
				else if(heightMap[x][y] <= waterLevel) {
					if(currentHeight > waterLevel - .5f && random.nextFloat() <= .01f) {
						tileData[x][y] = new KelpieTile(Tex.ROCK1.ref, sq.wx(), sq.wy(), kelpie, 0);
					}
						
					else if(currentHeight <= waterLevel - 1.5)
					{
						tileData[x][y] = new KelpieTile(Tex.DEEP_WATER.ref, sq.wx(), sq.wy(), kelpie, 1);
					}
					else
					{
						if(currentHeight >= waterLevel -.2f && random.nextFloat() <= .1f) {
							tileData[x][y] = new KelpieTile(Tex.REEDS.ref, sq.wx(), sq.wy(), kelpie, 1);
						}
						else {
							tileData[x][y] = new KelpieTile(Tex.WATER3.ref, sq.wx(), sq.wy(), kelpie, 1);
						}
						
					}
						
					//nativeTiles[x][y] = new KelpieTile(Tex.WATER.ref, sq.getWorldX(), sq.getWorldY()).create(kelpie);
					
					//tileData[x][y].setColor((float) CoffeeMath.mapRange(-1 * maxElevation,waterLevel , .5f, 1, (float) currentHeight));
				}
				else if(beachMap.get(x, y) <= .2f && currentHeight <= waterLevel + 1f) {
					moveable[x][y] = true;
					tileData[x][y] = new KelpieTile(Tex.SAND.ref, sq.wx(), sq.wy(), kelpie,2);
				}
				//grassland
				
				
				else if(heightMap[x][y] <= fertileLevel) 
				{
					
					
					moveable[x][y] = true;
					
					
					
					if(currentHeight > fertileLevel + 2 && hay.get(x / 10, y / 10)>= .2f) {
						tileData[x][y] = new KelpieTile(Tex.HAY.ref, sq.wx(), sq.wy(), kelpie,4);
					}
					else if(random.nextFloat() <= .02) {
						int flowerId = G_FLOWERS[random.nextInt(G_FLOWERS.length)].ref;
						tileData[x][y] = new KelpieTile(flowerId, sq.wx(), sq.wy(), kelpie,4);
					}
					else {
						
						float tall = (float) tallGrass.get(x, y);
						if(tall >= .2f && random.nextFloat() <= 1f) {
							tileData[x][y] = new KelpieTile(G_TALL[random.nextInt(G_TALL.length)].ref, sq.wx(), sq.wy(), kelpie,4);
							
							if(random.nextFloat() <= .05)
								stumps.add(new KelpieTile(Tex.SMALL_STUMP.ref, sq.wx(), sq.wy(),kelpie, 0));
						}
						else {
							int grassId = G_GRASS[random.nextInt(G_GRASS.length)].ref;
							hasTree = true;
							tileData[x][y] = new KelpieTile(grassId, sq.wx(), sq.wy(), kelpie,4, TAG_GRASS);
						}
					}
					
				}
				else if(currentHeight > SNOW_HEIGHT) {
					tileData[x][y] = new KelpieTile(Tex.SNOW.ref, sq.wx(), sq.wy(), kelpie,928);
					if(random.nextFloat() <= .1f)
						hasTree = true;
				}
				
				else {
					tileData[x][y] = new KelpieTile(Tex.DEFAULT.ref, sq.wx(), sq.wy(), kelpie,5);
					if(random.nextFloat() <= .3f)
						hasTree = true;
					
					//nativeTiles[x][y] = new KelpieTile(Tex.GRASS2.ref, sq.getWorldX(), sq.getWorldY()).create(kelpie);
				}
				
				float treeValue = (float) treeMap.get(x, y);
				
				if(hasTree && !isRoad && currentHeight < cloudLevel && tileData[x][y].getRenderId() != Tex.HAY.ref && treeValue <=.08f &&  x % 3 == 0 && y % 3 ==0 && random.nextFloat() <= .8) {
					
					//float stump = random.nextFloat()
					
					trees.add((NativeDrawable) new DawnTree(sq.wx(), sq.wy()).create(kelpie));
					
					//NativeDrawable tree = new KelpieTile(Tex.TREE_1.ref, sq.getWorldX(), sq.getWorldY(), kelpie);
					//tree.create(kelpie);
					//objects.add(tree);
				}
				//tileData[x][y].setColor((float) CoffeeMath.mapRange(waterLevel,maxElevation , 1, .5f, (float) currentHeight));
				
				//tileData[x][y].setColor(CoffeeMath.map(noise, -1,1,.5f,1f));
				
				float cloudInt = 65;
				if(currentHeight > cloudLevel) {
					//tileData[x][y].setAlpha((float) CoffeeMath.mapRange(cloudLevel, maxElevation-cloudInt, 1f, .5f, (float) currentHeight));
				}
				if(currentHeight >= maxElevation-cloudInt) {
					
				}
					//tileData[x][y].setAlpha(.5f);
				//tileData[x][y].setColor(Color.SALMON);
				/*
				switch(currentZone) {
				case 1:
					tileData[x][y].setColor(Color.SALMON);
					break;
				case 2:
					tileData[x][y].setColor(Color.GREEN);
					break;
				case 3:
					tileData[x][y].setColor(Color.YELLOW);
					break;
				case 4:
					tileData[x][y].setColor(Color.CYAN);
					break;
				case 5:
					tileData[x][y].setColor(Color.GOLD);
					break;
				}
				*/
				
				
			}
		}
		//load house
		int hx = 0;
		int hy = 0;
		
		int tryTimes = 50;
		int count = 0;
		/*
		for(int i=0; i < 10; i++) {
			do {
				hx = random.nextInt(REGION_SIZE);
				hy = random.nextInt(REGION_SIZE);
			
			}
			while(count < tryTimes && tileData[hx][hy].identity() != 4);
			++count;
			objects.add(new KelpieTile(RANDOM_HOUSE[random.nextInt(RANDOM_HOUSE.length)].ref, hx * 16, hy * 16, kelpie, 0));
		}
		*/
		
		//map.setScale(.001f);
		render = new DataLayerRenderer(map.export(0, 0, REGION_SIZE, REGION_SIZE));
		
		
		//System.out.println("hey");
	}
	
	private void setTrim() {
		for(int x=0; x < REGION_SIZE; x++) {
			for(int y=0; y < REGION_SIZE; y++) {
				GridSquare sq = grid.getSquare(x, y);
				
				
				
				int tValue = transition[x][y];
				double height = heightMap[x][y];
				
				if(tileData[x][y].identity() == 4)
				{
					switch(tValue) {
					case BU + BL + BR:
						trim.add(new KelpieTile(Tex.T2.ref, sq.wx(), sq.wy()-16, kelpie, 0));
						break;
						
					case BD + BL + BR:
						trim.add(new KelpieTile(Tex.T7.ref, sq.wx(), sq.wy()+16, kelpie, 0));
						break;
					case BU + BR + BD:
						trim.add(new KelpieTile(Tex.T5.ref, sq.wx()-16, sq.wy(), kelpie, 0));
						//kelpie.drawId(batch, Tex.T5.ref, sq.getWorldX()-16, sq.getWorldY());
						break;
					case BD + BL:
						trim.add(new KelpieTile(Tex.T6.ref, sq.wx(), sq.wy(), kelpie, 0));
						tileData[x][y].setRenderId(tileData[x+1][y+1].getRenderId(),kelpie);
						//kelpie.drawId(batch, Tex.T5.ref, sq.getWorldX()-16, sq.getWorldY());
						break;
					case BD + BR:
						trim.add(new KelpieTile(Tex.T8.ref, sq.wx(), sq.wy(), kelpie, 0));
						tileData[x][y].setRenderId(tileData[x-1][y+1].getRenderId(),kelpie);
						//kelpie.drawId(batch, Tex.T5.ref, sq.getWorldX()-16, sq.getWorldY());
						break;
					case BR + BU:
						//objects.add(new KelpieTile(Tex.WATER.ref, sq.getWorldX(), sq.getWorldY(), kelpie, 0));
						tileData[x][y].setRenderId(tileData[x-1][y-1].getRenderId(), kelpie);
						trim.add(new KelpieTile(Tex.T3.ref, sq.wx(), sq.wy(), kelpie, 0));
						trim.add(new KelpieTile(Tex.T10.ref, sq.wx()-16, sq.wy(), kelpie, 0));
						trim.add(new KelpieTile(Tex.T10.ref, sq.wx()-16, sq.wy()+16, kelpie, 0));
						//kelpie.drawId(batch, Tex.WATER.ref, sq.getWorldX(), sq.getWorldY());
						//kelpie.drawId(batch, Tex.T3.ref, sq.getWorldX(), sq.getWorldY());
						//tileData[x][y].setVisible(false);
						break;
					case BU + BL + BD:
						trim.add(new KelpieTile(Tex.T4.ref, sq.wx()+16, sq.wy(), kelpie, 0));
						//kelpie.drawId(batch, Tex.DEFAULT.ref, sq.getWorldX()+16, sq.getWorldY());
						
						break;
					case BL + BU:
						trim.add(new KelpieTile(Tex.T1.ref, sq.wx(), sq.wy(), kelpie, 0));
						//objects.add(new KelpieTile(Tex.WATER.ref, sq.getWorldX(), sq.getWorldY(), kelpie, 0));
						tileData[x][y].setRenderId(tileData[x+1][y-1].getRenderId(),kelpie);
						break;
					}
				}
				else if(tileData[x][y].identity() == 2)
				{
					
				}
			}
		}
	}
	private void smoothStep() {
		
		for(int x=1; x < REGION_SIZE -1; x+= 4) {
			for(int y=1; y < REGION_SIZE -1; y+= 4) {	
				
				
				KelpieTile tile1 = tileData[x-1][y+1];
				KelpieTile tile = tileData[x][y];
				KelpieTile tile2 = tileData[x][y];
				
				if(tile1.matches(tile) && tile1.identity() == 4) {
					KelpieTile change = tileData[x-1][y];
					//change.setRenderId(Tex.GRASS1.ref, kelpie);
					//change.setIndentity(4);
					
				}
				
				
			
			
			
			}
			
			
			
			
			
			
		}
		
	}
	
	
	private void updateTransition() {
		
		
		
		for(int x=1; x < REGION_SIZE -1; x++) {
			for(int y=1; y < REGION_SIZE-1; y++) {
				
				int tValue = 0;
				
				KelpieTile UL = tileData[x-1][y+1];
				KelpieTile U = tileData[x][y+1];
				KelpieTile UR = tileData[x+1][y+1];
				 
				KelpieTile L = tileData[x-1][y];
				KelpieTile R = tileData[x+1][y];
				
				KelpieTile DL = tileData[x-1][y-1];
				KelpieTile D = tileData[x][y-1];
				KelpieTile DR = tileData[x+1][y-1];
				
				KelpieTile current = tileData[x][y];
				
				boolean fil = sameTile(current, UL, U, UR, L, R, DL, D, DR);
				
				
				filled[x][y] = fil;
				
				if(U.matches(current))
					tValue += BU;
				if(R.matches(current))
					tValue += BR;
				if(D.matches(current))
					tValue += BD;
				if(L.matches(current))
					tValue += BL;
				
				transition[x][y] = tValue;
			}
		}
		setTrim();
		
	}
	
	public boolean sameTile(KelpieTile tile, KelpieTile... tiles) {
		
		for(int i=0; i < tiles.length; i++) {
			if(!tile.matches(tiles[i]))
				return false;
		}
		return true;
	}
	
	@Override
	public float getWidth() {
		return REGION_SIZE * tileSize;
	}

	@Override
	public float getHeight() {
		return REGION_SIZE * tileSize;
	}
	
	
	public static interface IN{
		
		public void mouseClick(float worldX, float worldY);
		
	}
	
	public static interface OUT{
		
	}
	

}
