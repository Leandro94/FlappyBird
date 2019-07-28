package com.leandro.flappybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.leandro.flappybird.Aplicacao;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.addAction;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;


public class MenuScreen implements Screen
{
    private final Aplicacao app;
    private Stage stage;
    private Skin skin;
    private ShapeRenderer shapeRenderer;
    private TextButton textButtonPlay, textButtonExit;

    public MenuScreen(Aplicacao app)
    {
        this.app = app;
        this.stage =  new Stage(new StretchViewport(Aplicacao.VIRTUAL_WIDHT, Aplicacao.VIRTUAL_HEIGHT,app.orthographicCamera));
        this.shapeRenderer = new ShapeRenderer();
    }

    private void update(float delta)
    {
        stage.act(delta);
    }

    @Override
    public void show() {
        System.out.println("MAIN MENU");
        Gdx.input.setInputProcessor(stage);
        stage.clear();

       // this.skin = new Skin(Gdx.files.internal(""));
        this.skin = new Skin();
        this.skin.addRegions(app.assetManager.get("ui/uiskin.atlas", TextureAtlas.class));
        this.skin.add("default-font",app.font24);
        this.skin.load(Gdx.files.internal("ui/uiskin.json"));
        stage.addAction(sequence(alpha(0), parallel(fadeIn(0.05f))));

        app.musicaFundo.setLooping(true);
        app.musicaFundo.setVolume(0.1f);
        app.musicaFundo.play();


        iniciarBotoes();
    }
    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update(delta);
        stage.draw();

        app.batch.begin();
        app.font24.draw(app.batch,"Screen: MENU",20,20);
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
    public void dispose()
    {
        stage.dispose();
        shapeRenderer.dispose();

    }

    private void iniciarBotoes()
    {
        final Runnable transicaoRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                app.setScreen(app.gameScreen);
                textButtonPlay.remove();
                textButtonExit.remove();
                app.musicaFundo.stop();
                app.musicaFundo.setLooping(false);

            }
        };

        textButtonPlay =  new TextButton("Play",skin,"default");
        textButtonPlay.setSize(270,120);
        textButtonPlay.setPosition(Aplicacao.VIRTUAL_WIDHT /2-textButtonPlay.getWidth()/2,Aplicacao.VIRTUAL_HEIGHT /2);
        textButtonPlay.addAction(sequence(alpha(0), parallel(fadeIn(.5f),moveBy(0,-20,.5f, Interpolation.pow5Out))));
        textButtonPlay.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                stage.addAction(sequence(alpha(1), parallel(fadeOut(0.8f)),run(transicaoRunnable)));

            }
        });
        //--------------------------------------------------------------------------------------------
        textButtonExit =  new TextButton("Exit",skin,"default");
        textButtonExit.setSize(270,120);
        textButtonExit.setPosition(Aplicacao.VIRTUAL_WIDHT /2-textButtonExit.getWidth()/2,Aplicacao.VIRTUAL_HEIGHT /2-textButtonPlay.getHeight()-10);
        textButtonExit.addAction(sequence(alpha(0), parallel(fadeIn(.5f),moveBy(0,-20,.5f, Interpolation.pow5Out))));
        textButtonExit.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Gdx.app.exit();
            }
        });
        //---------------------------------------------------------------------------------------------
        stage.addActor(textButtonPlay);
        stage.addActor(textButtonExit);
    }
}
