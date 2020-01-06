package test.game;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.coffeedriver.math.RandomHelper;

import au.com.kelpie.KelpieAPI;
import au.com.kelpie.nativ.KelpieObject;
import au.com.kelpie.nativ.NativeDrawable;

public class Player implements NativeDrawable
{
	private TextureRegion region;
	private float x;
	private float y;
	
	private float speed = 1;
	
	private TextureRegion selection;
	
	
	private Vector2 speedV;
	private float displace;
	private float nextDisplacement;
	
	public Player() {
		
	}
	
	private int cellX;
	private int cellY;
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	
	
	@Override
	public void draw(Batch batch) {
		batch.draw(region,x,y);
	}
	
	
	public void updateMove(Random random,float delta, boolean[][] moveable) {
		
		
		
		int cellX = (int) (x / 16);
		int cellY = (int) (y / 16);
		
		
		displace += Math.abs( speedV.x * delta);
		displace += Math.abs(speedV.y * delta);
		
		//chnage direction randomly
		if(displace >= nextDisplacement) {
			float c = random.nextFloat();
			float angle = speedV.angle();
			
			if(c <= 0.5)
				speedV.setAngle(angle + 90);
			else
				speedV.setAngle(angle - 90);
			
			nextDisplacement = RandomHelper.newInt(5, 10, random)*16;
			displace = 0;
		}
		
		
		if(moveable[cellX + 1][cellY]) {
			x += speedV.x * delta;
			y += speedV.y * delta;
		}
		
		
	}

	@Override
	public void update(float delta) {
		
	}

	@Override
	public KelpieObject create(KelpieAPI kelpie) {
		this.region = kelpie.getGraphics().getByRenderId(Tex.PLAYER.ref);
		this.speedV = new Vector2(4,0);
		
		x = 50 * 16;
		y = 50 * 16;
		
		nextDisplacement = RandomHelper.newInt(5, 10, kelpie.getRandom()) * 16;
		
		//this.selection = kelpie.getGraphics().getByName(Tex.HOUSE_1.key);
		return this;
	}

	@Override
	public float getWidth() {
		return region.getRegionWidth();
	}

	@Override
	public float getHeight() {
		return region.getRegionWidth();
	}

}
