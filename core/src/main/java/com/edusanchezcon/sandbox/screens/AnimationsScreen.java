package com.edusanchezcon.sandbox.screens;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class AnimationsScreen extends BaseScreen {

    private static final float WORLD_WIDTH = 100;
    private static final float WORLD_HEIGHT = 100;
    private static final float BLOCK_SIZE = 10;
    public static final String NAME = "Pruebas de Animaciones";

    private OrthographicCamera cam;
    private Viewport viewport;

    private Texture wolfSheet;
    private List<Animation<TextureRegion>> animations;
    float stateTime = 0f;
    int currentAnimation = 0;

    public AnimationsScreen(MyApp game){
        super(game, NAME);

        cam = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT * (MyApp.HEIGHT/MyApp.WIDTH), cam);
        viewport.apply();
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        cam.update();

//        walkSheet = new Texture("running-animation.png");
//        TextureRegion[][] regions = TextureRegion.split(walkSheet, walkSheet.getWidth()/6, walkSheet.getHeight()/5);
//        TextureRegion[] frames = Arrays.stream(regions).flatMap(Arrays::stream).toArray(i -> new TextureRegion[i]);
//        animation = new Animation<>(0.020f, frames);

        wolfSheet = new Texture("wolfsheet5.png");
        animations = loadWolfAnimations(wolfSheet);
    }


    @Override
    public void render(float delta){
        super.render(delta);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        stateTime += delta;
        Animation<TextureRegion> animation = animations.get(currentAnimation);
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        batch.begin();
        batch.draw(frame, 10, 10, BLOCK_SIZE, BLOCK_SIZE);
        batch.end();
    }

    private List<Animation<TextureRegion>> loadWolfAnimations(Texture sheet){
        TextureRegion rightHalf = new TextureRegion(sheet, sheet.getWidth()/2, 0, sheet.getWidth()/2, sheet.getHeight());
        TextureRegion[][] splitedFrames = rightHalf.split(rightHalf.getRegionWidth() / 5, rightHalf.getRegionHeight() / 12);

        List<List<TextureRegion>> tempRegionList = Arrays.stream(splitedFrames)
                .map(row -> Arrays.stream(row).collect(Collectors.toList()))
                .collect(Collectors.toList());

        // remove empty regions
        tempRegionList.remove(1);
        tempRegionList.remove(6);
        tempRegionList.get(0).remove(4);
        tempRegionList.get(1).remove(4);
        tempRegionList.get(5).remove(4);
        tempRegionList.get(6).remove(4);

        return tempRegionList.stream()
                .map(row -> new Animation<TextureRegion>(
                        0.2f, row.toArray(new TextureRegion[row.size()])))
                .collect(Collectors.toList());
    }

    @Override
    public boolean keyDown(int keycode){
        super.keyDown(keycode);
        if (keycode == Input.Keys.RIGHT){
            stateTime = 0;
            currentAnimation++;
            if (currentAnimation == animations.size() ){
                currentAnimation = 0;
            }
            return true;
        }
        if (keycode == Input.Keys.LEFT){
            stateTime = 0;
            currentAnimation--;
            if (currentAnimation < 0){
                currentAnimation = animations.size()-1;
            }
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
    }

    @Override
    public void dispose(){
        super.dispose();
        wolfSheet.dispose();
    }

}
