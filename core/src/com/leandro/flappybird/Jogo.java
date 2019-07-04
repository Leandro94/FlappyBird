package com.leandro.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Jogo extends ApplicationAdapter
{
    //texturas
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;

	//atributos de configurações
    private float alturaDispositivo;
    private float larguraDispositivo;
    private float variacao=0;
    private float gravidade=2;
    private float posicaoInicialPassaroVertical;
    private float posicaoCanoHorizontal;
    private float posicaoCanoVertical;
    private float espacoEntreCanos;
    private Random random;
    private int pontos=0;
    private boolean passouCano =false;
    private int estadoJogo =0;

    //exibição de pontuação
	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	//Formas para colisao
	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;

	//----------------------------------------------------------------------------------------------
	@Override
	public void create ()
	{
		inicializarTexturas();
		incializarObjetos();
	}
	//----------------------------------------------------------------------------------------------
	@Override
	public void render ()
	{
		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();

	}
	//----------------------------------------------------------------------------------------------
	private void verificarEstadoJogo()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//APLICA EVENTO DE CLIQUE:
		boolean toqueTela = Gdx.input.justTouched();

		if(estadoJogo ==0)
		{
			if(toqueTela)
			{
				gravidade=-15;
				estadoJogo=1;
			}
		}
		else if(estadoJogo==1)
		{
			if(toqueTela)
			{
				gravidade=-15;
			}
			//MOVIMENTA CANOS
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime()*200;
			if(posicaoCanoHorizontal <-canoTopo.getWidth())//menor que a largura negativa, ou seja abaixo de 0, ele volta o cano no inicio
			{
				posicaoCanoHorizontal =larguraDispositivo;
				posicaoCanoVertical =  random.nextInt(400)-200;//0 a 400
				passouCano=false;
			}
			//APLICA GRAVIDADE NO PASSARO:
			if(posicaoInicialPassaroVertical >0 || toqueTela)
			{
				posicaoInicialPassaroVertical = posicaoInicialPassaroVertical - gravidade;
			}

			gravidade++;

		}
		else if(estadoJogo==2)
		{

		}
	}
	//----------------------------------------------------------------------------------------------
	private void detectarColisoes()
	{
		circuloPassaro.set(50+passaros[0].getWidth()/2,posicaoInicialPassaroVertical+passaros[0].getHeight()/2,passaros[0].getWidth()/2);
		retanguloCanoTopo.set(posicaoCanoHorizontal,alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical,canoTopo.getWidth(),canoTopo.getHeight());
		retanguloCanoBaixo.set(posicaoCanoHorizontal,alturaDispositivo/2-canoBaixo.getHeight()-espacoEntreCanos/2+ posicaoCanoVertical,canoBaixo.getWidth(),canoBaixo.getHeight());
		boolean colidiuCanoTopo =Intersector.overlaps(circuloPassaro,retanguloCanoTopo);
		boolean colidiuCanoBaixo =Intersector.overlaps(circuloPassaro,retanguloCanoBaixo);
		if(colidiuCanoBaixo||colidiuCanoTopo)
		{
			estadoJogo=2;

		}
		//CODIGO SO PARA TESTAR DESENHOS DE FORMAS SOBRE OS OBJETOS
		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);//informa que o desenho sera preenchido
		//passaro
		shapeRenderer.circle(50+passaros[0].getWidth()/2,posicaoInicialPassaroVertical+passaros[0].getHeight()/2,passaros[0].getWidth()/2);
		//cano topo
		shapeRenderer.rect(posicaoCanoHorizontal,alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical,canoTopo.getWidth(),canoTopo.getHeight());
		//cano baixo
		shapeRenderer.rect(posicaoCanoHorizontal,alturaDispositivo/2-canoBaixo.getHeight()-espacoEntreCanos/2+ posicaoCanoVertical,canoBaixo.getWidth(),canoBaixo.getHeight());
		shapeRenderer.end();*/

	}
	//-----------------------------------------------------------------------------------------------
	private void desenharTexturas()
	{
		batch.begin();
		batch.draw(fundo,0,0,larguraDispositivo, alturaDispositivo);
		batch.draw(passaros[(int)variacao], 50, posicaoInicialPassaroVertical);
		batch.draw(canoBaixo, posicaoCanoHorizontal,alturaDispositivo/2-canoBaixo.getHeight()-espacoEntreCanos/2+ posicaoCanoVertical);
		batch.draw(canoTopo, posicaoCanoHorizontal,alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical);
		textoPontuacao.draw(batch,String.valueOf(pontos),larguraDispositivo/2, alturaDispositivo-110);

		if(estadoJogo==2)
		{
			batch.draw(gameOver,larguraDispositivo/2 -gameOver.getWidth()/2, alturaDispositivo/2);
			textoReiniciar.draw(batch,"Toque para reiniciar!",larguraDispositivo/2-140, alturaDispositivo/2-gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch,"Seu record é: 0 pontos",larguraDispositivo/2-140, alturaDispositivo/2-gameOver.getHeight());
		}
		batch.end();
	}
	//----------------------------------------------------------------------------------------------
	public void validarPontos()
	{
		if(posicaoCanoHorizontal <50-passaros[0].getWidth())//passou da posicao do passaro
		{
			if(!passouCano)
			{
				pontos++;
				passouCano=true;
			}
		}
		//VELOCIDADE QUE IRÁ ATUALIZAR A ANIMAÇÃO DO PASSARO
		variacao+=Gdx.graphics.getDeltaTime()*10;

		//calcula a diferenca entre um render e outro
		//Gdx.app.log("XXX","variacao: "+Gdx.graphics.getDeltaTime());
		if(variacao>3)
		{
			variacao=0;
		}
	}
	//----------------------------------------------------------------------------------------------
	private void inicializarTexturas()
	{
		passaros =  new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");

		fundo = new Texture("fundo.png");

		gameOver = new Texture("game_over.png");
	}
	//------------------------------------------------------------------------------------------------
	private void incializarObjetos()
	{
		batch = new SpriteBatch();
		larguraDispositivo =  Gdx.graphics.getWidth();
		alturaDispositivo =  Gdx.graphics.getHeight();
		posicaoInicialPassaroVertical = alturaDispositivo/2;
		posicaoCanoHorizontal =  larguraDispositivo;
		espacoEntreCanos = 400;
		random = new Random();

		//Configurações de textos
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(8);

		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);
		textoMelhorPontuacao.getData().setScale(2);

		//Formas Gemometricas para colisoes
		circuloPassaro =  new Circle();
		retanguloCanoBaixo =  new Rectangle();
		retanguloCanoTopo =  new Rectangle();
		shapeRenderer = new ShapeRenderer();
	}
	//------------------------------------------------------------------------------------------------
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
