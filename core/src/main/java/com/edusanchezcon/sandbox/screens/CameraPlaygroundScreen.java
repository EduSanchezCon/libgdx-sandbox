package com.edusanchezcon.sandbox.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.edusanchezcon.sandbox.MyApp;

public class CameraPlaygroundScreen extends BaseScreen {

    private static final float WORLD_WIDTH = 100;
    private static final float WORLD_HEIGHT = 100;
    public static final String NAME = "Pruebas de cámara";

    private Texture bgTexture;
    private OrthographicCamera cam;
    private float rotationSpeed;

    private enum Orientation{
        NEGATIVE(-1), ZERO(0), POSITIVE(1);
        int factor;
        Orientation(int f){ factor = f; }

        Orientation stop(Orientation previousOriention){
            return (this == previousOriention) ? ZERO : this;
        }
    }


    private Orientation nearFar = Orientation.ZERO;
    private Orientation leftRight = Orientation.ZERO;
    private Orientation upDown = Orientation.ZERO;
    private Orientation rotationDir = Orientation.ZERO;

    public CameraPlaygroundScreen(MyApp game){
        super(game, NAME);

        rotationSpeed = 0.5f;
        cam = new OrthographicCamera();
//        camera.setToOrtho(false, MyApp.WIDTH, MyApp.HEIGHT);
        cam.setToOrtho(false, 30, 30 * (MyApp.HEIGHT/MyApp.WIDTH));
        cam.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0);
        cam.update();

        bgTexture = new Texture("map.png");
    }

    @Override
    public void render(float delta){
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        super.render(delta);

        batch.begin();
        batch.draw(bgTexture, 0, 0, 100, 100);
        batch.end();

        cam.zoom += 0.1f * nearFar.factor;
        cam.translate(leftRight.factor * cam.zoom, 0, 0);
        cam.translate(0, upDown.factor * cam.zoom, 0);
        cam.rotate(rotationSpeed * rotationDir.factor, 0, 0, 1);

        // set min and max bounds to zoom value
        cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 100/ cam.viewportWidth);

        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
    }

    @Override
    public boolean keyDown(int key){
        super.keyUp(key);
        switch (key){
            case Input.Keys.A:
                nearFar = Orientation.POSITIVE;
                break;
            case Input.Keys.Q:
                nearFar = Orientation.NEGATIVE;
                break;
            case Input.Keys.LEFT:
                leftRight = Orientation.NEGATIVE;
                break;
            case Input.Keys.RIGHT:
                leftRight = Orientation.POSITIVE;
                break;
            case Input.Keys.DOWN:
                upDown = Orientation.NEGATIVE;
                break;
            case Input.Keys.UP:
                upDown = Orientation.POSITIVE;
                break;
            case Input.Keys.W:
                rotationDir = Orientation.NEGATIVE;
                break;
            case Input.Keys.E:
                rotationDir = Orientation.POSITIVE;
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int key){
        super.keyDown(key);
        switch (key){
            case Input.Keys.A:
                nearFar = nearFar.stop(Orientation.POSITIVE);
                break;
            case Input.Keys.Q:
                nearFar = nearFar.stop(Orientation.NEGATIVE);
                break;
            case Input.Keys.LEFT:
                leftRight = leftRight.stop(Orientation.NEGATIVE);
                break;
            case Input.Keys.RIGHT:
                leftRight = leftRight.stop(Orientation.POSITIVE);
                break;
            case Input.Keys.DOWN:
                upDown = upDown.stop(Orientation.NEGATIVE);
                break;
            case Input.Keys.UP:
                upDown = upDown.stop(Orientation.POSITIVE);
                break;
            case Input.Keys.W:
                rotationDir = rotationDir.stop(Orientation.NEGATIVE);
                break;
            case Input.Keys.E:
                rotationDir = rotationDir.stop(Orientation.POSITIVE);
                break;
        }
        return false;
    }



    @Override
    public boolean scrolled(int amount){
        cam.zoom += 0.02 * amount;
        return true;
    }

    @Override
    public void resize(int width, int height){
        cam.viewportWidth = 30f;                 // Viewport of 30 units!
        cam.viewportHeight = 30f * height/width; // Lets keep things in proportion.
//        cam.viewportWidth = width/100f;  //We will see width/32f units!
//        cam.viewportHeight = cam.viewportWidth * height/width;

        cam.update();
    }

    @Override
    public void dispose(){
        super.dispose();
        bgTexture.dispose();
    }

}
