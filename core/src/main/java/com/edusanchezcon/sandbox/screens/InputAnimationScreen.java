package com.edusanchezcon.sandbox.screens;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class InputAnimationScreen extends BaseScreen {

    enum State {IDLE, WALKING, RUNNING, JUMPING, HURT}

    private static final float WORLD_WIDTH = 100;
    private static final float WORLD_HEIGHT = 100;
    private static final float BLOCK_SIZE = 20;
    private static final float GROUND_HEIGHT = 10;
    public static final String NAME = "Pruebas de Control de Animaciones";

    private OrthographicCamera cam;
    private Viewport viewport;
    private TextureAtlas atlas;
    private Map<State, Animation<TextureRegion>> animations;

    private float stateTime = 0f;
    private State state = State.IDLE;

    private boolean leftDir = false;
    private boolean runFlag = false;
    private boolean walkFlag = false;
    private boolean jumpFlag = false;
    private boolean hurtFlag = false;
    private float hurtCoolDown = 1f;

    private float yPos = GROUND_HEIGHT;
    private float velocity = 0f;

    private static final float MAX_JUMP_HEIGHT = 60f;
    private static final float JUMP_TIME = 1f; // in seconds

    // given a max_jump_height and a jump_time, these are our constants
    private static final float GRAVITY = -(8*MAX_JUMP_HEIGHT/((JUMP_TIME * JUMP_TIME)));
    private static final float INITIAL_VELOCITY = (float) Math.sqrt( 2 * MAX_JUMP_HEIGHT * (-GRAVITY));

    private boolean prevVelPositive = false;
    private boolean currVelPositive = false;


    public InputAnimationScreen(MyApp game){
        super(game, NAME);

        Gdx.app.log("GRAVITY", ""+GRAVITY);
        Gdx.app.log("INITIAL_IMPULSE", ""+INITIAL_VELOCITY);

        cam = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, cam);
        // ExtendViewport MUST invoke update to get worldWith & worldHeight. Otherwise they will be null
        viewport.update((int)MyApp.WIDTH, (int)MyApp.HEIGHT, true);

        atlas = new TextureAtlas("packed/cats.atlas");
        animations = loadCatAnimations(atlas);
    }


    @Override
    public void render(float delta){
        super.render(delta);

        stateTime += delta;

        manageState(state, delta);
        updateMovement(delta);

        Animation<TextureRegion> animation = animations.get(state);
        TextureRegion frame = animation.getKeyFrame(stateTime);
        if (!frame.isFlipX() && leftDir){
            frame.flip(true, false);
        }
        if (!leftDir && frame.isFlipX()){
            frame.flip(true, false);
        }

        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(frame, cam.viewportWidth/2 - BLOCK_SIZE/2, yPos, BLOCK_SIZE, BLOCK_SIZE);
        batch.end();
    }

    // in a real game I would use Box2D library to apply physics. This is just for fun
    private void updateMovement(float delta){
        // ----- this was a simple approach, reached through try and error ----
        //  velocity -= 5*delta;
        //  yPos += velocity;
        //  yPos -= 20*delta;
        // ------------------------------------

        prevVelPositive = velocity > 0;

        velocity += GRAVITY * (delta);
        yPos = yPos + 0.5f * GRAVITY * (delta) * (delta) + velocity * (delta);

        if (yPos < GROUND_HEIGHT)  yPos = GROUND_HEIGHT;

        currVelPositive = velocity >= 0;
        if ( prevVelPositive && !currVelPositive){
            Gdx.app.log("Peak", ""+yPos);
        }
    }

    private void manageState(State previousState, float delta){
        /* Priority:
         * 1. HURT
         * 2. JUMP
         * 3. others
         */
        hurtCoolDown -= delta;
        if (hurtFlag){
            stateTime = 0;
            hurtFlag = false;
            hurtCoolDown = 1f;
            state = State.HURT;
        }else if (hurtCoolDown < 0 && yPos == GROUND_HEIGHT){
            if (jumpFlag){
                velocity = INITIAL_VELOCITY;
                jumpFlag = false;
                state = State.JUMPING;
            } else if (walkFlag){
                state = runFlag ? State.RUNNING : State.WALKING;
            } else{
                state = State.IDLE;
            }
        }
        if (state != previousState){
            stateTime = 0;
        }
    }

    private Map<State, Animation<TextureRegion>> loadCatAnimations(TextureAtlas atlas){
        Map<State, Animation<TextureRegion>> map = new EnumMap<>(State.class);
        map.put(State.IDLE, new Animation<>(0.1f, atlas.findRegions("Idle"), Animation.PlayMode.LOOP));
        map.put(State.WALKING, new Animation<>(0.1f, atlas.findRegions("Walk"), Animation.PlayMode.LOOP));
        map.put(State.RUNNING, new Animation<>(0.1f, atlas.findRegions("Run"), Animation.PlayMode.LOOP));
        map.put(State.JUMPING, new Animation<>(0.1f, atlas.findRegions("Jump")));
        map.put(State.HURT, new Animation<>(0.1f, atlas.findRegions("Hurt")));
        return map;
    }

    @Override
    public boolean keyDown(int keycode){
        super.keyDown(keycode);
        if (keycode == Input.Keys.RIGHT){
            walkFlag = true;
            leftDir = false;
        }
        if (keycode == Input.Keys.LEFT){
            walkFlag = true;
            leftDir = true;
        }
        if (keycode == Input.Keys.SHIFT_RIGHT){
            runFlag = true;
        }
        if (keycode == Input.Keys.SPACE){
            jumpFlag = true;
        }
        if (keycode == Input.Keys.X){
            hurtFlag = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode){
        super.keyUp(keycode);
        if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.LEFT){
            walkFlag = false;
        }
        if (keycode == Input.Keys.SHIFT_RIGHT){
            runFlag = false;
        }
        if (keycode == Input.Keys.SPACE){
            jumpFlag = false;
        }
        return false;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void dispose(){
        super.dispose();
        atlas.dispose();
    }
}
