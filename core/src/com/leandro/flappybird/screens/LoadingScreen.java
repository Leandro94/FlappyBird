package com.leandro.flappybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.leandro.flappybird.Aplicacao;

import java.util.concurrent.BlockingDeque;

public class LoadingScreen implements Screen
{
    private final Aplicacao app;
    private ShapeRenderer shapeRenderer;
    private float progresso;

    public LoadingScreen(Aplicacao app) {
        this.app = app;
        this.shapeRenderer = new ShapeRenderer();

    }
    private void queueAssets()
    {
        app.assetManager.load("passaro2.png", Texture.class);
        app.assetManager.load("ui/uiskin.atlas", TextureAtlas.class);
    }

    @Override
    public void show() {

        System.out.println("LOADING");
        queueAssets();
        this.progresso=0f;
    }
    public void update(float delta)
    {
        progresso = MathUtils.lerp(progresso,app.assetManager.getProgress(),.1f);
        if(app.assetManager.update()&& progresso>=app.assetManager.getProgress()-.001f)
        {
            app.setScreen(app.splashScreen);
        }


    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update(delta);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(40,app.orthographicCamera.viewportHeight/2,app.orthographicCamera.viewportWidth-40, 30);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(45,app.orthographicCamera.viewportHeight/2+2.5f,progresso*(app.orthographicCamera.viewportWidth-45), 25);
        shapeRenderer.end();

        app.batch.begin();
        app.font24.draw(app.batch,"Screen: LOADING...",20,20);
        app.batch.end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        shapeRenderer.dispose();
    }

}
