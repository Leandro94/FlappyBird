package com.leandro.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jogo extends ApplicationAdapter
{
	private  int movimentoX =0;
	private  int movimentoY =0;
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;

	//atributos de configurações
    private float alturaDispositivo;
    private float larguraDispositivo;
    private float variacao=0;

	@Override
	public void create ()
	{
		/*
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");*/
		//Gdx.app.log("create","jogo iniciado");
		batch = new SpriteBatch();
		passaros =  new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		fundo = new Texture("fundo.png");

		larguraDispositivo =  Gdx.graphics.getWidth();
		alturaDispositivo =  Gdx.graphics.getHeight();
	}

	@Override
	public void render ()
	{/*
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();*/
		//movimentoX++;
		//Gdx.app.log("render","jogo redenrizado "+movimentoX);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(fundo,0,0,larguraDispositivo, alturaDispositivo);
		batch.draw(passaros[(int)variacao], 30,alturaDispositivo/2);
		batch.end();

		variacao+=Gdx.graphics.getDeltaTime()*10;
		//calcula a diferenca entre um render e outro
		//Gdx.app.log("XXX","variacao: "+Gdx.graphics.getDeltaTime());
		movimentoX++;
		movimentoY++;
		if(variacao>2)
		{
			variacao=0;
		}
		if(movimentoX >=larguraDispositivo)
		{
			movimentoX =0;
		}
		if(movimentoY>=alturaDispositivo)
		{
			movimentoY=0;
		}


	}
	
	@Override
	public void dispose ()
	{/*
		batch.dispose();
		img.dispose();*/
		//Gdx.app.log("dispose","Descarte de conteudos");
		batch.dispose();
		passaros[(int)variacao].dispose();
	}
}
