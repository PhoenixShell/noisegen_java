package test.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coffeedriver.math.RandomHelper;

import au.com.kelpie.KelpieAPI;
import au.com.kelpie.nativ.KelpieObject;
import au.com.kelpie.nativ.NativeDrawable;

public class DawnTree implements NativeDrawable
{
	private TextureRegion graphic;
	private float x;
	private float y;
	
	private float alpha;
	
	
	
	public DawnTree(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public KelpieObject create(KelpieAPI api) {
		
		float stump = api.getRandom().nextFloat();
		
		if(stump <= 0.03) {
			graphic = 
					api.getGraphics().getByRenderId(Tex.STUMP.ref);
		}
		else
		{
			int random = api.getRandom().nextInt(Tex.Group.RANDOM_TREE.length);
			
			graphic = 
					api.getGraphics().getByRenderId(Tex.Group.RANDOM_TREE[random].ref);
			
		}
		alpha = RandomHelper.newFloat(.7f, 1f, api.getRandom());
		
		

		
		
		return this;
	}

	@Override
	public void draw(Batch batch) {
		Color prev = batch.getColor();
		batch.setColor(alpha, alpha, alpha, 1);
		batch.draw(graphic, x, y);
		batch.setColor(prev);
	}

	@Override
	public void update(float delta) {
		
	}

	@Override
	public float getWidth() {
		return graphic.getRegionWidth();
	}

	@Override
	public float getHeight() {
		return graphic.getRegionHeight();
	}

}
