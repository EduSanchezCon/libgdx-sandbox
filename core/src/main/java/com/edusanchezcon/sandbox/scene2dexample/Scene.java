package com.edusanchezcon.sandbox.scene2dexample;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Scene{

    public static final float WORLD_WIDTH = 20;
    public static final float WORLD_HEIGHT = 20;

    private TiledMap tiledMap;
    private CharacterActor player;
    private Stage stage;
    private OrthographicCamera cam;
    private float unitScale;
    private ShapeRenderer shape;
    private TiledMapRenderer mapRenderer;
    private int[] backgroundLayers = {0};
    private int[] foregroundLayers = {1, 2};
    private List<Shape2D> collisionShapes;
    private boolean drawCollisionShapes;

    public Scene(Stage stage, float unitScale, ShapeRenderer shape){
        this.stage = stage;
        this.cam = (OrthographicCamera) stage.getCamera();
        this.unitScale = unitScale;
        this.shape = shape;
        initMap();
        initCharacter();
    }

    private void initMap(){
        // This map has been created with Tiled -- https://www.mapeditor.org/
        // you can edit city.tmx from Tiled!
        tiledMap = new TmxMapLoader().load("city.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);

        collisionShapes = loadCollisionShapes();
    }

    public List<Shape2D> loadCollisionShapes(){
        List<Shape2D> shapes = new ArrayList<>();
        MapObjects mapObjects = tiledMap.getLayers().get("Collisions").getObjects();
        for (MapObject obj : mapObjects){
            if (obj instanceof RectangleMapObject){
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                shapes.add(
                        new Rectangle(rect.x * unitScale,
                                rect.y * unitScale,
                                rect.width*unitScale,
                                rect.height*unitScale));
            }
            if (obj instanceof EllipseMapObject){
                Ellipse ell = ((EllipseMapObject) obj).getEllipse();
                shapes.add(
                        new Ellipse((ell.x + ell.width/2)* unitScale,
                                (ell.y + ell.height/2) * unitScale,
                                ell.width*unitScale,
                                ell.height*unitScale));
            }
        }
        return shapes;
    }

    private void initCharacter(){
        player = new CharacterActor(this, 0, 0, shape);
        stage.addActor(player);
        stage.setKeyboardFocus(player);
    }

    public void update(){

        stage.act();

        mapRenderer.setView(cam);
        mapRenderer.render(backgroundLayers);

        // for debug purposes we draw also the collision shapes
        if (drawCollisionShapes){
            shape.setProjectionMatrix(stage.getCamera().combined);
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.VIOLET);
            for (Shape2D s : collisionShapes){
                if (s instanceof Ellipse){
                    Ellipse e = (Ellipse) s;
                    // x & y are center coords in Ellipse class but left-down coords in ShapeRenderer's ellipse() method
                    shape.ellipse(e.x - e.width / 2, e.y - e.height / 2, e.width, e.height);
                } else{
                    Rectangle rect = (Rectangle) s;
                    shape.rect(rect.x, rect.y, rect.width, rect.height);
                }
            }
            shape.end();
        }

        stage.draw();

        mapRenderer.render(foregroundLayers);

        cam.position.set(player.getX(), player.getY(), 0);
        cam.position.x = MathUtils.clamp(cam.position.x, stage.getViewport().getWorldWidth()/2, WORLD_WIDTH - stage.getViewport().getWorldWidth()/2);
        cam.position.y = MathUtils.clamp(cam.position.y, stage.getViewport().getWorldHeight()/2, WORLD_HEIGHT - stage.getViewport().getWorldHeight()/2);
        cam.update();
    }


    public Shape2D getCollisionObject(float x, float y, float width, float height){
        for (Shape2D shape : collisionShapes){
            if (collides(shape, x, y, width, height)){
                Gdx.app.log("Collision", "Collision detected");
                return shape;
            }
        }
        // Maybe Optional?
        return null;
    }

    private boolean collides(Shape2D collShape, float x, float y, float width, float height){
        return (collShape.contains(x, y)
                || collShape.contains(x, y+height)
                || collShape.contains(x+width, y)
                || collShape.contains(x+width, y+height));
    }

    public void dispose(){
        tiledMap.dispose();
        player.dispose();
    }

    public boolean isDrawCollisionShapes(){
        return drawCollisionShapes;
    }

    public void setDrawCollisionShapes(boolean drawCollisionShapes){
        this.drawCollisionShapes = drawCollisionShapes;
    }

}
