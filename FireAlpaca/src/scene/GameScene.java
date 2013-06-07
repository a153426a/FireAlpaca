package scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import Object.Bullet;
import Object.Enemy;
import Object.Player;
import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.example.base.BaseScene;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

import extra.CoolDown;
import extra.LevelCompleteWindow;
import extra.LevelCompleteWindow.StarsCount;

public class GameScene extends BaseScene implements IOnSceneTouchListener {

	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	private Text scoreText;
	private int score = 0;
	private boolean isLevelComplete = false;
	private boolean firstTouch = false;
	private int level = 1;

	// game graphic fields
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";

	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_STONE = "stone";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BREAKABLE = "breakable";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN = "coin";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE = "levelComplete";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BLUE_ENEMY = "blueEnemy";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RED_ENEMY = "redEnemy";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_YELLOW_ENEMY = "yellowEnemy";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BASE = "base";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_FLAG = "flag";
	private Player player;
	public LinkedList<Bullet> bulletList;
	public int bulletCount;
	private List<Enemy> enemyList;

	// Enum for enemy AI
	public enum Map {
		BREAKABLE, STONE, COIN, BASE, FLAG, ENEMY, EMPTY
	}

	// (gigantic?) matrix field for enemy AI.
	public Map[][] map;

	private Text gameOverText;
	private Text intro;
	private LevelCompleteWindow levelCompleteWindow;
	private boolean gameOverDisplayed = false;

	// analog on screen control
	private AnalogOnScreenControl analogControl;

	@Override
	public void createScene() {
		enemyList = new ArrayList<Enemy>();
		map = new Map[40][24];
		for (int i = 0; i < 39; i++) {
			for (int j = 0; j < 23; j++) {
				map[i][j] = Map.EMPTY;
			}
		}
		createBackground();
		createControl();
		createHUD();
		createPhysics();
		loadLevel(level);
		setOnSceneTouchListener(this);
		createGameOverText();
		createIntro();
		displayIntroText();
		levelCompleteWindow = new LevelCompleteWindow(vbom);
		bulletList = new LinkedList<Bullet>();
	}

	private void createIntro() {

		if (level == 1 || level == 5) {
			intro = new Text(0, 0, resourcesManager.font,
					"Destory all enemy! ", vbom);
		} else if (level == 2 || level == 6) {
			intro = new Text(0, 0, resourcesManager.font, "Protect the base! ",
					vbom);
		} else if (level == 3 || level == 7) {
			intro = new Text(0, 0, resourcesManager.font,
					"Reach the extraction flag! ", vbom);
		} else if (level == 4) {
			intro = new Text(0, 0, resourcesManager.font,
					"Survive for 20 seconds!  ", vbom);
		} else {
			intro = new Text(0, 0, resourcesManager.font,
					"Kill the Final Boss! ", vbom);
		}

	}

	private void displayIntroText() {
		intro.setPosition(camera.getCenterX(), camera.getCenterY());
		attachChild(intro);
	}

	public void createControl() {
		analogControl = new AnalogOnScreenControl(68, 68, camera,
				ResourcesManager.getInstance().analog_base_region,
				ResourcesManager.getInstance().analog_knob_region, 0.1f, 200,
				vbom, new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {
						player.getBody().setLinearVelocity(pValueX * 2,
								pValueY * 2);
						if (pValueX != 0 && pValueY != 0
								&& CoolDown.getSharedInstance().checkValidity()) {
							player_shoot(pValueX, pValueY);
						}
						// clean();

					}

					@Override
					public void onControlClick(
							final AnalogOnScreenControl pAnalogOnScreenControl) {

					}
				});

		analogControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA,
				GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogControl.getControlBase().setAlpha(0.5f);
		analogControl.getControlBase().setScaleCenter(0, 128);
		analogControl.refreshControlKnobPosition();

