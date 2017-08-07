package com.kaleshsingh.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
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

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture background;
    private ShapeRenderer shapeRenderer;
    private Texture gameOver;

	private Texture[] birds;
    private int flapState = 0;
    private float birdX = 0;
    private float birdY = 0;
    private float velocity = 0;
    private Circle birdCircle;

    private int gameState = 0;
    private float gravity = 1.8f;

    private Texture topTube;
    private Texture bottomTube;
    private float gap = 600;
    private float maxTubeOffset;
    private Random randomGenerator;
    private float tubeVelocity = 6;
    private int numberOfTubes = 4;
    private float tubeX[] = new float[numberOfTubes];
    private float tubeOffset[] = new float[numberOfTubes];
    private float distanceBetweenTubes;
    private Rectangle topTubeRectangles[] = new Rectangle[numberOfTubes];
    private Rectangle bottomTubeRectangles[] = new Rectangle[numberOfTubes];

    private int score = 0;
    private int scoringTube = 0;        // The first tube is tube 0;


    // Set up a Font to write text
    BitmapFont font;



	@Override
	public void create () {
		batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
		background = new Texture("bg.png");
        font = new BitmapFont();        // Default Font
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
        gameOver = new Texture("gameover.png");


        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        birdX = (Gdx.graphics.getWidth() / 2) - (birds[0].getWidth() / 2);
        birdCircle = new Circle();

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
//        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;
        distanceBetweenTubes = 600;

        // Start Game
        startGame();

	}

	public void startGame(){
        birdY = (Gdx.graphics.getHeight() / 2) - (birds[0].getHeight() / 2);

        for (int i = 0; i < numberOfTubes; ++i){
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + i * distanceBetweenTubes + Gdx.graphics.getWidth();

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }

        score = 0;
        scoringTube = 0;        // The first tube is tube 0;
        velocity = 0;
    }

	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());



        if (gameState == 1) {

            // Every time one of the tubes pass a certain point increment the score.
            if (tubeX[scoringTube] <= Gdx.graphics.getWidth() / 2 - bottomTube.getWidth() - birds[0].getWidth() /2){
                score += 1;

                scoringTube += 1;
                scoringTube %= 4;

                // Another way to increment the scoring tube;
                // scoringTube = (scoringTube < numberOfTubes - 1) ? scoringTube + 1 : 0;

                Gdx.app.log("Score", String.valueOf(score));
            }

            if(Gdx.input.justTouched()){
                velocity = -30;
            }

            for (int i = 0; i < numberOfTubes; ++i) {

                //Check when a tube has moved completely off the left of the screen
                if(tubeX[i] < -topTube.getWidth()){
                    // Reposition it to the right behind the other tubes.
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;

                    // And recalculate the tube offset to avoid repetitiveness
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                }
                else{
                    tubeX[i] -= tubeVelocity;
                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            }

            flapState = (flapState == 0) ? 1 : 0;       // Short-hand Conditional Assignment
            batch.draw(birds[flapState], birdX, birdY);
            font.draw(batch, String.valueOf(score), 30, Gdx.graphics.getHeight() - 30);      // Add Score the font to the batch

            if(birdY > 0) {     // TODO:Stops the bird from falling off the bottom, while still allowing us to move the bird up
                velocity += gravity;        // Increase the velocity each time the render loop is called.
                birdY -= velocity;   // Decrease the Y position of the bird by the velocity to give
                // the effect of gravity.
            }
            else{
                gameState = 2;
            }

            // Test for collision

            if(Intersector.overlaps(birdCircle, topTubeRectangles[scoringTube]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[scoringTube])) {
                Gdx.app.log("Collision", "Yes!");
                gameState = 2;
            }
        }
        else if (gameState == 0){

            batch.draw(birds[0], birdX, birdY);

            if (Gdx.input.justTouched()){
                Gdx.app.log("Touched", "The screen was touched!");
                gameState = 1;
            }
        } else if(gameState == 2){
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
            if (Gdx.input.justTouched()){
                Gdx.app.log("Touched", "The screen was touched!");
                gameState = 0;

                startGame();
            }
        }

        batch.end();

        // Must be enabled inorder to render transparent shapes
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 1, 0, 0.0f));
        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);
        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        for (int i = 0; i < numberOfTubes; ++i) {
            shapeRenderer.rect(topTubeRectangles[i].x, topTubeRectangles[i].y, topTubeRectangles[i].width, topTubeRectangles[i].height);
            shapeRenderer.rect(bottomTubeRectangles[i].x, bottomTubeRectangles[i].y, bottomTubeRectangles[i].width, bottomTubeRectangles[i].height);
        }
        shapeRenderer.end();

        // Disable
        Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
