package com.edusanchezcon.sandbox.screens;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class Box2dScreen extends BaseScreen {


    private boolean isTouched;

    private static final float WORLD_WIDTH = 70;
    private static final float WORLD_HEIGHT = 70;
    private static final float BLOCK_SIZE = 1.7f;
    public static final String NAME = "Pruebas con Box2D";

    private OrthographicCamera cam;
    private Viewport viewport;
    private Vector3 camNextPos;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body body;
    private Body enemy;
    private enum BodyObject {PLAYER, ENEMY, BLOCK, FLOOR};

    private Vector2 touchPos = new Vector2(0,0);

    private Sprite playerSprite;
    private Sprite enemySprite;
    private Texture playerTexture;
    private Texture enemyTexture;
    private boolean drawSprites = false;

    private Texture killTexture;
    private Animation<TextureRegion> killAnimation;
    private float stateTime = 0f;
    private boolean flagKilled = false;
    private boolean enemySpriteChanged = false;

    public Box2dScreen(MyApp game){
        super(game, NAME);

        cam = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH/5, WORLD_HEIGHT/5, cam);
        viewport.update((int)MyApp.WIDTH, (int)MyApp.HEIGHT, true);
        camNextPos = cam.position.cpy();

        debugRenderer = new Box2DDebugRenderer(
                true, true, true, true, true, true);
        world = new World(new Vector2(0f, -9.8f),true);

        createBody();
        createFloor();
        createObstacles();
        createEnemy();

        loadTextures();

        world.setContactListener(new ContactListener(){
            @Override
            public void beginContact(Contact contact){ }

            @Override
            public void endContact(Contact contact){ }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold){ }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse){
                final Body enemy;
                if (contact.getFixtureA().getBody().getUserData() == BodyObject.ENEMY){
                    enemy = contact.getFixtureA().getBody();
                }else if (contact.getFixtureB().getBody().getUserData() == BodyObject.ENEMY){
                    enemy = contact.getFixtureB().getBody();
                }else{
                    return;
                }

                float[] impulses =  impulse.getNormalImpulses();
                double totalImpact = impulses[0] + impulses[1];
                // we want to register only significant impacts, Avoiding small frictions against the floor
                if (totalImpact > 5){
                    Gdx.app.log("CONTACT", contact.getFixtureA().getBody().getUserData() + " - "
                            + contact.getFixtureB().getBody().getUserData() + " force: " + totalImpact);
                    if (!flagKilled){
                        flagKilled = true;
                        enemySprite.setPosition(enemy.getPosition().x, enemy.getPosition().y);
                        Gdx.app.postRunnable(()-> world.destroyBody(enemy));
                    }
                }
            }
        });

    }

    private void loadTextures(){
        playerTexture = new Texture("box2dPlayer.png");
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(BLOCK_SIZE, BLOCK_SIZE);
        playerSprite.setOriginCenter();
        enemyTexture = new Texture("box2dEnemy.png");
        enemySprite = new Sprite(enemyTexture);
        enemySprite.setSize(BLOCK_SIZE, BLOCK_SIZE);
        enemySprite.setOriginCenter();

        killTexture = new Texture("boom.png");
        killAnimation = loadAnimation(killTexture);
    }

    private void createBody(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(BLOCK_SIZE*3, BLOCK_SIZE*3);
        bodyDef.angularDamping = 1f;
        // in this specific case we want the body to stay in the air until we apply a force on it
        bodyDef.awake = false;
        body = world.createBody(bodyDef);
        // userData can be any Object we want to "attach" to the box2dBody
        body.setUserData(BodyObject.PLAYER);

        CircleShape shape = new CircleShape();
        shape.setRadius(BLOCK_SIZE/2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
        // shape information is already copied into the body, so we can (and must) get rid of it
        shape.dispose();
    }

    private void createFloor(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(WORLD_WIDTH/2, 0);
        Body floor = world.createBody(bodyDef);
        floor.setUserData(BodyObject.FLOOR);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(WORLD_WIDTH/2, 2f);

        // I don't need density in a static body
        floor.createFixture(shape, 0f);

        shape.dispose();
    }

    private void createObstacles(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.1f, 1.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 3f;
        fixtureDef.friction = 0.5f;
        fixtureDef.shape = shape;

        for (int i = 0; i < 5; i++){
            bodyDef.position.set(20 + i, 3);
            Body obstacle = world.createBody(bodyDef);
            obstacle.setUserData(BodyObject.BLOCK);
            obstacle.createFixture(fixtureDef);
        }

        shape.dispose();
    }

    private void createEnemy(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(25, 2 + BLOCK_SIZE/2);
        bodyDef.angularDamping = 1f;
        enemy = world.createBody(bodyDef);
        enemy.setUserData(BodyObject.ENEMY);

        CircleShape shape = new CircleShape();
        shape.setRadius(BLOCK_SIZE/2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.shape = shape;

        enemy.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void render(float delta){
        super.render(delta);

        world.step(1/60f, 6, 2);

        updateCamPosition();
        updateSprites(delta);
        debugRenderer.render(world, cam.combined);

        if (drawSprites){
            batch.setProjectionMatrix(cam.combined);
            batch.begin();
            playerSprite.draw(batch);
            enemySprite.draw(batch);
            batch.end();
        }
    }


    private void updateSprites(float delta){
        playerSprite.setPosition(body.getPosition().x - BLOCK_SIZE/2, body.getPosition().y - BLOCK_SIZE/2);
        playerSprite.setRotation((float)Math.toDegrees(body.getAngle()));
        enemySprite.setPosition(enemy.getPosition().x - BLOCK_SIZE / 2, enemy.getPosition().y - BLOCK_SIZE / 2);
        if (!flagKilled){
            enemySprite.setRotation(MathUtils.radiansToDegrees * enemy.getAngle());
        }else{
            if (!enemySpriteChanged){
                enemySpriteChanged = true;
                enemySprite.setRotation(0f);
                enemySprite.setOrigin(0, 0);
                enemySprite.setScale(2f);
            }
            stateTime += delta;
            enemySprite.setRegion(killAnimation.getKeyFrame(stateTime));
        }
    }

    private void updateCamPosition(){
        camNextPos.x = body.getPosition().x;

        // set min and max bounds to zoom value
        cam.zoom = MathUtils.clamp(cam.zoom, 0.5f, 50/ cam.viewportWidth);

        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        camNextPos.x = MathUtils.clamp(camNextPos.x, effectiveViewportWidth/2, WORLD_WIDTH -effectiveViewportWidth/2);
        camNextPos.y = effectiveViewportHeight/2;
        cam.position.set(camNextPos);
        cam.update();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button){
        if (!isTouched){
            Vector3 vector3 = cam.unproject(new Vector3(screenX, screenY, 0f));
            isTouched = true;
            touchPos.set(vector3.x, vector3.y);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        if (isTouched){
            Vector3 vector3 = cam.unproject(new Vector3(screenX, screenY, 0f));
            isTouched = false;
            Gdx.app.log("init-pos", touchPos.toString());
            Gdx.app.log("end-pos", vector3.toString());
            touchPos.sub(vector3.x, vector3.y);
            Gdx.app.log("pre-limit", touchPos.toString());
            touchPos.limit(10);
            body.applyLinearImpulse(touchPos.scl(4f, 4f), body.getPosition(), true);
//            body.setLinearVelocity(touchPos.scl(2, 2));
            Gdx.app.log("post-limit", touchPos.toString());
        }
        return true;
    }

    @Override
    public boolean keyDown(int keycode){
        super.keyDown(keycode);
        if (keycode == Input.Keys.SHIFT_RIGHT){
            drawSprites = !drawSprites;
        }
        if (keycode == Input.Keys.SPACE){
            body.applyLinearImpulse(new Vector2(0, 50f), body.getWorldCenter(), true) ;
        }
        return false;
    }

    private Animation<TextureRegion> loadAnimation(Texture sheet){
        TextureRegion textureRegion = new TextureRegion(sheet, 0, 0, sheet.getWidth(), sheet.getHeight());
        TextureRegion[][] splitedFrames = textureRegion.split(textureRegion.getRegionWidth() / 8, textureRegion.getRegionHeight() / 8);

        TextureRegion[] tempRegionList = Arrays.stream(splitedFrames)
                .flatMap(row -> Arrays.stream(row)).toArray(TextureRegion[]::new);

        return new Animation<>(1/32f, tempRegionList);
    }

    @Override
    public boolean scrolled(int amount){
        cam.zoom += 0.02 * amount;
        return true;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void dispose(){
        playerTexture.dispose();
        enemyTexture.dispose();
        killTexture.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
