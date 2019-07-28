package com.leandro.flappybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.leandro.flappybird.Aplicacao;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;


public class SplashScreen implements Screen {

    private final Aplicacao app;
    private Stage stage;
    private Image splashImg;

    public SplashScreen(final Aplicacao app)
    {
        this.app = app;
        this.stage =  new Stage(new StretchViewport(Aplicacao.VIRTUAL_WIDHT, Aplicacao.VIRTUAL_HEIGHT,app.orthographicCamera));
       // stage = new Stage();

    }

    public void update(float delta)
    {
        stage.act(delta);
    }
    @Override
    public void show()
    {
        System.out.println("SPLASH");
        Gdx.input.setInputProcessor(stage);

        Runnable transicaoRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                //stage.addAction(sequence(alpha(1f),parallel(fadeOut(2f))));
                app.setScreen(app.menuScreen);

            }
        };

        // Texture splashTex = new Texture(Gdx.files.internal("passaro1.png"));
        Texture splashTex = app.assetManager.get("passaro2.png",Texture.class);
        splashImg = new Image(splashTex);
        splashImg.setOrigin(splashImg.getWidth()/2,splashImg.getImageHeight()/2);

        //splashImg.setPosition(stage.getWidth()/2-16,stage.getHeight()/2-16);
        splashImg.setPosition(stage.getWidth()/2,stage.getHeight());
        splashImg.addAction(sequence(alpha(0f),scaleTo(.1f,1f),parallel(fadeIn(2f, Interpolation.pow2),
                scaleTo(2f,2f,2.5f,Interpolation.pow5),moveTo(stage.getWidth()/2-32,stage.getHeight()/2-32,2f,
                        Interpolation.swing)),delay(0.5f),fadeOut(1.25f),run(transicaoRunnable)));

        //splashImg.addAction(sequence(alpha(0f),parallel(moveBy(30,-100,2f),fadeIn(3f))));
        //splashImg.addAction(sequence(alpha(0f),fadeIn(2f)));
       // splashImg.addAction(fadeIn(3f));
        stage.addActor(splashImg);
    }

    @Override
    public void render(float delta)
    {
        //Gdx.gl.glClearColor(.25f, .25f, .25f, 1f);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update(delta);
        stage.draw();

        app.batch.begin();
        app.font24.draw(app.batch,"Screen: SPLASH",20,20);
        app.batch.end();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,false);

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
}
