package com.edusanchezcon.sandbox;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.edusanchezcon.sandbox.screens.MainMenuScreen;

public class MyApp extends Game{

	public static final float WIDTH = 900;
	public static final float HEIGHT = 600;

	private Screen currentScreen;
	private SpriteBatch batch;
	private BitmapFont font;
	private ShapeRenderer shape;



	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		shape = new ShapeRenderer();
		this.setScreen( new MainMenuScreen(this));
	}

	@Override
	public void setScreen(Screen screen){
		assert (screen != currentScreen);
		super.setScreen(screen);

		if (currentScreen != null){
			currentScreen.dispose();
		}
		currentScreen = screen;
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		currentScreen.dispose();
		batch.dispose();
		shape.dispose();
		font.dispose();
	}

	public SpriteBatch getBatch(){
		return batch;
	}

	public BitmapFont getFont(){
		return font;
	}

	public ShapeRenderer getShape(){
		return shape;
	}
}
