package com.planetas.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Demo extends ApplicationAdapter {

	public static final boolean FULL_SCREEN = false;
	public static final int HEIGHT = 800;
	public static final int WIDTH = 800;
	public static final String TITLE = "Demo";

	public static final String SUN_FILE = "sun.png";
	public static final String PLANET_FILE = "planet.png";

	AssetManager am;
	SpriteBatch batch;
	Texture img;
	World world;

	protected void loadAssets() {
		am.load(SUN_FILE, Texture.class);
		am.load(PLANET_FILE, Texture.class);
	}

	protected void createSprites() {
		am.finishLoading();
		
		Texture txrSun = am.get(SUN_FILE, Texture.class);
		Texture txrPlanet = am.get(PLANET_FILE, Texture.class);

		Sprite sun = new Sprite(txrSun);
		sun.setSize(200f, 200f);
		
		Sprite planet = new Sprite(txrPlanet);
		planet.setSize(50f, 50f);

		world = new World(new Vector2(0, 0), false);

		BodyDef sunDef = new BodyDef();
		BodyDef planetDef = new BodyDef();

		float x = Gdx.graphics.getWidth() / 2.0f;
		float y = Gdx.graphics.getHeight()/ 2.0f;

		sunDef.position.set(new Vector2(x, y));
		planetDef.position.set(new Vector2(x - 200, y));

		world = new World(new Vector2(0, 0), false);
		Body sunBody = world.createBody(sunDef);
		Body planetBody = world.createBody(planetDef);

		CircleShape sunCircle = new CircleShape();
		CircleShape planetCircle = new CircleShape();
		sunCircle.setRadius(150f);
		planetCircle.setRadius(50f);

		FixtureDef sunFixtureDef = new FixtureDef();
		FixtureDef planetFixtureDef = new FixtureDef();
		sunFixtureDef.shape = sunCircle;
		planetFixtureDef.shape = planetCircle;

		sunBody.createFixture(sunFixtureDef);
		planetBody.createFixture(planetFixtureDef);

		sunBody.setUserData(sun);
		planetBody.setUserData(planet);

		sunCircle.dispose();
		planetCircle.dispose();
	}

	@Override
	public void create() {
		am = new AssetManager();
		loadAssets();
		createSprites();
		batch = new SpriteBatch();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		world.step(Gdx.graphics.getDeltaTime(), 6, 2);

		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);

		for (Body b : bodies) {
			Sprite sprite = (Sprite) b.getUserData();
			if (sprite != null) {
				sprite.setPosition(b.getPosition().x, b.getPosition().y);
				sprite.setRotation(MathUtils.radiansToDegrees * b.getAngle());
			}
			
			batch.begin();
			sprite.draw(batch);
			batch.end();
		}

//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
		am.dispose();
		world.dispose();
	}
}
