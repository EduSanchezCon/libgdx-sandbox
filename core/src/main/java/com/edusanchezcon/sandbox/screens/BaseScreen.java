package com.edusanchezcon.sandbox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.edusanchezcon.sandbox.MyApp;

public abstract class BaseScreen implements Screen, InputProcessor{

    protected final MyApp game;
    protected SpriteBatch batch;
    protected BitmapFont font;
    protected ShapeRenderer shapeRenderer;
    private final String screenName;

    public BaseScreen(MyApp game, String screenName){
        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();
        this.shapeRenderer = game.getShape();
        this.screenName = screenName;
        Gdx.input.setInputProcessor(this);
    }


    @Override
    public void show(){
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Pulsa <ESC> para volver al menú ", 10, MyApp.HEIGHT - 10);
        font.draw(batch, screenName, 10, MyApp.HEIGHT - 40);
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode){
        if (keycode == Input.Keys.ESCAPE){
            Gdx.app.postRunnable(() -> {
                game.setScreen(new MainMenuScreen(game));
            });
        }
        return false;
    }

    @Override
    public void resize(int width, int height){

    }

    @Override
    public void pause(){

    }

    @Override
    public void resume(){

    }

    @Override
    public void hide(){

    }

    @Override
    public void dispose(){

    }

    @Override
    public boolean keyUp(int keycode){
        return false;
    }

    @Override
    public boolean keyTyped(char character){
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button){
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer){
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY){
        return false;
    }

    @Override
    public boolean scrolled(int amount){
        return false;
    }
}
