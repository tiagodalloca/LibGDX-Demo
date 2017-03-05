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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
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
	public static final double G = 6.67408;

	public static final String SUN_FILE = "sun.png";
	public static final String PLANET_FILE = "planet.png";

	AssetManager am;
	SpriteBatch batch;
	Texture img;
	World world;

	Body sunBody;
	Body planetBody;

	Box2DDebugRenderer debugRenderer;

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
		planetDef.position.set(new Vector2(x + 400, y + 200));

		world = new World(new Vector2(0, 0), false);
		sunBody = world.createBody(sunDef);
		planetBody = world.createBody(planetDef);

		CircleShape sunCircle = new CircleShape();
		CircleShape planetCircle = new CircleShape();
		sunCircle.setRadius(100);
		planetCircle.setRadius(25);
		sunCircle.setPosition(new Vector2(100, 100));
		planetCircle.setPosition(new Vector2(25, 25));

		FixtureDef sunFixtureDef = new FixtureDef();
		FixtureDef planetFixtureDef = new FixtureDef();
		sunFixtureDef.shape = sunCircle;
		planetFixtureDef.shape = planetCircle;
		sunFixtureDef.density = 1000;
		planetFixtureDef.density = 0.1f;

		sunBody.createFixture(sunFixtureDef);
		planetBody.createFixture(planetFixtureDef);
		sunBody.setUserData(sun);
		planetBody.setUserData(planet);
		
//		double pv = Math.sqrt(((sunBody.getMass() + planetBody.getMass())*G)/planetBody.getPosition().dst(sunBody.getPosition()));
//		
//		Vector2 dstVector2 = planetBody.getPosition();
//		dstVector2.sub(sunBody.getPosition());
//		
//		double pvx = 2000;
//		double pvy = 1000;
//		
//		planetBody.applyLinearImpulse(new Float(pvx), new Float(pvy), 
//						planetBody.getPosition().x,
//						planetBody.getPosition().y, true);

		sunCircle.dispose();
		planetCircle.dispose();
	}

	protected void updateOrbits() {
		Vector2 dstVector2 = planetBody.getPosition();
		dstVector2.sub(sunBody.getPosition());
		
		double dst2 = Math.pow(dstVector2.x, 2) + Math.pow(dstVector2.y, 2);

		double f = (G * planetBody.getMass() * sunBody.getMass()) / dst2;
		double fx = dstVector2.x * f *-1;
		double fy = dstVector2.y * f *-1;

		planetBody.applyForceToCenter(
						new Vector2(new Float(fx), new Float(fy)),
						true);

		sunBody.applyForceToCenter(
						new Vector2(new Float(fx * -1), new Float(fy * -1)),
						true);
	}

	@Override
	public void create() {
		am = new AssetManager();
		loadAssets();
		createSprites();
		batch = new SpriteBatch();
		debugRenderer = new Box2DDebugRenderer();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		updateOrbits();
		world.step(Gdx.graphics.getDeltaTime(), 2, 2);

		debugRenderer.render(world, batch.getProjectionMatrix());

		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);

		for (Body b : bodies) {
			Sprite sprite = (Sprite) b.getUserData();
			if (sprite != null) {
				sprite.setPosition(b.getPosition().x, b.getPosition().y);
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
