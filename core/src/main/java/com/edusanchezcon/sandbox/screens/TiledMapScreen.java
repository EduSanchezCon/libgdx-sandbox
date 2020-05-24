package com.edusanchezcon.sandbox.screens;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class TiledMapScreen extends BaseScreen {

    private static final float WORLD_WIDTH = 20;
    private static final float WORLD_HEIGHT = 20;
    private static final float BLOCK_SIZE = 1;
    public static final String NAME = "Pruebas de Tiled Maps";

    private OrthographicCamera cam;
    private Viewport viewport;

    private TiledMap tiledMap;
    private float unitScale;
    private TiledMapRenderer mapRenderer;
    int[] backgroundLayers = {0};
    int[] foregroundLayers = {1, 2};
    List<Shape2D> collisionShapes;

    private Texture characterTexture;
    private Sprite character;
    private Vector2 position;
    private float[] bounds;
    private boolean lookAtRight = true;
    private float stateTime = 0;
    private Map<AnimationState, Animation<TextureRegion>> animations;
    private AnimationState currentState = AnimationState.WALK_DOWN;
    private Animation<TextureRegion> currentAnimation;

    // 5 blocks / second
    private float velocity = 5f;

    private enum Orientation{
        NEGATIVE(-1), ZERO(0), POSITIVE(1);
        int factor;
        Orientation(int f){ factor = f; }

        Orientation stop(Orientation previousOriention){
            return (this == previousOriention) ? ZERO : this;
        }
    }

    private enum AnimationState {
        IDLE_UP, IDLE_DOWN, IDLE_SIDE, WALK_UP, WALK_DOWN, WALK_SIDE
    }

    private Orientation leftRight = Orientation.ZERO;
    private Orientation upDown = Orientation.ZERO;

    public TiledMapScreen(MyApp game){
        super(game, NAME);

        cam = new OrthographicCamera();
        viewport = new ExtendViewport(10, 10, cam);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // This map has been created with Tiled -- https://www.mapeditor.org/
        // you can edit city.tmx from Tiled!
        tiledMap = new TmxMapLoader().load("city.tmx");
        // 1 World Unit = 32 pixels in map file
        unitScale = BLOCK_SIZE / 32f;
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
        collisionShapes = loadCollisionShapes();
        for (Shape2D s : collisionShapes){
            Gdx.app.log("SHAPE",
                    MessageFormat.format("{0}  {1}", s.getClass().getSimpleName(), s.toString()));
        }

        position = new Vector2(0,0);
        characterTexture = new Texture("Link_spritesheet.png");
        animations = loadAnimations(characterTexture);
        currentAnimation = animations.get(currentState);
        character = new Sprite(currentAnimation.getKeyFrames()[0]);
        character.setSize(BLOCK_SIZE, BLOCK_SIZE);
        bounds = new float[]{
                position.x-BLOCK_SIZE/2, position.y-BLOCK_SIZE/2,
                position.x-BLOCK_SIZE/2, position.y+BLOCK_SIZE/2,
                position.x+BLOCK_SIZE/2, position.y-BLOCK_SIZE/2,
                position.x+BLOCK_SIZE/2, position.y+BLOCK_SIZE/2
        };
    }


    @Override
    public void render(float delta){
        super.render(delta);
        updatePosition(delta);

        cam.position.set(position.x, position.y, 0);
        cam.position.x = MathUtils.clamp(cam.position.x, viewport.getWorldWidth()/2, WORLD_WIDTH - viewport.getWorldWidth()/2);
        cam.position.y = MathUtils.clamp(cam.position.y, viewport.getWorldHeight()/2, WORLD_HEIGHT - viewport.getWorldHeight()/2);
        cam.update();

        stateTime += delta;
        currentAnimation = animations.get(currentState);
        TextureRegion frame = currentAnimation.getKeyFrame(stateTime);
        if (currentState == AnimationState.WALK_SIDE){
            if (frame.isFlipX() && lookAtRight){
                frame.flip(true, false);
            }
            if (!lookAtRight && !frame.isFlipX()){
                frame.flip(true, false);
            }
        }
        character.setRegion(frame);

        mapRenderer.setView(cam);
        mapRenderer.render(backgroundLayers);

        // for debug purposes we draw also the collision shapes
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.VIOLET);
        for (Shape2D s : collisionShapes){
            if (s instanceof Ellipse){
                Ellipse e = (Ellipse) s;
                // x & y are center coords in Ellipse class but left-down coords in ShapeRenderer's ellipse() method
                shapeRenderer.ellipse(e.x-e.width/2, e.y-e.height/2, e.width, e.height);
            }else{
                Rectangle rect = (Rectangle) s;
                shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
            }
        }
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.polygon(bounds);
        shapeRenderer.end();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        character.draw(batch);
        batch.end();

        mapRenderer.render(foregroundLayers);
    }

    private void updatePosition(float delta){
        // I will only do stuff when there is any movement
        if (leftRight.factor != 0 || upDown.factor != 0){
            position.set( character.getX(), character.getY());
            position.x += leftRight.factor * velocity * delta;
            position.y += upDown.factor * velocity * delta;

            for (Shape2D shape : collisionShapes){
                if (collides(shape, position)){
                    Gdx.app.log("Collision", "Collision detected");
                    return;
                }
            }

            position.x = MathUtils.clamp(position.x, 0f, WORLD_WIDTH - BLOCK_SIZE);
            position.y = MathUtils.clamp(position.y, 0f, WORLD_HEIGHT - BLOCK_SIZE);

            bounds[0] = position.x;
            bounds[1] = position.y;

            bounds[2] = position.x;
            bounds[3] = position.y+BLOCK_SIZE;

            bounds[4] = position.x+BLOCK_SIZE;
            bounds[5] = position.y;

            bounds[6] = position.x+BLOCK_SIZE;
            bounds[7] = position.y+BLOCK_SIZE;

            character.setPosition(position.x, position.y);
        }
    }

    private boolean collides(Shape2D collShape, Vector2 characterPos){
        return (collShape.contains(characterPos.x, characterPos.y)
                || collShape.contains(characterPos.x, characterPos.y+BLOCK_SIZE)
                || collShape.contains(characterPos.x+BLOCK_SIZE, characterPos.y)
                || collShape.contains(characterPos.x+BLOCK_SIZE, characterPos.y+BLOCK_SIZE));
    }

    public List<Shape2D> loadCollisionShapes(){
        List<Shape2D> shapes = new ArrayList<>();
        MapObjects touchables = tiledMap.getLayers().get("Collisions").getObjects();
        for (MapObject obj : touchables){
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

    @Override
    public boolean keyDown(int key){
        super.keyUp(key);
        switch (key){
            case Input.Keys.LEFT:
                leftRight = Orientation.NEGATIVE;
                currentState = AnimationState.WALK_SIDE;
                lookAtRight = false;
                stateTime=0;
                break;
            case Input.Keys.RIGHT:
                leftRight = Orientation.POSITIVE;
                currentState = AnimationState.WALK_SIDE;
                lookAtRight = true;
                stateTime=0;
                break;
            case Input.Keys.DOWN:
                upDown = Orientation.NEGATIVE;
                currentState = AnimationState.WALK_DOWN;
                stateTime=0;
                break;
            case Input.Keys.UP:
                upDown = Orientation.POSITIVE;
                currentState = AnimationState.WALK_UP;
                stateTime=0;
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int key){
        super.keyDown(key);
        switch (key){
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
        }
        return false;
    }

    private Map<AnimationState, Animation<TextureRegion>> loadAnimations(Texture sheet){

        // Actually, only idle animations are being rendered.
        // It's not the goal of this screen to switch correctly between animations
        Map<AnimationState, Animation<TextureRegion>> animations = new EnumMap<>(AnimationState.class);
        animations.put(AnimationState.WALK_UP, new Animation<>(0.25f,
                new TextureRegion(sheet, 5, 2, 22, 32),
                new TextureRegion(sheet, 27, 2, 22, 32),
                new TextureRegion(sheet, 49, 2, 22, 32),
                new TextureRegion(sheet, 71, 2, 22, 32)
        ));
        animations.put(AnimationState.WALK_DOWN, new Animation<>(0.25f,
                new TextureRegion(sheet, 99, 2, 22, 33),
                new TextureRegion(sheet, 123, 2, 22, 33),
                new TextureRegion(sheet, 147, 2, 22, 33),
                new TextureRegion(sheet, 171, 2, 22, 33)
        ));
        animations.put(AnimationState.WALK_SIDE, new Animation<>(0.25f,
                new TextureRegion(sheet, 200, 2, 21, 32),
                new TextureRegion(sheet, 221, 2, 21, 32),
                new TextureRegion(sheet, 241, 2, 21, 32),
                new TextureRegion(sheet, 262, 2, 21, 32)
        ));

        animations.values().stream().forEach(a -> a.setPlayMode(Animation.PlayMode.LOOP));


//        TextureRegion idleDownRegion = new TextureRegion(sheet, 98, 2, 96, 32);
//        TextureRegion idleSideRegion = new TextureRegion(sheet, 199, 2, 84, 31);

        return animations;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
    }

    @Override
    public void dispose(){
        super.dispose();
        tiledMap.dispose();
        characterTexture.dispose();
    }

}
