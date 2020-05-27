package com.edusanchezcon.sandbox.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class TexturePlayGroundScreen extends BaseScreen {

    private static final float WORLD_WIDTH = 100;
    private static final float WORLD_HEIGHT = 100;
    private static final float BLOCK_SIZE = 10;
    public static final String NAME = "Pruebas de texturas";

    private OrthographicCamera cam;
    private Viewport viewport;

    private Texture background;
    private Texture generated;

    Color[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, new Color(0,0,0,0.5f),
            Color.YELLOW, Color.BROWN, Color.GOLD, Color.VIOLET, Color.ORANGE};

    public TexturePlayGroundScreen(MyApp game){
        super(game, NAME);

        cam = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, cam);
//        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT * (MyApp.HEIGHT/MyApp.WIDTH), cam);
        viewport.apply();
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        cam.update();

        background = new Texture("tileable10.png");
        generated = generateTexture();
    }

    private Texture generateTexture(){
        // Everything in this method is just for learning about Pixmaps.
        // It's always better to import a texture or draw a shape.
        Pixmap pixmap = new Pixmap(512,512, Pixmap.Format.RGBA8888);

        //Fill it red
        pixmap.setColor(Color.RED);
        pixmap.fill();

        //Draw a circle about the middle
        pixmap.setColor(Color.YELLOW);
        pixmap.fillCircle(pixmap.getWidth()/2, pixmap.getHeight()/2, pixmap.getHeight()/2 - 1);

        //Draw two lines forming an X
        pixmap.setColor(Color.BLACK);
        pixmap.drawLine(0, 0, pixmap.getWidth()-1, pixmap.getHeight()-1);
        pixmap.drawLine(1, 0, pixmap.getWidth()-1, pixmap.getHeight()-2);
        pixmap.drawLine(0, 1, pixmap.getWidth()-2, pixmap.getHeight()-1);
        pixmap.drawLine(0, pixmap.getHeight()-1, pixmap.getWidth()-1, 0);
        pixmap.drawLine(1, pixmap.getHeight()-1, pixmap.getWidth()-1, 1);
        pixmap.drawLine(0, pixmap.getHeight()-2, pixmap.getWidth()-2, 0);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void render(float delta){
        super.render(delta);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        batch.begin();
        for (int y = 0; y < (WORLD_HEIGHT/BLOCK_SIZE); y++){
            batch.setColor(new Color(1,1,1, 1-(y*0.1f)));
            for (int x = 0; x < (WORLD_WIDTH/BLOCK_SIZE); x++){
                batch.draw(background, x*BLOCK_SIZE, y*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE );
            }
        }
        batch.setColor(Color.WHITE);
        batch.draw(generated, WORLD_WIDTH/2 - BLOCK_SIZE, WORLD_HEIGHT/2 - BLOCK_SIZE,
                2*BLOCK_SIZE, 2*BLOCK_SIZE);
        batch.end();
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
    }

    @Override
    public void dispose(){
        super.dispose();
        background.dispose();
        generated.dispose();
    }

}
