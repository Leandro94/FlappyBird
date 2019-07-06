package com.leandro.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class Jogo extends ApplicationAdapter
{
    //texturas
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture[] canoBaixo;
	private Texture[] canoTopo;
	private Texture gameOver;
	private Texture chao;

	//atributos de configurações
    private float alturaDispositivo;
    private float larguraDispositivo;
    private float variacao=0;
    private float variacaoFlutuacaoPassaro=0;
    private float gravidade=2;

    private float[] posicaoCanoHorizontal;
    private float[] posicaoCanoVertical;

    private float posicaoChaoHorizontal=0;

    private float espacoEntreCanos;
    private Random randomValorPrincipal, randomValorSubtrair;
    private int pontos=0;
    private int pontuacaoMaxima=0;
    private boolean passouCano =false;
    private int estadoJogo =0;
	private boolean toqueTela=false;
	private float tempoInclinarPassaro=0;
	private int contadorFlutuacaoPassaroSubir =0;
	private int contadorFlutuacaoPassaroDescer =0;

    private float posicaoPassaroHorizontal =0;
	private float posicaoPassaroVertical;

    //exibição de pontuação
	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	//Formas para colisao
	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;
	private Rectangle retanguloChao;

	//Sons
	private Sound somVoando;
	private Sound somColisao;
	private Sound somPontuacao;
	private Sound musicaFundo;

	//objeto para salvar pontuação
	Preferences preferencias;

	//objetos para câmera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 620;
	private final float VIRTUAL_HEIGHT = 1180;


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
		//Limpar frames anteriores
		//Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		animarPassaro();
		detectarColisoes();
	}
	//----------------------------------------------------------------------------------------------
	private void verificarEstadoJogo()
	{
		//APLICA EVENTO DE CLIQUE:
		toqueTela = Gdx.input.justTouched();

		//MOVIMENTA CHAO
		if(estadoJogo!=2)
		{
			posicaoChaoHorizontal-=Gdx.graphics.getDeltaTime()*200;
			if(posicaoChaoHorizontal<-chao.getWidth()/2)
			{
				posicaoChaoHorizontal=0;
			}
		}


		if(estadoJogo ==0)
		{
			espacoEntreCanos=300;
			if(toqueTela)
			{
				gravidade=-15;
				estadoJogo=1;
				somVoando.play();
			}
			//MOVIMENTA CHAO
			posicaoChaoHorizontal-=Gdx.graphics.getDeltaTime()*200;
			if(posicaoChaoHorizontal<-chao.getWidth()/2)
			{
				posicaoChaoHorizontal=0;
			}
		}
		else if(estadoJogo==1)
		{
			if(toqueTela)
			{
				if(posicaoPassaroVertical<alturaDispositivo)
				{
					gravidade=-15;
				}
				if(posicaoPassaroVertical>alturaDispositivo)
				{
					posicaoPassaroVertical =alturaDispositivo-50;
				}
				somVoando.play();
			}
			//MOVIMENTA CHAO
			posicaoChaoHorizontal-=Gdx.graphics.getDeltaTime()*200;
			if(posicaoChaoHorizontal<-chao.getWidth()/2)
			{
				posicaoChaoHorizontal=0;
			}
			//MOVIMENTA CANOS
			posicaoCanoHorizontal[0] -= Gdx.graphics.getDeltaTime()*200;
			posicaoCanoHorizontal[1] -= Gdx.graphics.getDeltaTime()*200;
			posicaoCanoHorizontal[2] -= Gdx.graphics.getDeltaTime()*200;

			if (posicaoCanoHorizontal[0] <-canoTopo[0].getWidth())
			{
				posicaoCanoHorizontal[0] = posicaoCanoHorizontal[2]+400;
				posicaoCanoVertical[0] = randomValorPrincipal.nextInt(500) - randomValorSubtrair.nextInt(200);//0 a 400
				passouCano = false;
			}else if (posicaoCanoHorizontal[1] <-canoTopo[1].getWidth()) {
				posicaoCanoHorizontal[1] = posicaoCanoHorizontal[0] + 400;
				posicaoCanoVertical[1] = randomValorPrincipal.nextInt(500) -randomValorSubtrair.nextInt(200);//0 a 400
				passouCano = false;
			} else if (posicaoCanoHorizontal[2] <-canoTopo[2].getWidth()) {
				posicaoCanoHorizontal[2] = posicaoCanoHorizontal[1] + 400;
				posicaoCanoVertical[2] = randomValorPrincipal.nextInt(500) -randomValorSubtrair.nextInt(200);//0 a 400
				passouCano = false;
			}
			//APLICA GRAVIDADE NO PASSARO:
			if(posicaoPassaroVertical >0 || toqueTela)
			{
				posicaoPassaroVertical = posicaoPassaroVertical - gravidade;
			}

			gravidade++;

		}
		else if(estadoJogo==2)
		{
			espacoEntreCanos=300;
			// FAZ O PASSARO CAIR APÓS BATER. PRIMEIRA FORMA DE FAZER:
			//APLICA GRAVIDADE NO PASSARO:
			if(posicaoPassaroVertical >chao.getHeight() || toqueTela)
			{
				posicaoPassaroVertical = posicaoPassaroVertical - gravidade;
			}
			if(posicaoPassaroVertical <chao.getHeight())
            {
                posicaoPassaroVertical = chao.getHeight()+1;
            }
			gravidade++;
			Gdx.app.log("XXX","gravidade: "+Gdx.graphics.getDeltaTime());

			if(pontos>pontuacaoMaxima)
			{
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaximo",pontuacaoMaxima);
			}
			//faz o passaro sair para o lado quando morre
			//posicaoPassaroHorizontal -= Gdx.graphics.getDeltaTime()*500;

			if(toqueTela)
			{
				estadoJogo=0;
				pontos=0;
				gravidade=2;
				posicaoPassaroHorizontal =0;
				posicaoPassaroVertical = alturaDispositivo/2;
				posicaoCanoHorizontal[0] = larguraDispositivo+30;
				posicaoCanoHorizontal[1] = posicaoCanoHorizontal[0]+400;
				posicaoCanoHorizontal[2] = posicaoCanoHorizontal[1]+400;
			}

		}
	}
	//----------------------------------------------------------------------------------------------
	private void detectarColisoes()
	{
		boolean colidiuCanoTopo =false;
		boolean colidiuCanoBaixo =false;
		boolean colidiuChao = false;
		retanguloChao.set(posicaoChaoHorizontal,0,chao.getWidth(),chao.getHeight());
		circuloPassaro.set(50+ posicaoPassaroHorizontal +passaros[0].getWidth()/2, posicaoPassaroVertical +passaros[0].getHeight()/2,passaros[0].getWidth()/2);
		for(int i=0; i<canoTopo.length;i++)
		{
			retanguloCanoTopo.set(posicaoCanoHorizontal[i],alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical[i],canoTopo[i].getWidth(),canoTopo[i].getHeight());
			colidiuCanoTopo =Intersector.overlaps(circuloPassaro,retanguloCanoTopo);
			for(int j=0; j<canoBaixo.length;j++)
			{
				retanguloCanoBaixo.set(posicaoCanoHorizontal[j],alturaDispositivo/2-canoBaixo[j].getHeight()-espacoEntreCanos/2+ posicaoCanoVertical[j],canoBaixo[j].getWidth(),canoBaixo[j].getHeight());
				colidiuCanoBaixo =Intersector.overlaps(circuloPassaro,retanguloCanoBaixo);
				if(colidiuCanoBaixo||colidiuCanoTopo)
				{
					if(estadoJogo==1)
					{
						estadoJogo=2;
						somColisao.play();
					}
				}
			}

		}
		colidiuChao = Intersector.overlaps(circuloPassaro,retanguloChao);
		if(colidiuChao)
		{
			if(estadoJogo==1)
			{
				estadoJogo=2;
				somColisao.play();
			}
		}
		//retanguloCanoTopo.set(posicaoCanoHorizontal,alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical,canoTopo.getWidth(),canoTopo.getHeight());
		//retanguloCanoBaixo.set(posicaoCanoHorizontal,alturaDispositivo/2-canoBaixo.getHeight()-espacoEntreCanos/2+ posicaoCanoVertical,canoBaixo.getWidth(),canoBaixo.getHeight());


		//CODIGO SO PARA TESTAR DESENHOS DE FORMAS SOBRE OS OBJETOS
		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);//informa que o desenho sera preenchido
		//passaro
		shapeRenderer.circle(50+passaros[0].getWidth()/2,posicaoPassaroVertical+passaros[0].getHeight()/2,passaros[0].getWidth()/2);
		//cano topo
		shapeRenderer.rect(posicaoCanoHorizontal,alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical,canoTopo.getWidth(),canoTopo.getHeight());
		//cano baixo
		shapeRenderer.rect(posicaoCanoHorizontal,alturaDispositivo/2-canoBaixo.getHeight()-espacoEntreCanos/2+ posicaoCanoVertical,canoBaixo.getWidth(),canoBaixo.getHeight());
		shapeRenderer.end();*/

	}
	//-----------------------------------------------------------------------------------------------
	private void desenharTexturas()
	{
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//desenha fundo
		batch.draw(fundo,0,0,larguraDispositivo, alturaDispositivo);
		//desenha passaro
		batch.draw(passaros[(int)variacao], 50+ posicaoPassaroHorizontal, posicaoPassaroVertical);
		//desenha canos baixo
		batch.draw(canoBaixo[0], posicaoCanoHorizontal[0],alturaDispositivo/2-canoBaixo[0].getHeight()-espacoEntreCanos/2+ posicaoCanoVertical[0]);
		batch.draw(canoBaixo[1], posicaoCanoHorizontal[1],alturaDispositivo/2-canoBaixo[1].getHeight()-espacoEntreCanos/2+ posicaoCanoVertical[1]);
		batch.draw(canoBaixo[2], posicaoCanoHorizontal[2],alturaDispositivo/2-canoBaixo[2].getHeight()-espacoEntreCanos/2+ posicaoCanoVertical[2]);
		//desenha canos topo
		batch.draw(canoTopo[0], posicaoCanoHorizontal[0],alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical[0]);
		batch.draw(canoTopo[1], posicaoCanoHorizontal[1],alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical[1]);
		batch.draw(canoTopo[2], posicaoCanoHorizontal[2],alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical[2]);
		//desenha chao
		batch.draw(chao,posicaoChaoHorizontal,0);
		//desenha pontuacao
		if(estadoJogo!=0)
		{
			textoPontuacao.draw(batch,String.valueOf(pontos),larguraDispositivo/2, alturaDispositivo-105);
		}
		if(estadoJogo==2)
		{
			batch.draw(gameOver,larguraDispositivo/2 -gameOver.getWidth()/2, alturaDispositivo/2+50);
			textoReiniciar.draw(batch,"Retry?",larguraDispositivo/2-140, alturaDispositivo/2-gameOver.getHeight()/2+50);
			textoMelhorPontuacao.draw(batch,"Best score: "+pontuacaoMaxima+" pontos",larguraDispositivo/2-140, alturaDispositivo/2-gameOver.getHeight()+50);
		}
		batch.end();
	}
	//----------------------------------------------------------------------------------------------
	public void validarPontos()
	{
		for(int i=0; i<canoTopo.length;i++)
		{
			if(posicaoCanoHorizontal[i] <50-passaros[0].getWidth())//passou da posicao do passaro
			{
				if(!passouCano)
				{
					pontos++;
					passouCano=true;
					if(espacoEntreCanos>100)
					{
						espacoEntreCanos-=5;
					}

					somPontuacao.play();
				}
			}
		}

	}
	//--------------------------------------------------------------------------------------------
	private void animarPassaro()
	{
		if(estadoJogo!=2)
		{
			//VELOCIDADE QUE IRÁ ATUALIZAR A ANIMAÇÃO DO PASSARO
			variacao+=Gdx.graphics.getDeltaTime()*10;
			variacaoFlutuacaoPassaro+=Gdx.graphics.getDeltaTime();

			//calcula a diferenca entre um render e outro
			//Gdx.app.log("XXX","variacao: "+Gdx.graphics.getDeltaTime());
			if(variacao>2)
			{
				if(estadoJogo==1 && !toqueTela)
				{
					if(variacao>5)
					{
						variacao+=Gdx.graphics.getDeltaTime()*60;

					}
					if(variacao>13)
					{
						variacao=14;
					}

					//Gdx.app.log("XXX","variacao: "+variacao);
				}
				else
				{
					variacao=0;
					if(estadoJogo==0&&variacaoFlutuacaoPassaro>=0.8)
					{
						contadorFlutuacaoPassaroDescer++;
						posicaoPassaroVertical-=1.5;

						if(contadorFlutuacaoPassaroDescer==contadorFlutuacaoPassaroSubir)
						{
							contadorFlutuacaoPassaroSubir =0;
							variacaoFlutuacaoPassaro=0;
							contadorFlutuacaoPassaroDescer=0;
						}
					}
				}
			}
			if(variacaoFlutuacaoPassaro>=0&&variacaoFlutuacaoPassaro<0.8)
			{
				if(estadoJogo==0 && !toqueTela)
				{
					contadorFlutuacaoPassaroSubir++;
					posicaoPassaroVertical+=1.5;
				}
			}
		}
		else
		{
			variacao=2;
		}
	}
	//----------------------------------------------------------------------------------------------
	private void inicializarTexturas()
	{
		passaros =  new Texture[15];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		passaros[3] = new Texture("passaro2.png");
		passaros[4] = new Texture("passaro2.png");
		passaros[5] = new Texture("passaro2.png");
		passaros[6] = new Texture("passaro4.png");
		passaros[7] = new Texture("passaro5.png");
		passaros[8] = new Texture("passaro6.png");
		passaros[9] = new Texture("passaro7.png");
		passaros[10] = new Texture("passaro8.png");
		passaros[11] = new Texture("passaro9.png");
		passaros[12] = new Texture("passaro10.png");
		passaros[13] = new Texture("passaro11.png");
		passaros[14] = new Texture("passaro12.png");

		canoBaixo = new Texture[3];
		canoBaixo[0] = new Texture("cano_baixo_maior.png");
		canoBaixo[1] = new Texture("cano_baixo_maior.png");
		canoBaixo[2] = new Texture("cano_baixo_maior.png");

		canoTopo =  new Texture[3];
		canoTopo[0] = new Texture("cano_topo_maior.png");
		canoTopo[1] = new Texture("cano_topo_maior.png");
		canoTopo[2] = new Texture("cano_topo_maior.png");

		chao =  new Texture("chao.png");

		fundo = new Texture("fundo.png");

		gameOver = new Texture("gameover.png");
	}
	//------------------------------------------------------------------------------------------------
	private void incializarObjetos()
	{
		batch = new SpriteBatch();
		//larguraDispositivo =  Gdx.graphics.getWidth();
		larguraDispositivo =  VIRTUAL_WIDTH;
		//alturaDispositivo =  Gdx.graphics.getHeight();
		alturaDispositivo =  VIRTUAL_HEIGHT;
		posicaoPassaroVertical = alturaDispositivo/2;

		randomValorPrincipal = new Random();
		randomValorSubtrair =  new Random();

		posicaoCanoHorizontal = new float[3];
		posicaoCanoHorizontal[0] =  larguraDispositivo+30;
		posicaoCanoHorizontal[1] =  posicaoCanoHorizontal[0]+400;
		posicaoCanoHorizontal[2] =  posicaoCanoHorizontal[1]+400;

		posicaoCanoVertical =  new float[3];
		posicaoCanoVertical[0] =  randomValorPrincipal.nextInt(500) - randomValorSubtrair.nextInt(200);//0 a 400
		posicaoCanoVertical[1] =  randomValorPrincipal.nextInt(500) - randomValorSubtrair.nextInt(200);//0 a 400
		posicaoCanoVertical[2] =  randomValorPrincipal.nextInt(500) - randomValorSubtrair.nextInt(200);//0 a 400

		espacoEntreCanos = 400;

		//Configurações de textos
		textoPontuacao = new BitmapFont(Gdx.files.internal("font.fnt"));
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(2);

		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);
		textoMelhorPontuacao.getData().setScale(2);

		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		//Formas Gemometricas para colisoes
		circuloPassaro =  new Circle();
		retanguloCanoBaixo =  new Rectangle();
		retanguloCanoTopo =  new Rectangle();
		shapeRenderer = new ShapeRenderer();
		retanguloChao =  new Rectangle();

		//Inicializa sons
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		//musicaFundo = Gdx.audio.newSound(Gdx.files.internal("dj_next_hit_leta.mp3"));

		//Configuracao preferencias
		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima",0);

		//Configuração da câmera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);

		//Stretch estica
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT,camera);
		//preenches
		//viewport = new FillViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT,camera);
		//corta as laterais para ajustar
		//viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT,camera);
	}
	//----------------------------------------------------------------------------------------------
	@Override
	public void resize(int width, int height)
	{
		viewport.update(width,height);
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
		batch.dispose();
	}
}
