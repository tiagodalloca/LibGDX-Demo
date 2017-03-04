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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Demo extends ApplicationAdapter {

	public static final boolean FULL_SCREEN = true;
	public static final int HEIGHT = 800;
	public static final int WIDTH = 800;
	public static final String TITLE = "Demo";

	public static final String SUN_FILE = "sun.png";
	public static final String PLANET_FILE = "planet.png";

	AssetManager am;
	SpriteBatch batch;
	Texture img;
	World world;

	Body sunBody;
	Body planetBody;

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
		sunDef.type = BodyDef.BodyType.DynamicBody;
		planetDef.type = BodyDef.BodyType.DynamicBody;

		float x = (Gdx.graphics.getWidth() - sun.getWidth()) / 2.0f;
		float y = (Gdx.graphics.getHeight() - sun.getHeight()) / 2.0f;

		sunDef.position.set(new Vector2(x, y));
		planetDef.position.set(new Vector2(x - 200, y));

		world = new World(new Vector2(0, 0), false);
		sunBody = world.createBody(sunDef);
		planetBody = world.createBody(planetDef);

		CircleShape sunCircle = new CircleShape();
		CircleShape planetCircle = new CircleShape();
		sunCircle.setRadius(150f);
		planetCircle.setRadius(50f);

		FixtureDef sunFixtureDef = new FixtureDef();
		FixtureDef planetFixtureDef = new FixtureDef();
		sunFixtureDef.shape = sunCircle;
		planetFixtureDef.shape = planetCircle;
		sunFixtureDef.density = 1000;
		planetFixtureDef.density = 10;

		sunBody.createFixture(sunFixtureDef);
		planetBody.createFixture(planetFixtureDef);
		sunBody.setUserData(sun);
		planetBody.setUserData(planet);

		sunCircle.dispose();
		planetCircle.dispose();
	}

	protected void updateOrbits() {
		double dst2 = planetBody.getPosition().dst2(sunBody.getPosition());
		float dstX = planetBody.getPosition().x - sunBody.getPosition().x;
		float dstY = planetBody.getPosition().y - sunBody.getPosition().y;

		double bearingAngle = Math.atan2(dstX, dstY) * -1;

		double f = (6.67408f * planetBody.getMass() * sunBody.getMass()) / dst2;
		double fx = Math.sin(bearingAngle) * f;
		double fy = Math.cos(bearingAngle) * f;

		planetBody.applyForceToCenter(
						new Vector2(new Float(fx), new Float(fy)),
						true);
		
//		sunBody.applyForceToCenter(
//						new Vector2(new Float(fx * -1), new Float(fy * -1)),
//						true);
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

		updateOrbits();
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
		am.dispose();
		world.dispose();
	}
}
