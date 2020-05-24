package com.edusanchezcon.sandbox.screens;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.edusanchezcon.sandbox.MyApp;

public class Scene2dUIMenuScreen extends BaseScreen {

    public static final String NAME = "Prueba con Scene2d UI";
    private List<Function<MyApp, Screen>> screenGenerator = new LinkedList<>();
    private List<String> screenTitles;

    private Skin skin;
    private Skin skin2;
    private Skin activeSkin;
    private Stage stage;
    private Table table;

    private Vector2 velocity = new Vector2(0,0);

    public Scene2dUIMenuScreen(MyApp game){
        super(game, "Menú");

        screenGenerator.add(CameraPlaygroundScreen::new);
        screenGenerator.add(ViewportScreen::new);
        screenGenerator.add(CoordinateSystemsScreen::new);
        screenGenerator.add(TexturePlayGroundScreen::new);
        screenGenerator.add(AnimationsScreen::new);
        screenGenerator.add(TextureAtlasScreen::new);
        screenGenerator.add(InputAnimationScreen::new);
        screenGenerator.add(MouseInputScreen::new);
        screenGenerator.add(ParticleEffectsScreen::new);
        screenGenerator.add(TiledMapScreen::new);
        screenGenerator.add(Scene2dScreen::new);
        screenGenerator.add(Scene2dUIMenuScreen::new);
        screenGenerator.add(Box2dScreen::new);

        screenTitles = Arrays.asList(
                CameraPlaygroundScreen.NAME,
                ViewportScreen.NAME,
                CoordinateSystemsScreen.NAME,
                TexturePlayGroundScreen.NAME,
                AnimationsScreen.NAME,
                TextureAtlasScreen.NAME,
                InputAnimationScreen.NAME,
                MouseInputScreen.NAME,
                ParticleEffectsScreen.NAME,
                TiledMapScreen.NAME,
                Scene2dScreen.NAME,
                Scene2dUIMenuScreen.NAME,
                Box2dScreen.NAME);
    }

    @Override
    public void show(){
        stage = new Stage(new ScreenViewport(), batch);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));

        skin = new Skin(new TextureAtlas("ui/default/uiskin.atlas"));
        skin.load(Gdx.files.internal("ui/default/uiskin.json"));

        skin2 = new Skin(new TextureAtlas("ui/quantum-horizon/quantum-horizon-ui.atlas"));
        skin2.load(Gdx.files.internal("ui/quantum-horizon/quantum-horizon-ui.json"));

        activeSkin = skin;

        initMenu(activeSkin);
    }

    private void initMenu(Skin skin){

        stage.clear();
        table = new Table();
        stage.addActor(table);
        table.center();
        table.setFillParent(true);

        Touchpad touchpad = new Touchpad(5f, skin);
        touchpad.setSize(70, 70);
        touchpad.setPosition(10, 10);
        touchpad.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                Touchpad target = (Touchpad) actor;
                velocity.set(target.getKnobPercentX(), target.getKnobPercentY());
            }
        });

        stage.addActor(touchpad);

        for (int i = 0; i < screenTitles.size(); i++){
            table.row();
            TextButton button = new TextButton(screenTitles.get(i), skin);
//            button.getLabel().setFontScale(1/2f);
            final Function<MyApp, Screen> screenFunction = screenGenerator.get(i);
            button.addListener(new ChangeListener(){
                @Override
                public void changed(ChangeEvent event, Actor actor){
                    game.setScreen(screenFunction.apply(game));
                }
            });
            table.add(button).fillX().height(40f);
        }
        activeSkin = skin;
    }

    @Override
    public void render(float delta){

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());

        table.moveBy(velocity.x, velocity.y);

        stage.draw();
    }

    @Override
    public boolean keyDown(int key){
        super.keyDown(key);
        if (Input.Keys.SPACE == key){
            initMenu(activeSkin==skin ? skin2 : skin);
        }
        return false;
    }

    @Override
    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose(){
        super.dispose();
        stage.dispose();
        skin.dispose();
    }
}
