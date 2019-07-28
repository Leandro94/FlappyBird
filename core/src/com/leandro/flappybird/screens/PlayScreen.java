package com.leandro.flappybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.leandro.flappybird.Aplicacao;
import com.leandro.flappybird.actors.SlideButton;

public class PlayScreen implements Screen
{
    private final Aplicacao app;

    private Stage stage;
    private Skin skin;

    //game grid
    private int boardSize = 4;
    private int holeX,holeY;
    private SlideButton[][] buttonGrid;

    //nav buttons
    private TextButton buttonBack;

    public PlayScreen(Aplicacao app) {
        this.app = app;
        this.stage =  new Stage(new StretchViewport(Aplicacao.VIRTUAL_WIDHT, Aplicacao.VIRTUAL_HEIGHT,app.orthographicCamera));
    }

    @Override
    public void show() {
        System.out.println("PLAY");
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        this.skin = new Skin();
        this.skin.addRegions(app.assetManager.get("ui/uiskin.atlas", TextureAtlas.class));
        this.skin.add("default-font",app.font24);
        this.skin.load(Gdx.files.internal("ui/uiskin.json"));

        inicializarBotoesNavegacao();
        inicializarGrid();

    }
    private void update(float delta)
    {
        stage.act(delta);

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        update(delta);
        stage.draw();

        app.batch.begin();
        app.font24.draw(app.batch,"Screen: PLAY",20,20);
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
        stage.dispose();

    }

    private void inicializarBotoesNavegacao()
    {
        buttonBack = new TextButton("Back", skin,"default");
        buttonBack.setPosition(20,app.orthographicCamera.viewportHeight-60);
        buttonBack.setSize(100,50);
        buttonBack.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                app.setScreen(app.menuScreen);
            }
        });
        stage.addActor(buttonBack);


    }
    private void inicializarGrid()
    {

    }
}
