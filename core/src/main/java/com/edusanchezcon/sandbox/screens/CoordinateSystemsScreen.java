package com.edusanchezcon.sandbox.screens;

import java.text.MessageFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class CoordinateSystemsScreen extends BaseScreen {

    public static final String NAME = "Sistemas de coordenadas y propagación de eventos";

    // Big Section. It works in pixels
    private OrthographicCamera cam;
    private Viewport viewport;
    String outerText = "";

    // Small Section. Normalized (0, 1) coords
    public static final float SMALL_WIDTH = 1;
    public static final float SMALL_HEIGHT = 1;
    private static final String SMALL_TITLE = SMALL_WIDTH + " x " + SMALL_HEIGHT;
    private Rectangle smallRect;
    private Camera smallCam;
    private Vector3 smallTextCoords;
    private Vector3 smallTitleCoords;
    private String smallText = "";

    // Medium Section. (0, 10) coords
    public static final float MED_WIDTH = 10;
    public static final float MED_HEIGHT = 10;
    private static final String MED_TITLE = MED_WIDTH + " x " + MED_HEIGHT;
    private Rectangle medRect;
    private Camera medCam;
    private Vector3 medTextCoords;
    private Vector3 medTitleCoords;
    private String medText = "";


    public CoordinateSystemsScreen(MyApp game){
        super(game, NAME);

        // we create the screen's coordinates system
        cam = new OrthographicCamera();
        viewport = new ScreenViewport(cam);
        viewport.apply();
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        // small section dimensions are 1/4 screen dimensions and is centered
        smallRect = new Rectangle(0, 0, SMALL_WIDTH, SMALL_HEIGHT);
        Viewport smallViewport = new StretchViewport(4* smallRect.width, 4* smallRect.height);
        smallViewport.apply();
        smallCam = smallViewport.getCamera();
        smallCam.position.set(smallRect.width/2, smallRect.height/2, 0);
        smallCam.update();
        shapeRenderer.setProjectionMatrix(smallCam.combined);
        smallTextCoords = smallCam.project(new Vector3(0,0,0));
        smallTitleCoords = smallCam.project(new Vector3(0, smallRect.height,0));

        // medium section dimensions are 1/2 screen dimensions and is centered
        medRect = new Rectangle(0, 0, MED_WIDTH, MED_HEIGHT);
        Viewport medViewport = new FitViewport(2* medRect.width, 2* medRect.height);
        medViewport.apply();
        medCam = medViewport.getCamera();
        medCam.position.set(medRect.width/2, medRect.height/2, 0);
        medCam.update();
        medTextCoords = medCam.project(new Vector3(0,0,0));
        medTitleCoords = medCam.project(new Vector3(0, medRect.height,0));

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new SmallSectionProcessor());
        multiplexer.addProcessor(new MediumSectionProcessor());
        multiplexer.addProcessor(new OuterSectionProcessor());
        multiplexer.addProcessor(this);

        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta){
        cam.update();
        super.render(delta);

        // draw medium section
        shapeRenderer.setProjectionMatrix(medCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(0,0, medRect.width, medRect.height);
        shapeRenderer.end();

        // draw small section
        shapeRenderer.setProjectionMatrix(smallCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(0,0, smallRect.width, smallRect.height);
        shapeRenderer.end();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        font.draw(batch, SMALL_TITLE, smallTitleCoords.x + 10, smallTitleCoords.y - 10);
        font.draw(batch, MED_TITLE, medTitleCoords.x + 10, medTitleCoords.y - 10);
        font.draw(batch, viewport.getWorldWidth() + " x " + viewport.getWorldHeight(),
                10, viewport.getWorldHeight() - 80);

        font.draw(batch, outerText, 10, 30);
        font.draw(batch, smallText, smallTextCoords.x + 10, smallTextCoords.y + 30);
        font.draw(batch, medText, medTextCoords.x + 10, medTextCoords.y + 30);
        batch.end();
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        cam.update();

        smallTextCoords = smallCam.project(new Vector3(0,0,0));
        smallTitleCoords = smallCam.project(new Vector3(0, smallRect.height,0));
        medTextCoords = medCam.project(new Vector3(0,0,0));
        medTitleCoords = medCam.project(new Vector3(0, medRect.height,0));
    }

    @Override
    public void dispose(){
        super.dispose();

    }

    private class SmallSectionProcessor extends InputAdapter{
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button){
            Vector3 boundedCords = smallCam.unproject(new Vector3(screenX, screenY, 0));
            if (smallRect.contains(boundedCords.x, boundedCords.y)){
                smallText = MessageFormat.format("({0}, {1})", boundedCords.x, boundedCords.y);
            }
            // always propagates the event to the next processor
            return false;
        }
    }

    private class MediumSectionProcessor extends InputAdapter{
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button){
            Vector3 boundedCords = medCam.unproject(new Vector3(screenX, screenY, 0));
            if (medRect.contains(boundedCords.x, boundedCords.y)){
                medText = MessageFormat.format("({0}, {1})", boundedCords.x, boundedCords.y);
                // if touched stop the event from being propagated further
                return true;
            }
            return false;
        }
    }

    private class OuterSectionProcessor extends InputAdapter{
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button){
            Vector3 boundedCords = cam.unproject(new Vector3(screenX, screenY, 0));
            outerText = MessageFormat.format("({0}, {1})", boundedCords.x, boundedCords.y);
            // The event always ends here
            return true;
        }
    }
}
