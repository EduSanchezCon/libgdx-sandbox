package com.edusanchezcon.sandbox.screens;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class MainMenuScreen extends BaseScreen {

    private OrthographicCamera camera;
    private Viewport viewport;
    private IntMap<Function<MyApp, Screen>> screensMenu= new IntMap<>();
    private List<String> screenTitles;

    public MainMenuScreen(MyApp game){
        super(game, "Menú");

        camera = new OrthographicCamera();
        viewport = new FitViewport(MyApp.WIDTH, MyApp.HEIGHT, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
        camera.update();

        screensMenu.put(Input.Keys.NUM_1, CameraPlaygroundScreen::new);
        screensMenu.put(Input.Keys.NUM_2, ViewportScreen::new);
        screensMenu.put(Input.Keys.NUM_3, CoordinateSystemsScreen::new);
        screensMenu.put(Input.Keys.NUM_4, TexturePlayGroundScreen::new);
        screensMenu.put(Input.Keys.NUM_5, AnimationsScreen::new);
        screensMenu.put(Input.Keys.NUM_6, TextureAtlasScreen::new);
        screensMenu.put(Input.Keys.NUM_7, InputAnimationScreen::new);
        screensMenu.put(Input.Keys.NUM_8, MouseInputScreen::new);
        screensMenu.put(Input.Keys.NUM_9, ParticleEffectsScreen::new);
        screensMenu.put(Input.Keys.A, TiledMapScreen::new);
        screensMenu.put(Input.Keys.B, Scene2dScreen::new);
        screensMenu.put(Input.Keys.C, Scene2dUIMenuScreen::new);
        screensMenu.put(Input.Keys.D, Box2dScreen::new);

        screenTitles = Arrays.asList(
                CameraPlaygroundScreen.NAME,
                ViewportScreen.NAME,
                CoordinateSystemsScreen.NAME,
                TexturePlayGroundScreen.NAME,
                AnimationsScreen.NAME,
                TextureAtlasScreen.NAME,
                InputAnimationScreen.NAME,
                MouseInputScreen.NAME,
                ParticleEffectsScreen.NAME,
                TiledMapScreen.NAME,
                Scene2dScreen.NAME,
                Scene2dUIMenuScreen.NAME,
                Box2dScreen.NAME);
    }

    @Override
    public void render(float delta){
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        super.render(delta);
        int i=0;
        batch.begin();
        for (String title: screenTitles){
            font.draw(batch, String.format("%x - %s", ++i, title), 10,camera.viewportHeight-50 - 40*i);
        }
        batch.end();
    }

    @Override
    public boolean keyDown(int key){
        if (screensMenu.containsKey(key)){
            game.setScreen( screensMenu.get(key).apply(game) );
            dispose();
        }
        return true;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
    }
}
