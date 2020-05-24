package com.edusanchezcon.sandbox.screens;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class TextureAtlasScreen extends BaseScreen {

    private static final float WORLD_WIDTH = 100;
    private static final float WORLD_HEIGHT = 100;
    private static final float BLOCK_SIZE = 20;
    public static final String NAME = "Pruebas de Animaciones con TextureAtlas";

    private OrthographicCamera cam;
    private Viewport viewport;

    private TextureAtlas atlas;
    private List<Animation<TextureRegion>> animations;
    float stateTime = 0f;
    int currentAnimation = 0;

    public TextureAtlasScreen(MyApp game){
        super(game, NAME);

        cam = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT * (MyApp.HEIGHT/MyApp.WIDTH), cam);
        viewport.apply();
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        cam.update();

        atlas = new TextureAtlas("packed/cats.atlas");
        animations = loadCatAnimations(atlas);
    }


    @Override
    public void render(float delta){
        super.render(delta);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        stateTime += delta;
        Animation<TextureRegion> animation = animations.get(currentAnimation);
        TextureRegion frame = animation.getKeyFrame(stateTime);
        batch.begin();
        batch.draw(frame, cam.viewportWidth/2 - BLOCK_SIZE/2, 10, BLOCK_SIZE, BLOCK_SIZE);
        font.draw(batch, "" + currentAnimation, 10, 10);
        batch.end();
    }

    private List<Animation<TextureRegion>> loadCatAnimations(TextureAtlas atlas){
        return Arrays.asList(
                new Animation<>(0.1f, atlas.findRegions("Idle"), Animation.PlayMode.LOOP),
                new Animation<>(0.1f, atlas.findRegions("Walk"), Animation.PlayMode.LOOP),
                new Animation<>(0.1f, atlas.findRegions("Run"), Animation.PlayMode.LOOP),
                new Animation<>(0.1f, atlas.findRegions("Jump"), Animation.PlayMode.LOOP),
                new Animation<>(0.1f, atlas.findRegions("Slide"), Animation.PlayMode.LOOP),
                new Animation<>(0.1f, atlas.findRegions("Hurt")),
                new Animation<>(0.1f, atlas.findRegions("Fall")),
                new Animation<>(0.1f, atlas.findRegions("Dead")));
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
        atlas.dispose();
    }

}
