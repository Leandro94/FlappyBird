package com.leandro.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class Jogo extends ApplicationAdapter
{
    //texturas
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;

	//atributos de configurações
    private float alturaDispositivo;
    private float larguraDispositivo;
    private float variacao=0;
    private float gravidade=2;
    private float posicaoInicialPassaroY;
    private float posicaoCanoX;
    private float posicaoCanoY;
    private float espacoEntreCanos;
    private Random random;

	@Override
	public void create ()
	{
		inicializarTexturas();
		incializarObjetos();
	}

	@Override
	public void render ()
	{
		verificarEstadoJogo();
		desenharTexturas();

	}
	private void verificarEstadoJogo()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//MOVIMENTA CANOS
		posicaoCanoX-= Gdx.graphics.getDeltaTime()*200;
		if(posicaoCanoX<-canoTopo.getWidth())//menor que a largura negativa, ou seja abaixo de 0
		{
			posicaoCanoX=larguraDispositivo;
			posicaoCanoY =  random.nextInt(400)-200;//0 a 400
		}

		//APLICA EVENTO DE CLIQUE:
		boolean toqueTela = Gdx.input.justTouched();
		if(toqueTela)
		{
			gravidade=-20;
		}
		//APLICA GRAVIDADE NO PASSARO:
		if(posicaoInicialPassaroY>0 || toqueTela)
		{
			posicaoInicialPassaroY = posicaoInicialPassaroY - gravidade;
		}
		//VELOCIDADE QUE IRÁ ATUALIZAR A ANIMAÇÃO DO PASSARO
		variacao+=Gdx.graphics.getDeltaTime()*10;


		//calcula a diferenca entre um render e outro
		//Gdx.app.log("XXX","variacao: "+Gdx.graphics.getDeltaTime());
		if(variacao>3)
		{
			variacao=0;
		}
		gravidade++;
	}
	private void desenharTexturas()
	{
		batch.begin();
		batch.draw(fundo,0,0,larguraDispositivo, alturaDispositivo);
		batch.draw(passaros[(int)variacao], 30, posicaoInicialPassaroY);
		batch.draw(canoBaixo,posicaoCanoX,alturaDispositivo/2-canoBaixo.getHeight()-espacoEntreCanos/2+posicaoCanoY);
		batch.draw(canoTopo,posicaoCanoX,alturaDispositivo/2+espacoEntreCanos/2+posicaoCanoY);
		batch.end();
	}
	private void inicializarTexturas()
	{
		passaros =  new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");

		fundo = new Texture("fundo.png");
	}
	private void incializarObjetos()
	{
		batch = new SpriteBatch();
		larguraDispositivo =  Gdx.graphics.getWidth();
		alturaDispositivo =  Gdx.graphics.getHeight();
		posicaoInicialPassaroY = alturaDispositivo/2;
		posicaoCanoX =  larguraDispositivo;
		espacoEntreCanos = 400;
		random = new Random();
	}
	
	@Override
	public void dispose ()
	{/*
		batch.dispose();
		img.dispose();*/
		//Gdx.app.log("dispose","Descarte de conteudos");
		//batch.dispose();
		//passaros[(int)variacao].dispose();
	}
}
