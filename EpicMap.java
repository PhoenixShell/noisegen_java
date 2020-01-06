package test.game;

import com.badlogic.gdx.graphics.g2d.Batch;

import au.com.kelpie.KelpieAPI;
import au.com.kelpie.nativ.KelpieObject;
import au.com.kelpie.nativ.NativeDrawable;
import au.com.kelpie.nativ.RuntimeTile;

public class EpicMap implements NativeDrawable
{
	private static final int SIZE= 100;
	
	private RuntimeTile[][] tiles= new RuntimeTile[SIZE][SIZE];
	
	private RuntimeTile def;
	private RuntimeTile grass;
	private RuntimeTile water;
	
	private RuntimeTile player;
	
	private int oriX = 50 * 16;
	private int oriY = 50 * 16;
	private int distance =15 * 16;
	
	private float dx = 0;
	private float speed;
	
	
	public void paintRect(int ox, int oy, int w, int h, RuntimeTile tile) {
		
		for(int x=0; x < w; x++) {
			for(int y=0; y < h; y++) {
				
				tiles[x + ox][y + oy] = tile;	
			}
		}
		
	}
	
	
	
	
	public EpicMap(KelpieAPI api) {
		create(api);
	}	
	
	
	@Override
	public KelpieObject create(KelpieAPI api) {
		def = (RuntimeTile) new RuntimeTile(Tex.DEFAULT).create(api);
		grass = (RuntimeTile) new RuntimeTile(Tex.GRASS1).create(api);
		water = (RuntimeTile) new RuntimeTile(Tex.WATER3).create(api);
		player = (RuntimeTile) new RuntimeTile(Tex.PLAYER).create(api);
		
		for(int x=0; x < SIZE; x++) {
			for(int y=0; y < SIZE; y++) {
				tiles[x][y] = water;
			}
		}
		paintRect(2, 2, 5, 10, grass);
		paintRect(2,2,10,2, grass);
		paintRect(10,10,50,50, grass);
		
		
		
		return this ;
	}

	@Override
	public void draw(Batch batch) {

		for(int x=0; x < SIZE; x++) {
			for(int y=0; y < SIZE; y++) {
				tiles[x][y].draw(batch, x * 16, y * 16);
			}
		}
		
		player.draw(batch, oriX + dx, oriY);
		
		
	}

	@Override
	public void update(float delta) {


		
		dx += speed * delta;
		dx %= distance;
		
		
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
