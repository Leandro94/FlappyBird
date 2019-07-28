package com.leandro.flappybird.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.leandro.flappybird.Aplicacao;

import org.w3c.dom.css.Rect;

import java.awt.Paint;
import java.util.Random;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class GameScreen implements Screen
{
    private Stage stage;

    //texturas
	private Texture[] passaros;
	private Texture fundo;
	private Image imgFundo;
	private Texture[] canoBaixo;
	private Texture[] canoTopo;
	private Texture gameOver;
	private Texture retry;
	private Image imgBotaoRetry;
	private Texture ready;
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
	private int contadorFlutuacaoPassaroSubir =0;
	private int contadorFlutuacaoPassaroDescer =0;
	private boolean colidiu=false;

    private float posicaoPassaroHorizontal =0;
	private float posicaoPassaroVertical;

	//Formas para colisao
	private Circle circuloPassaro;
	private Rectangle retanguloCanoTopo;
	private Rectangle retanguloCanoBaixo;
	private Rectangle retanguloChao;

	//Sons
	private Sound somVoando;
	private Sound somColisao;
	private Sound somPontuacao;

	//objeto para salvar pontuação
	Preferences preferencias;

	//objetos para câmera
	private OrthographicCamera camera;
	private Viewport viewport;

	private Aplicacao app;


	public GameScreen(Aplicacao app)
	{
		this.app = app;
		this.stage =  new Stage(new StretchViewport(Aplicacao.VIRTUAL_WIDHT, Aplicacao.VIRTUAL_HEIGHT,app.orthographicCamera));
		//this.shapeRenderer = new ShapeRenderer();
	}

	private void update(float delta)
	{
		stage.act(delta);

	}
	@Override
	public void show()
	{
		inicializarTexturas();
		incializarObjetos();
		stage.clear();
        stage.addAction(sequence(alpha(0), parallel(fadeIn(0.8f))));
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		animarPassaro();
		detectarColisoes();

		update(delta);
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
			if(toqueTela)
			{
                espacoEntreCanos=400;
				gravidade=-13;
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
			//app.musicaFundo.stop();
			if(toqueTela)
			{
				if(posicaoPassaroVertical<alturaDispositivo)
				{
					gravidade=-13;
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
			gravidade+=0.9f;
		}
		else if(estadoJogo==2)
		{
			//app.musicaFundo.play();
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
			gravidade+=0.9f;
			Gdx.app.log("XXX","gravidade: "+Gdx.graphics.getDeltaTime());

			if(pontos>pontuacaoMaxima)
			{
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaximo",pontuacaoMaxima);
			}
			if(colidiu==true)
			{
				//faz o passaro sair para o lado quando morre
				posicaoPassaroHorizontal -= Gdx.graphics.getDeltaTime()*500;
			}

			if(toqueTela)
			{
                //if((Gdx.input.getX()>= imgBotaoRetry.getOriginX() && Gdx.input.getX()<= imgBotaoRetry.getWidth()+ imgBotaoRetry.getWidth()/2) &&
				//		(Gdx.input.getY()>=alturaDispositivo/2+gameOver.getHeight()+10- imgBotaoRetry.getHeight() && Gdx.input.getY()<=alturaDispositivo/2+gameOver.getHeight()+10+ imgBotaoRetry.getHeight()/ imgBotaoRetry.getOriginY()))
				//{
					/*System.out.println("xxx if X "+Gdx.input.getX());
					System.out.println("  xxx if Y "+Gdx.input.getY());
					System.out.println("xxx bounds wi "+imgBotaoRetry.getWidth());
					System.out.println("xxx bounds hei "+imgBotaoRetry.getHeight());
					System.out.println("xxx origin hei "+imgBotaoRetry.getOriginY());
					System.out.println("xxx origin wi "+imgBotaoRetry.getOriginX());
					System.out.println("xxx y "+imgBotaoRetry.getY());*/
					colidiu=false;
					estadoJogo=0;
					pontos=0;
					gravidade=2;
					posicaoPassaroHorizontal =0;
					posicaoPassaroVertical = alturaDispositivo/2+20;
					posicaoCanoHorizontal[0] = larguraDispositivo+50;
					posicaoCanoHorizontal[1] = posicaoCanoHorizontal[0]+400;
					posicaoCanoHorizontal[2] = posicaoCanoHorizontal[1]+400;
					//app.musicaFundo.stop();

					stage.addAction(sequence(alpha(1f),parallel(fadeOut(0.05f)),parallel(fadeIn(0.05f))));
					imgBotaoRetry.remove();
				//}
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
		circuloPassaro.set(120+ posicaoPassaroHorizontal +passaros[0].getWidth()/2, posicaoPassaroVertical +passaros[0].getHeight()/2,passaros[0].getWidth()/2);
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
						colidiu=true;
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
        imgFundo.setPosition(0,0);
        imgFundo.setSize(larguraDispositivo,alturaDispositivo);
        stage.addActor(imgFundo);
        stage.draw();
		app.batch.setProjectionMatrix(camera.combined);
		app.batch.begin();
		//desenha fundo
		//batch.draw(fundo,0,0,larguraDispositivo, alturaDispositivo);

		//desenha passaro
     	app.batch.draw(passaros[(int)variacao], 120+ posicaoPassaroHorizontal, posicaoPassaroVertical);

		//desenha canos baixo
		app.batch.draw(canoBaixo[0], posicaoCanoHorizontal[0],alturaDispositivo/2-canoBaixo[0].getHeight()-espacoEntreCanos/2+ posicaoCanoVertical[0]);
		app.batch.draw(canoBaixo[1], posicaoCanoHorizontal[1],alturaDispositivo/2-canoBaixo[1].getHeight()-espacoEntreCanos/2+ posicaoCanoVertical[1]);
		app.batch.draw(canoBaixo[2], posicaoCanoHorizontal[2],alturaDispositivo/2-canoBaixo[2].getHeight()-espacoEntreCanos/2+ posicaoCanoVertical[2]);

		//desenha canos topo
		app.batch.draw(canoTopo[0], posicaoCanoHorizontal[0],alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical[0]);
		app.batch.draw(canoTopo[1], posicaoCanoHorizontal[1],alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical[1]);
		app.batch.draw(canoTopo[2], posicaoCanoHorizontal[2],alturaDispositivo/2+espacoEntreCanos/2+ posicaoCanoVertical[2]);

		//desenha chao
		app.batch.draw(chao,posicaoChaoHorizontal,0);

		if(estadoJogo!=0)
		{

			app.fontPontos.draw(app.batch,String.valueOf(pontos),larguraDispositivo/2,alturaDispositivo-105);
		}
		else if(estadoJogo==0)
		{
			app.batch.draw(ready,larguraDispositivo/2 -ready.getWidth()/2, alturaDispositivo/2+200);
		}
		if(estadoJogo==2)
		{
		    String bestScore = "BEST:"+pontuacaoMaxima;
			app.batch.draw(gameOver,larguraDispositivo/2 -gameOver.getWidth()/2, alturaDispositivo/2+100);

            GlyphLayout glyphLayout = new GlyphLayout();
            glyphLayout.setText(app.font45,bestScore);
            float larguraTexto = glyphLayout.width;

            app.font45.draw(app.batch,bestScore,larguraDispositivo/2-larguraTexto/2,alturaDispositivo/2-gameOver.getHeight()+250);

            app.batch.draw(retry, larguraDispositivo/2-retry.getWidth()/2,alturaDispositivo/2-gameOver.getHeight()-10);
			//batch.draw(imgBotaoRetry,larguraDispositivo/2-retry.getWidth()/2,alturaDispositivo/2-gameOver.getHeight()-10);
			imgBotaoRetry.setPosition(larguraDispositivo/2-retry.getWidth()/2,alturaDispositivo/2-gameOver.getHeight()-10);
			stage.addActor(imgBotaoRetry);
		}
		app.batch.end();

	}
	//----------------------------------------------------------------------------------------------
	public void validarPontos()
	{
		for(int i=0; i<canoTopo.length;i++)
		{
			if(posicaoCanoHorizontal[i] <120-passaros[0].getWidth())//passou da posicao do passaro
			{
				if(!passouCano)
				{
					pontos++;
					passouCano=true;
					if(espacoEntreCanos>200)
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
			variacao+=Gdx.graphics.getDeltaTime()*15;
			variacaoFlutuacaoPassaro+=Gdx.graphics.getDeltaTime();

			//calcula a diferenca entre um render e outro
			//Gdx.app.log("XXX","variacao: "+Gdx.graphics.getDeltaTime());
			if(variacao>3)
			{
				if(estadoJogo==1 && !toqueTela)
				{
					if(variacao>7)
					{
						variacao+=Gdx.graphics.getDeltaTime()*60;

					}
					if(variacao>13)
					{
						variacao=14;
					}

					Gdx.app.log("XXX","variacao: "+variacao);
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
		imgFundo = new Image(fundo);

		gameOver = new Texture("gameover.png");

		retry =  new Texture("botao_retry.png");
		imgBotaoRetry = new Image(retry);

		ready =  new Texture("ready.png");
	}
	//------------------------------------------------------------------------------------------------
	private void incializarObjetos()
	{
		//larguraDispositivo =  Gdx.graphics.getWidth();
		larguraDispositivo =  Aplicacao.VIRTUAL_WIDHT;
		//alturaDispositivo =  Gdx.graphics.getHeight();
		alturaDispositivo =  Aplicacao.VIRTUAL_HEIGHT;
		posicaoPassaroVertical = alturaDispositivo/2+20;

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

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/press_start.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;

        generator.dispose();

		//Formas Gemometricas para colisoes
		circuloPassaro =  new Circle();
		retanguloCanoBaixo =  new Rectangle();
		retanguloCanoTopo =  new Rectangle();
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
		camera.position.set(Aplicacao.VIRTUAL_WIDHT /2,Aplicacao.VIRTUAL_HEIGHT /2,0);

		//Stretch estica
		viewport = new StretchViewport(Aplicacao.VIRTUAL_WIDHT, Aplicacao.VIRTUAL_HEIGHT,camera);
		//preenches
		//viewport = new FillViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT,camera);
		//corta as laterais para ajustar
		//viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT,camera);
		//iniciarBotoes();
	}


	//----------------------------------------------------------------------------------------------
	@Override
	public void resize(int width, int height)
	{
		viewport.update(width,height);
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

	//------------------------------------------------------------------------------------------------
	@Override
	public void dispose ()
	{
		stage.dispose();
	}



	//----------------------------------------------------------------------------------
	//METODOS ANTIGOS
	//----------------------------------------------------------------------------------------------
	/*@Override
	public void create ()
	{
		//this.setScreen(new SplashScreen(this));
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
	}*/

}
