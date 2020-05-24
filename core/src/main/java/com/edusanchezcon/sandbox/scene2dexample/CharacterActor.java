package com.edusanchezcon.sandbox.scene2dexample;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class CharacterActor extends Actor {

    private Scene scene;
    private final ShapeRenderer shape;
    public static final float BLOCK_SIZE = 1;
    private Texture characterTexture;
    private Vector2 nextPos;
    private boolean lookAtRight = true;
    private float stateTime = 0;
    private Map<AnimationState, Animation<TextureRegion>> animations;

    private Orientation horizontal = Orientation.ZERO;
    private Orientation vertical = Orientation.ZERO;
    private AnimationState currentState = AnimationState.WALK_DOWN;

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

    public CharacterActor(Scene scene, float x, float y, ShapeRenderer shape){
        this.scene = scene;
        setPosition(x, y);
        setSize(BLOCK_SIZE, BLOCK_SIZE);
        this.shape = shape;
        this.nextPos = new Vector2(x,y);
        characterTexture = new Texture("Link_spritesheet.png");
        animations = loadAnimations(characterTexture);

        addListener();
    }



    private void addListener(){
        this.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int key){
                switch (key){
                    case Input.Keys.LEFT:
                        horizontal = Orientation.NEGATIVE;
                        setAnimationState(AnimationState.WALK_SIDE);
                        lookAtRight = false;
                        break;
                    case Input.Keys.RIGHT:
                        horizontal = Orientation.POSITIVE;
                        setAnimationState(AnimationState.WALK_SIDE);
                        lookAtRight = true;
                        break;
                    case Input.Keys.DOWN:
                        vertical = Orientation.NEGATIVE;
                        setAnimationState( AnimationState.WALK_DOWN);
                        break;
                    case Input.Keys.UP:
                        vertical = Orientation.POSITIVE;
                        setAnimationState(AnimationState.WALK_UP);
                        break;
                }
                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int key){
                switch (key){
                    case Input.Keys.LEFT:
                        horizontal = horizontal.stop(Orientation.NEGATIVE);
                        break;
                    case Input.Keys.RIGHT:
                        horizontal = horizontal.stop(Orientation.POSITIVE);
                        break;
                    case Input.Keys.DOWN:
                        vertical = vertical.stop(Orientation.NEGATIVE);
                        break;
                    case Input.Keys.UP:
                        vertical = vertical.stop(Orientation.POSITIVE);
                        break;
                }
                return false;
            }
        });
    }

    private void setAnimationState(AnimationState newState){
        currentState = newState;
        stateTime = 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        stateTime += Gdx.graphics.getDeltaTime();

        TextureRegion frame = animations.get(currentState).getKeyFrame(stateTime);
        if (currentState == AnimationState.WALK_SIDE){
            if (frame.isFlipX() && lookAtRight){
                frame.flip(true, false);
            }
            if (!lookAtRight && !frame.isFlipX()){
                frame.flip(true, false);
            }
        }
        batch.draw(frame, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        if (scene.isDrawCollisionShapes()){
            batch.flush();
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.rect(getX(), getY(), getWidth(), getHeight());
            shape.end();
        }
    }

    @Override
    public void act(float delta){
        super.act(delta);
        // I will only do stuff when there is any movement
        if (horizontal.factor != 0 || vertical.factor != 0){
            nextPos.set( getX(), getY());
            nextPos.x += horizontal.factor * velocity * delta;
            nextPos.y += vertical.factor * velocity * delta;

            nextPos.x = MathUtils.clamp(nextPos.x, 0f, scene.WORLD_WIDTH - BLOCK_SIZE);
            nextPos.y = MathUtils.clamp(nextPos.y, 0f, Scene.WORLD_HEIGHT - BLOCK_SIZE);

            if (scene.getCollisionObject(nextPos.x, nextPos.y, getWidth(), getHeight()) != null){
                return;
            }

            addAction(Actions.moveTo(nextPos.x, nextPos.y));
//            setPosition(nextPos.x, nextPos.y);
        }
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

        return animations;
    }

    public void dispose(){
        characterTexture.dispose();
    }

}
