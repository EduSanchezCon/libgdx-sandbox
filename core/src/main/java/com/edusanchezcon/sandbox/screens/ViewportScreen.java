package com.edusanchezcon.sandbox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class ViewportScreen extends BaseScreen {

    private static final float WORLD_WIDTH = 100;
    private static final float WORLD_HEIGHT = 100;
    public static final String NAME = "Pruebas de viewport";

    private Sprite background;
    private OrthographicCamera cam;
    private Viewport viewport;

    public ViewportScreen(MyApp game){
        super(game, NAME);

        cam = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, cam);
        viewport.apply();
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        cam.update();

        background = new Sprite(new Texture("screenSizes.jpg"));
        background.setSize(WORLD_WIDTH, WORLD_HEIGHT);
    }

    @Override
    public void render(float delta){
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        super.render(delta);

        batch.begin();
        background.draw(batch);
        batch.end();
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button){
        if (Gdx.input.isTouched()){
            Vector3 vec3 = new Vector3(screenX, screenY, 0);
            Gdx.app.log("Mouse Event","Click at " + vec3.x + "," + vec3.y);
            Vector3 worldCoordinates = cam.unproject(vec3);
            Gdx.app.log("Mouse Event","Projected at " + worldCoordinates.x + "," + worldCoordinates.y);
        }
        return true;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
    }

    @Override
    public void dispose(){
        super.dispose();
        background.getTexture().dispose();
    }

}
