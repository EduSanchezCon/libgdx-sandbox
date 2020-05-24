package com.edusanchezcon.sandbox.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class MouseInputScreen extends BaseScreen {

    private static final float WORLD_WIDTH = 100;
    private static final float WORLD_HEIGHT = 100;
    private static final float BLOCK_SIZE = 10;
    public static final String NAME = "Pruebas de control de movimiento con el ratón";

    private OrthographicCamera cam;
    private Viewport viewport;
    private Texture img;
    private Sprite arrow;

    private float screenWidth;
    private float screenHeight;
    Vector3 pos;
    Vector3 screenCoords;
    Vector2 mov;
    float rotation;
    String movText = "";
    private Viewport hudViewport;
    private Camera hudCam;

    public MouseInputScreen(MyApp game){
        super(game, NAME);

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        screenCoords = new Vector3(0,0,0);
        cam = new OrthographicCamera();
        viewport = new FillViewport(WORLD_WIDTH, WORLD_HEIGHT * (screenHeight/screenWidth), cam);
        // with 'true' parameter there's no need to update camera position
        viewport.apply(true);

        pos = new Vector3(viewport.getWorldWidth()/2-BLOCK_SIZE/2, viewport.getWorldHeight()/2-BLOCK_SIZE/2, 0);
        mov = new Vector2(0,0);
        img = new Texture("arrow.png");
        arrow = new Sprite(img);
        arrow.setSize(BLOCK_SIZE, BLOCK_SIZE);
        arrow.setOrigin(arrow.getWidth()/2, arrow.getHeight()/2);

        hudCam = new OrthographicCamera();
        hudViewport = new ScreenViewport(hudCam);
        hudViewport.update((int)screenWidth, (int)screenHeight, true);
    }


    @Override
    public void render(float delta){
        super.render(delta);
        cam.update();

        // I've chosen input pooling over touchDragged method because movement control is smoother when working with deltas
        // Anyway, in a real game I'd use Box2D library to apply forces and impulses
        if (Gdx.input.isTouched()){
            mov.add(
                    Gdx.input.getDeltaX() * (viewport.getWorldWidth()/screenWidth),
                    -Gdx.input.getDeltaY() * (viewport.getWorldHeight()/screenHeight));
            rotation = mov.angle();
            movText = "( " + mov.x + " - " + mov.y + " )  º" + rotation;
        }

        batch.setProjectionMatrix(hudCam.combined);
        batch.begin();
        font.draw(batch, movText, 10, 20);
        batch.end();

        calculatePosition(delta);


        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        arrow.draw(batch);
        batch.end();
    }

    private void calculatePosition(float delta){
        pos.add(mov.x * 2*delta, mov.y * 2*delta, 0);
        pos.x = MathUtils.clamp(pos.x, 0, viewport.getWorldWidth()-BLOCK_SIZE);
        if (pos.x == 0 || pos.x ==  viewport.getWorldWidth()-BLOCK_SIZE){
            mov.x = 0;
        }
        pos.y = MathUtils.clamp(pos.y, 0, viewport.getWorldHeight()-BLOCK_SIZE);
        if (pos.y == 0 || pos.y ==  viewport.getWorldHeight()-BLOCK_SIZE){
            mov.y = 0;
        }
        arrow.setRotation(rotation);
        arrow.setPosition(pos.x, pos.y);

    }

    // You can uncomment this and comment the isTouched block in render() method, for a slightly different movement treatment
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer){
//        screenCoords.set(screenX, screenY, 0);
//        cam.unproject(screenCoords);
//        mov.set(screenCoords.x - pos.x, screenCoords.y - pos.y);
//        rotation = mov.angle();
//        movText = "( " + mov.x + " - " + mov.y + " )  º" + rotation;
        return true;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        screenWidth = width;
        screenHeight = height;
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
    }

    @Override
    public void dispose(){
        super.dispose();
        img.dispose();
    }

}