		setChildScene(analogControl);
	}

	@Override
	public void onBackKeyPressed() {

		SceneManager.getInstance().loadMenuScene(engine);

	}

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disposeScene() {

		camera.setHUD(null);
		camera.setCenter(400, 240);

	}

	private void createBackground() {

		attachChild(new Sprite(400, 240,
				resourcesManager.game_background_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});

	}

	private void createHUD() {

		gameHUD = new HUD();

		scoreText = new Text(20, 420, resourcesManager.font,
				"Score: 0123456789", new TextOptions(HorizontalAlign.LEFT),
				vbom);
		scoreText.setAnchorCenter(0, 0);
		scoreText.setText("Score: 0");
		gameHUD.attachChild(scoreText);
		camera.setHUD(gameHUD);

	}

	private void addToScore(int i) {

		score += i;
		scoreText.setText("Score: " + score);

	}

	private void createPhysics() {

		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
		registerUpdateHandler(physicsWorld);
		physicsWorld.setContactListener(contactListener());

	}

	private void loadLevel(int levelID) {
		final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0,
				0.01f, 0.5f);

		levelLoader
				.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(
						LevelConstants.TAG_LEVEL) {
					public IEntity onLoadEntity(
							final String pEntityName,
							final IEntity pParent,
							final Attributes pAttributes,
							final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData)
							throws IOException {
						final int width = SAXUtils.getIntAttributeOrThrow(
								pAttributes,
								LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
						final int height = SAXUtils.getIntAttributeOrThrow(
								pAttributes,
								LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);

						camera.setBounds(0, 0, width, height); // here we set
																// camera bounds
						camera.setBoundsEnabled(true);

						return GameScene.this;
					}
				});

		levelLoader
				.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(
						TAG_ENTITY) {
					public IEntity onLoadEntity(
							final String pEntityName,
							final IEntity pParent,
							final Attributes pAttributes,
							final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData)
							throws IOException {
						final int x = SAXUtils.getIntAttributeOrThrow(
								pAttributes, TAG_ENTITY_ATTRIBUTE_X);
						final int y = SAXUtils.getIntAttributeOrThrow(
								pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
						final String type = SAXUtils.getAttributeOrThrow(
								pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);

						final Sprite levelObject;

						if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_STONE)) {
							levelObject = new Sprite(x, y,
									resourcesManager.stone_region, vbom);
							PhysicsFactory.createBoxBody(physicsWorld,
									levelObject, BodyType.StaticBody,
									FIXTURE_DEF).setUserData("stone");
							map[(x - 10) / 20][(y - 10) / 20] = Map.STONE;

						} else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BREAKABLE)) {
							levelObject = new Sprite(x, y,
									resourcesManager.breakable_region, vbom);
							final Body body = PhysicsFactory.createBoxBody(
									physicsWorld, levelObject,
									BodyType.StaticBody, FIXTURE_DEF);
							body.setUserData("breakable");
							map[(x - 10) / 20][(y - 10) / 20] = Map.BREAKABLE;
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BASE)) {
							levelObject = new Sprite(x, y,
									resourcesManager.base_region, vbom);
							PhysicsFactory.createBoxBody(physicsWorld,
									levelObject, BodyType.StaticBody,
									FIXTURE_DEF).setUserData("base");
							// TODO
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_FLAG)) {
							levelObject = new Sprite(x, y,
									resourcesManager.flag_region, vbom);
							PhysicsFactory.createBoxBody(physicsWorld,
									levelObject, BodyType.StaticBody,
									FIXTURE_DEF).setUserData("flag");
							// TODO
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN)) {
							levelObject = new Sprite(x, y,
									resourcesManager.coin_region, vbom) {
								@Override
								protected void onManagedUpdate(
										float pSecondsElapsed) {
									super.onManagedUpdate(pSecondsElapsed);

									if (player.collidesWith(this)) {

										addToScore(100);
										this.setVisible(false);
										this.setIgnoreUpdate(true);
									}

								}
							};
							levelObject
									.registerEntityModifier(new LoopEntityModifier(
											new ScaleModifier(1, 1, 1.3f)));
							map[(x - 10) / 20][(y - 10) / 20] = Map.COIN;
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
							player = new Player(x, y, vbom, camera,
									physicsWorld) {
								// TODO
								@Override
								public void onDie() {
									if (!gameOverDisplayed) {
										// physicsWorld.unregisterPhysicsConnector(physicsConnector);
										player.getBody().setActive(false);
										displayGameOverText();
									}
								}

							};
							levelObject = player;
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE)) {
							levelObject = new Sprite(x, y,
									resourcesManager.complete_stars_region,
									vbom) {
								@Override
								protected void onManagedUpdate(
										float pSecondsElapsed) {
									super.onManagedUpdate(pSecondsElapsed);

									if (player.collidesWith(this)) {
										levelCompleteWindow.display(
												StarsCount.TWO, GameScene.this,
												camera);
										this.setVisible(false);
										this.setIgnoreUpdate(true);
									}
								}
							};
							levelObject
									.registerEntityModifier(new LoopEntityModifier(
											new ScaleModifier(1, 1, 1.3f)));

						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RED_ENEMY)) {
							levelObject = new Enemy(x, y, vbom, camera,
									physicsWorld, ResourcesManager
											.getInstance().red_enemy_region,
									player, level) {
								@Override
								public void onDie() {
									addToScore(100);
									this.get_body().setActive(false);
									this.setVisible(false);
									this.setIgnoreUpdate(true);
									enemyList.remove(this);
									map[(int) ((this.getX() - 10) / 20)][(int) ((this
											.getY() - 10) / 20)] = null;
								}
							};
							enemyList.add((Enemy) levelObject);
							map[(x - 10) / 20][(y - 10) / 20] = Map.ENEMY;
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BLUE_ENEMY)) {
							levelObject = new Enemy(x, y, vbom, camera,
									physicsWorld, ResourcesManager
											.getInstance().blue_enemy_region,
									player, level) {
								@Override
								public void onDie() {
									addToScore(30);
									this.setVisible(false);
									this.setIgnoreUpdate(true);
									enemyList.remove(this);
									map[(int) ((this.getX() - 10) / 20)][(int) ((this
											.getY() - 10) / 20)] = null;
								}
							};
							enemyList.add((Enemy) levelObject);
							map[(x - 10) / 20][(y - 10) / 20] = Map.ENEMY;
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_YELLOW_ENEMY)) {
							levelObject = new Enemy(x, y, vbom, camera,
									physicsWorld, ResourcesManager
											.getInstance().yellow_enemy_region,
									player, level) {
								@Override
								public void onDie() {
									addToScore(50);
									this.setVisible(false);
									this.setIgnoreUpdate(true);
									enemyList.remove(this);
									map[(int) ((this.getX() - 10) / 20)][(int) ((this
											.getY() - 10) / 20)] = null;
								}
							};
							enemyList.add((Enemy) levelObject);
							map[(x - 10) / 20][(y - 10) / 20] = Map.ENEMY;
						}

						else {
							throw new IllegalArgumentException();
						}

						levelObject.setCullingEnabled(true);

						return levelObject;
					}
				});

		levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID
				+ ".lvl");
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		if (!firstTouch) {

			intro.setVisible(false);
			intro.detachSelf();
			firstTouch = true;

		} else {

			if (level == 4 && gameOverDisplayed) {
				SceneManager.getInstance().loadGameScene(engine);
			}

			if (pSceneTouchEvent.isActionDown() && isLevelComplete) {

				if (level != 8) {
					level++;
					SceneManager.getInstance().loadGameScene(engine);
				} else {
					SceneManager.getInstance().loadMenuScene(engine);
				}

			}
		}

		return false;

	}

	private void createGameOverText() {

		gameOverText = new Text(0, 0, resourcesManager.font, "Game Over!", vbom);

	}

	private void displayGameOverText() {

		gameOverText.setPosition(camera.getCenterX(), camera.getCenterY());
		attachChild(gameOverText);
		gameOverDisplayed = true;

	}

	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x1.getBody().getUserData().equals("stone")
						&& x2.getBody().getUserData().equals("player")) {
					player.onDie();
				}

				if (x1.getBody().getUserData().equals("breakable")
						&& x2.getBody().getUserData().equals("player")) {
					engine.registerUpdateHandler(new TimerHandler(0.2f,
							new ITimerCallback() {
								public void onTimePassed(
										final TimerHandler pTimerHandler) {
									pTimerHandler.reset();
									engine.unregisterUpdateHandler(pTimerHandler);
									player.onDie();
								}
							}));
				}

				if ((x1.getBody().getUserData().equals("blueEnemy")
						|| x1.getBody().getUserData().equals("redEnemy") || x1
						.getBody().getUserData().equals("yellowEnemy"))
						&& x2.getBody().getUserData().equals("player")) {
					player.onDie();
				}

				if ((x2.getBody().getUserData().equals("blueEnemy")
						|| x2.getBody().getUserData().equals("redEnemy") || x2
						.getBody().getUserData().equals("yellowEnemy"))
						&& x1.getBody().getUserData().equals("player")) {
					player.onDie();
				}

				if (x2.getBody().getUserData().equals("bullet")
						&& x1.getBody().equals("stone")) {
					player.onDie();
				}

				if (x1.getBody().getUserData() != null
						&& x2.getBody().getUserData() != null) {

				}

			}

			public void endContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				if (x1.getBody().getUserData() != null
						&& x2.getBody().getUserData() != null) {
					if (x2.getBody().getUserData().equals("player")) {
						// TODO;
					}
				}
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub

			}

		};
		return contactListener;
	}

	/*
	 * public void clean() { Iterator<Bullet> it = bulletList.iterator();
	 * 
	 * while(it.hasNext()) { Bullet b = (Bullet) it.next(); int mapX = (int)
	 * ((b.sprite.getX()-2)/20); int mapY = (int) ((b.sprite.getY()-2)/20);
	 * if(b.sprite.getX()<=0 || b.sprite.getX()>=800 || b.sprite.getY()>=480 ||
	 * b.sprite.getY()<=0) { BulletPool.shareBulletPool().recyclePoolItem(b);
	 * it.remove(); continue; }
	 * 
	 * if(map[mapX][mapY] != Map.EMPTY) {
	 * BulletPool.shareBulletPool().recyclePoolItem(b); it.remove(); continue; }
	 * 
	 * if(map[mapX][mapY] == Map.BREAKABLE) { //TODO remove breakable
	 * map[mapX][mapY] = Map.EMPTY; addToScore(10); continue; }
	 * 
	 * if(map[mapX][mapY] == Map.ENEMY) { map[mapX][mapY] = Map.EMPTY;
	 * find_enemy(mapX, mapY).onDie();
	 * 
	 * }
	 * 
	 * } }
	 */

	private Enemy find_enemy(int x, int y) {

		int xMin = x * 20 + 10;
		int xMax = x * 20 + 30;
		int yMin = y * 20 + 10;
		int yMax = y * 20 + 30;
		Enemy result = null;

		for (int i = 0; i < enemyList.size(); i++) {
			if (enemyList.get(i).getX() >= xMin
					&& enemyList.get(i).getX() <= xMax
					&& enemyList.get(i).getY() >= yMin
					&& enemyList.get(i).getY() <= yMax) {
				result = enemyList.get(i);
			}
		}
		return result;
	}

	public void player_shoot(float x, float y) {

		float xVel, yVel;
		int a, b;
		float px = x * x;
		float py = y * y;
		if (x > 0 && y > 0) {
			xVel = px / (px + py);
			yVel = py / (px + py);
			a = 1;
			b = 1;
		} else if (x > 0 && y < 0) {
			xVel = px / (px + py);
			yVel = -py / (px + py);
			a = 1;
			b = -1;
		} else if (x < 0 && y < 0) {
			xVel = -px / (px + py);
			yVel = -py / (px + py);
			a = -1;
			b = -1;
		} else {
			xVel = -px / (px + py);
			yVel = py / (px + py);
			a = -1;
			b = 1;
		}
		Bullet bullet = new Bullet(player.getX() + 15 * a, player.getY() + 15
				* b, vbom, camera, physicsWorld, "player_bullet");
		attachChild(bullet);
		bullet.bullet_get_body().setLinearVelocity(xVel * 20, yVel * 20);
	}
}
