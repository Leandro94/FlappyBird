package com.leandro.flappybird;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.leandro.flappybird.screens.GameScreen;
import com.leandro.flappybird.screens.LoadingScreen;
import com.leandro.flappybird.screens.MenuScreen;
import com.leandro.flappybird.screens.PlayScreen;
import com.leandro.flappybird.screens.SplashScreen;

public class Aplicacao extends Game
{
    public static final float VIRTUAL_WIDHT = 620;
    public static final float VIRTUAL_HEIGHT = 1180;

    public OrthographicCamera orthographicCamera;
    public SpriteBatch batch;
    public AssetManager assetManager;

    //Sons
    public Sound somVoando;
    public Sound somColisao;
    public Sound somPontuacao;
    public Music musicaFundo;

    public LoadingScreen loadingScreen;
    public SplashScreen splashScreen;
    public MenuScreen menuScreen;
    public PlayScreen playScreen;
    public GameScreen gameScreen;

    public BitmapFont font24;
    public BitmapFont font45;
    public BitmapFont fontPontos;
    @Override
    public void create()
    {
        assetManager =  new AssetManager();
        orthographicCamera = new OrthographicCamera();
        orthographicCamera.setToOrtho(false, VIRTUAL_WIDHT, VIRTUAL_HEIGHT);
        batch = new SpriteBatch();
        //font = new BitmapFont();
        //font.setColor(Color.BLACK);

        iniciarFontes();
        iniciarSons();

        loadingScreen =  new LoadingScreen(this);
        splashScreen = new SplashScreen(this);
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        playScreen = new PlayScreen(this);
        //this.setScreen(new LoadingScreen(this));
        this.setScreen(loadingScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font24.dispose();
        assetManager.dispose();

        loadingScreen.dispose();
        menuScreen.dispose();
        splashScreen.dispose();
        playScreen.dispose();
        gameScreen.dispose();

    }

    private void iniciarFontes()
    {
        FreeTypeFontGenerator freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/press_start.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size=24;
        parameter.color = Color.BLACK;
        font24 = freeTypeFontGenerator.generateFont(parameter);

        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/press_start.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size=45;
        parameter.color = Color.WHITE;
        font45 = freeTypeFontGenerator.generateFont(parameter);

        fontPontos = new BitmapFont(Gdx.files.internal("font.fnt"));
        fontPontos.setColor(Color.WHITE);
        fontPontos.getData().setScale(2);

    }
    private void iniciarSons()
    {
        somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
        somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("intro.wav"));
    }

}
