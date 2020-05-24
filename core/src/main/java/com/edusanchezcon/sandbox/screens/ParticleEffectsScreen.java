package com.edusanchezcon.sandbox.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edusanchezcon.sandbox.MyApp;

public class ParticleEffectsScreen extends BaseScreen {

    private static final float WORLD_WIDTH = 100;
    private static final float WORLD_HEIGHT = 100;
    private static final int MAX_EFFECTS = 5;
    private Random random = new Random();
    public static final String NAME = "Pruebas de partículas";

    private OrthographicCamera cam;
    private Viewport viewport;

    private ParticleEffectPool particlePool;
    private Array<PooledEffect> effects = new Array<>();

    private TextureAtlas atlas;
    private ParticleEffect particleEffect;
    // in https://github.com/raeleus/Particle-Park there are a lot of open source particle effects
    // in this example I'm using the default one provided by particle generator app (gdx-tools)

    public ParticleEffectsScreen(MyApp game){
        super(game, NAME);

        cam = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT * (MyApp.HEIGHT/MyApp.WIDTH), cam);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        atlas = new TextureAtlas("particles-packed/pack.atlas");
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("particles/blueFire.p"), atlas);

        // We must pool our particle effects instead creating them whenever needed
        particlePool = new ParticleEffectPool(particleEffect, 1, MAX_EFFECTS);
    }


    @Override
    public void render(float delta){
        super.render(delta);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        batch.begin();
        for (int i=effects.size-1; i >= 0; i--){
            PooledEffect effect = effects.get(i);
            effect.draw(batch, delta);
            if (effect.isComplete()){
                effect.free();
                effects.removeIndex(i);
            }
        }
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode){
        super.keyDown(keycode);
        if (keycode == Input.Keys.SPACE){
            if (effects.size < MAX_EFFECTS){
                effects.add( fireEffect());
            }
        }
        return false;
    }

    private PooledEffect fireEffect(){
        PooledEffect effect = particlePool.obtain();
        effect.scaleEffect(1f / 5f);
        effect.setPosition(random.nextFloat()*cam.viewportWidth, random.nextFloat()*(cam.viewportHeight/2));
        effect.start();
        return effect;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
    }

    @Override
    public void dispose(){
        super.dispose();
        atlas.dispose();
        for (PooledEffect effect : effects){
            effect.free();
        }
        effects.clear();
        particleEffect.dispose();
    }

}
