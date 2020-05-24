package com.edusanchezcon.sandbox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.edusanchezcon.sandbox.MyApp;
import com.edusanchezcon.sandbox.scene2dexample.Scene;

public class Scene2dScreen extends BaseScreen {


    private static final float BLOCK_SIZE = 1;
    public static final String NAME = "Prueba con Scene2d";

    private Stage stage;
    private OrthographicCamera cam;
    private Scene scene;

    public Scene2dScreen(MyApp game){
        super(game, NAME);

        cam = new OrthographicCamera();
        stage = new Stage(new ExtendViewport(10, 10, cam), batch);
    }

    @Override
    public void show(){
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        // we add also (InputProcessor)this to have the <ESC> key navigation
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));

        initWorld(BLOCK_SIZE / 32f);
    }

    private void initWorld(float unitScale){
        scene = new Scene(stage, unitScale, shapeRenderer);
        scene.setDrawCollisionShapes(false);
    }

    @Override
    public void render(float delta){
        super.render(delta);
        scene.update();
    }

    @Override
    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose(){
        super.dispose();
        scene.dispose();
        stage.dispose();
    }

}
