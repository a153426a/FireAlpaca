package scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import org.andengine.input.touch.controller.MultiTouchController;
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
	public PhysicsWorld physicsWorld;
	private Text scoreText;
	private int score = 0;
	private int coin = 0;
	private boolean isLevelComplete = false;
	private boolean firstTouch = false;
	private boolean baseDestoryed = false;
	private boolean introDisplaying = true; 
	private int base_health = 20;

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
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BOSS = "boss";
	public Player player;

	// many many many hashMaps
	private Map<Body, Enemy> enemies;
	private HashMap player_bullets;
	private HashMap breakables;
	private HashMap enemy_bullets;


	private Text gameOverText;
	private Text intro;
	private LevelCompleteWindow levelCompleteWindow;
	private boolean gameOverDisplayed = false;
	public int bulletCounter; 

	// analog on screen control
	private AnalogOnScreenControl analogLeftControl;
	private AnalogOnScreenControl analogRightControl;

	@Override
	public void createScene(int lv) {
		enemies = new HashMap<Body, Enemy>();

		breakables = new HashMap();
		player_bullets = new HashMap();
		enemy_bullets = new HashMap();
		engine.setTouchController(new MultiTouchController());
		createBackground();
		createLeftControl();
		createRightControl();
		createHUD();
		createPhysics();
		loadLevel(lv);
		setOnSceneTouchListener(this);
		createIntro();
		displayIntroText();
		createGameOverText();
		levelCompleteWindow = new LevelCompleteWindow(vbom);
	}

	private void createIntro() {

		if (level == 1 || level == 5) {
			intro = new Text(0, 0, resourcesManager.font,
					"Destory all enemy! ", vbom);
		} else if (level == 2 || level == 6) {
			intro = new Text(0, 0, resourcesManager.font, "Protect the base! ",
					vbom);
		} else if (level == 3 || level == 7) {
			intro = new Text(0, 0, resourcesManager.font, "Reach the flag! ",
					vbom);
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

	public void createLeftControl() {
		
		analogLeftControl = new AnalogOnScreenControl(68, 68, camera,
				ResourcesManager.getInstance().analog_base_region,
				ResourcesManager.getInstance().analog_knob_region, 0.1f, 200,
				vbom, new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {
						player.getBody().setLinearVelocity(pValueX * 3,
								pValueY * 3);
					}

					@Override
					public void onControlClick(
							final AnalogOnScreenControl pAnalogOnScreenControl) {

					}
				});

		analogLeftControl.getControlBase().setBlendFunction(
				GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogLeftControl.getControlBase().setAlpha(0.5f);
		analogLeftControl.getControlBase().setScaleCenter(0, 128);
		// analogLeftControl.refreshControlKnobPosition();

		setChildScene(analogLeftControl);
	}

	public void createRightControl() {
		analogRightControl = new AnalogOnScreenControl(
				700, 68, camera,
				ResourcesManager.getInstance().analog_base_region,
				ResourcesManager.getInstance().analog_knob_region, 0.1f, 200,
				vbom, new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {
						if ((pValueX != 0 || pValueY != 0)
								&& CoolDown.getSharedInstance().checkValidity()) {
							player_shoot(pValueX, pValueY);
						}

					}

					@Override
					public void onControlClick(
							final AnalogOnScreenControl pAnalogOnScreenControl) {

					}
				});

		analogRightControl.getControlBase().setBlendFunction(
				GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogRightControl.getControlBase().setAlpha(0.5f);
		analogRightControl.getControlBase().setScaleCenter(800, 128);
		// analogRightControl.refreshControlKnobPosition();
		analogLeftControl.setChildScene(analogRightControl);
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().setLevel(1);
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

		physicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0f, 0f),
				false, 8, 1);
		// physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0),
		// false);
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

						} else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BREAKABLE)) {
							levelObject = new Sprite(x, y,
									resourcesManager.breakable_region, vbom);
							final Body body = PhysicsFactory.createBoxBody(
									physicsWorld, levelObject,
									BodyType.StaticBody, FIXTURE_DEF);
							body.setUserData("breakable");
							breakables.put(body, levelObject);
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
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
							player = new Player(x, y, vbom, camera,
									physicsWorld) {
								// TODO
								@Override
								public void onDie() {
									if (!gameOverDisplayed) {

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
									/*if(bulletCounter<60) {
										bulletCounter++; 
									} else { 
										bulletCounter = 1;
									}
									
									if(bulletCounter == 1) { 
									if(level == 2 || level == 6) { 
										enemy_shoot(400, 40); 
									} else { 
										enemy_shoot(player.getX(), player.getY()); 
									}}*/
									
									if ((level == 1 || level == 5)
											&& enemies.isEmpty()) {
										if (score > 100 && score < 200) {
											levelCompleteWindow.display(
													StarsCount.TWO,
													GameScene.this, camera);
										} else if (score > 200) {
											levelCompleteWindow.display(
													StarsCount.THREE,
													GameScene.this, camera);
										} else {
											levelCompleteWindow.display(
													StarsCount.ONE,
													GameScene.this, camera);
										}
										SceneManager.getInstance()
												.incTotalScore(score);
										SceneManager.getInstance()
												.incTotalCoin(coin);
										if (level > maxLevel) {
											SceneManager.getInstance()
													.setMaxLevel(level);
										}
										isLevelComplete = true;
										this.setVisible(false);
										this.setIgnoreUpdate(true);
									} else if ((level == 2 || level == 6)
											&& (baseDestoryed || enemies
													.isEmpty())) {
										if (score > 100 && score < 200) {
											levelCompleteWindow.display(
													StarsCount.TWO,
													GameScene.this, camera);
										} else if (score > 200) {
											levelCompleteWindow.display(
													StarsCount.THREE,
													GameScene.this, camera);
										} else {
											levelCompleteWindow.display(
													StarsCount.ONE,
													GameScene.this, camera);
										}
										SceneManager.getInstance()
												.incTotalScore(score);
										SceneManager.getInstance()
												.incTotalCoin(coin);
										if (level > maxLevel) {
											SceneManager.getInstance()
													.setMaxLevel(level);
										}
										isLevelComplete = true;
										this.setVisible(false);
										this.setIgnoreUpdate(true);
									} else if (level == 3
											&& (player.getX() > 745
													&& player.getX() < 795
													&& player.getY() > 185 && player
													.getY() < 235)) {
										if (score > 100 && score < 200) {
											levelCompleteWindow.display(
													StarsCount.TWO,
													GameScene.this, camera);
										} else if (score > 200) {
											levelCompleteWindow.display(
													StarsCount.THREE,
													GameScene.this, camera);
										} else {
											levelCompleteWindow.display(
													StarsCount.ONE,
													GameScene.this, camera);
										}
										SceneManager.getInstance()
												.incTotalScore(score);
										SceneManager.getInstance()
												.incTotalCoin(coin);
										if (level > maxLevel) {
											SceneManager.getInstance()
													.setMaxLevel(level);
										}
										isLevelComplete = true;
										this.setVisible(false);
										this.setIgnoreUpdate(true);
									} else if (level == 7
											&& (player.getX() > 745
													&& player.getX() < 795
													&& player.getY() > 5 && player
													.getY() < 55)) {
										if (score > 100 && score < 200) {
											levelCompleteWindow.display(
													StarsCount.TWO,
													GameScene.this, camera);
										} else if (score > 200) {
											levelCompleteWindow.display(
													StarsCount.THREE,
													GameScene.this, camera);
										} else {
											levelCompleteWindow.display(
													StarsCount.ONE,
													GameScene.this, camera);
										}
										SceneManager.getInstance()
												.incTotalScore(score);
										SceneManager.getInstance()
												.incTotalCoin(coin);
										if (level > maxLevel) {
											SceneManager.getInstance()
													.setMaxLevel(level);
										}
										isLevelComplete = true;
										this.setVisible(false);
										this.setIgnoreUpdate(true);
									} else if (level == 4) {
										engine.registerUpdateHandler(new TimerHandler(
												20f, new ITimerCallback() {

													@Override
													public void onTimePassed(
															TimerHandler pTimerHandler) {
														if (score > 100
																&& score < 200) {
															levelCompleteWindow
																	.display(
																			StarsCount.TWO,
																			GameScene.this,
																			camera);
														} else if (score > 200) {
															levelCompleteWindow
																	.display(
																			StarsCount.THREE,
																			GameScene.this,
																			camera);
														} else {
															levelCompleteWindow
																	.display(
																			StarsCount.ONE,
																			GameScene.this,
																			camera);
														}
														isLevelComplete = true;
														SceneManager
																.getInstance()
																.incTotalScore(
																		score);
														SceneManager
																.getInstance()
																.incTotalCoin(
																		coin);
														if (level > maxLevel) {
															SceneManager
																	.getInstance()
																	.setMaxLevel(
																			level);
														}

													}

												}));

										
										this.setVisible(false);
										this.setIgnoreUpdate(true);
									} else if (level == 8 && enemies.isEmpty()) {
										if (score > 100 && score < 200) {
											levelCompleteWindow.display(
													StarsCount.TWO,
													GameScene.this, camera);
										} else if (score > 200) {
											levelCompleteWindow.display(
													StarsCount.THREE,
													GameScene.this, camera);
										} else {
											levelCompleteWindow.display(
													StarsCount.ONE,
													GameScene.this, camera);
										}
										SceneManager.getInstance()
												.incTotalScore(score);
										SceneManager.getInstance()
												.incTotalCoin(coin);
										if (level > maxLevel) {
											SceneManager.getInstance()
													.setMaxLevel(level);
										}
										isLevelComplete = true;
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
							Enemy enemy = new Enemy(x, y, vbom, camera,
									physicsWorld, ResourcesManager
											.getInstance().red_enemy_region) {
								@Override
								public void onDie() {
									addToScore(100);
								}

							

							};
							levelObject = enemy;
							enemies.put(enemy.get_body(), enemy);
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BLUE_ENEMY)) {
							Enemy enemy = new Enemy(x, y, vbom, camera,
									physicsWorld, ResourcesManager
											.getInstance().blue_enemy_region) {
								@Override
								public void onDie() {
									addToScore(30);

								}

								
							};
							levelObject = enemy;
							enemies.put(enemy.get_body(), enemy);
						}
						
						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BOSS)) {
							Enemy enemy = new Enemy(x, y, vbom, camera,
									physicsWorld, ResourcesManager
											.getInstance().boss_region) {
								@Override
								public void onDie() {
									addToScore(1000);
								}

							};
							levelObject = enemy;
							enemies.put(enemy.get_body(), enemy);
						}

						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_YELLOW_ENEMY)) {
							Enemy enemy = new Enemy(x, y, vbom, camera,
									physicsWorld, ResourcesManager
											.getInstance().yellow_enemy_region) {
								@Override
								public void onDie() {
									addToScore(50);
								}

								
								
							};
							levelObject = enemy;
							enemies.put(enemy.get_body(), enemy);
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

		if (pSceneTouchEvent.isActionDown() && !firstTouch) {
			intro.setVisible(false);
			intro.detachSelf();
			firstTouch = true;
		}

		if (pSceneTouchEvent.isActionDown() && level == 4 && gameOverDisplayed) {
			SceneManager.getInstance().setLevel(5);
			SceneManager.getInstance().loadGameScene(engine);
		} else if (pSceneTouchEvent.isActionDown() && level < 8
				&& isLevelComplete) {
			SceneManager.getInstance().setLevel(level + 1);
			SceneManager.getInstance().loadGameScene(engine);
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

				if ((x2.getBody().getUserData().equals("blueEnemy")
						|| x2.getBody().getUserData().equals("redEnemy") || x2
						.getBody().getUserData().equals("yellowEnemy"))
						&& x1.getBody().getUserData().equals("player")) {
					x1.getBody().setUserData("player_dead");
				}

				// stone + player bullet;
				if (x1.getBody().getUserData().equals("stone")
						&& x2.getBody().getUserData().equals("player_bullet")) {

					x2.getBody().setUserData("player_bullet_deleted");

				}
				
				else if (x2.getBody().getUserData().equals("stone")
						&& x1.getBody().getUserData().equals("player_bullet")) {

					x1.getBody().setUserData("player_bullet_deleted");

				}

				// bullet & bullet
				else if (x1.getBody().getUserData().equals("player_bullet")
						&& x2.getBody().getUserData().equals("player_bullet")) {

					x1.getBody().setUserData("player_bullet_deleted");
					x2.getBody().setUserData("player_bullet_deleted");
				}

				// bullet & breakable
				else if (x1.getBody().getUserData().equals("breakable")
						&& x2.getBody().getUserData().equals("player_bullet")) {
					x1.getBody().setUserData("breakable_deleted");
					x2.getBody().setUserData("player_bullet_deleted");
				}
				
				
				else if (x2.getBody().getUserData().equals("breakable")
						&& x1.getBody().getUserData().equals("player_bullet")) {
					x2.getBody().setUserData("breakable_deleted");
					x1.getBody().setUserData("player_bullet_deleted");
				}

				// bullet & redEnemy
				else if (x1.getBody().getUserData().equals("redEnemy")
						&& x2.getBody().getUserData().equals("player_bullet")) {
					x2.getBody().setUserData("player_bullet_deleted");
					Enemy e = (Enemy) enemies.get(x1.getBody());
					float health = e.get_health();
					// TODO: shop feature: change 1 to player attack value later
					if (health > 1) {
						e.set_health(health - 1);
					} else {
						x1.getBody().setUserData("red_enemy_deleted");
					}
				}

				
				else if (x2.getBody().getUserData().equals("redEnemy")
						&& x1.getBody().getUserData().equals("player_bullet")) {
					x1.getBody().setUserData("player_bullet_deleted");
					Enemy e = (Enemy) enemies.get(x2.getBody());
					float health = e.get_health();
					// TODO: shop feature: change 1 to player attack value later
					if (health > 1) {
						e.set_health(health - 1);
					} else {
						x2.getBody().setUserData("red_enemy_deleted");
					}
				}
				
				// bullet & blueEnemy
				else if (x1.getBody().getUserData().equals("blueEnemy")
						&& x2.getBody().getUserData().equals("player_bullet")) {
					x1.getBody().setUserData("blue_enemy_deleted");
					x2.getBody().setUserData("player_bullet_deleted");
				}

				// bullet & blueEnemy
				else if (x2.getBody().getUserData().equals("blueEnemy")
						&& x1.getBody().getUserData().equals("player_bullet")) {
					x2.getBody().setUserData("blue_enemy_deleted");
					x1.getBody().setUserData("player_bullet_deleted");
				}

				// bullet & yellowEnemy
				else if (x1.getBody().getUserData().equals("yellowEnemy")
						&& x2.getBody().getUserData().equals("player_bullet")) {
					x2.getBody().setUserData("player_bullet_deleted");
					Enemy e = (Enemy) enemies.get(x1.getBody());
					float health = e.get_health();
					// TODO: shop feature: change 1 to player attack value later
					if (health > 1) {
						e.set_health(health - 1);
					} else {
						x1.getBody().setUserData("yellow_enemy_deleted");
					}
				}
				
				else if (x2.getBody().getUserData().equals("yellowEnemy")
						&& x1.getBody().getUserData().equals("player_bullet")) {
					x1.getBody().setUserData("player_bullet_deleted");
					Enemy e = (Enemy) enemies.get(x2.getBody());
					float health = e.get_health();
					// TODO: shop feature: change 1 to player attack value later
					if (health > 1) {
						e.set_health(health - 1);
					} else {
						x2.getBody().setUserData("yellow_enemy_deleted");
					}
				}
				

				// bullet & yellowEnemy
				else if (x1.getBody().getUserData().equals("boss")
						&& x2.getBody().getUserData().equals("player_bullet")) {
					x2.getBody().setUserData("player_bullet_deleted");
					Enemy e = (Enemy) enemies.get(x1.getBody());
					float health = e.get_health();
					// TODO: shop feature: change 1 to player attack value later
					if (health > 1) {
						e.set_health(health - 1);
					} else {
						x1.getBody().setUserData("boss_deleted");
					}
				}
				
				else if (x2.getBody().getUserData().equals("boss")
						&& x1.getBody().getUserData().equals("player_bullet")) {
					x1.getBody().setUserData("player_bullet_deleted");
					Enemy e = (Enemy) enemies.get(x2.getBody());
					float health = e.get_health();
					// TODO: shop feature: change 1 to player attack value later
					if (health > 1) {
						e.set_health(health - 1);
					} else {
						x2.getBody().setUserData("boss_deleted");
					}
				}

				//player bullet with base
				else if (x2.getBody().getUserData().equals("base")
						&& x1.getBody().getUserData().equals("player_bullet")) {
					x1.getBody().setUserData("player_bullet_deleted");
					
				}
				
				else if (x1.getBody().getUserData().equals("base")
						&& x2.getBody().getUserData().equals("player_bullet")) {
					x2.getBody().setUserData("player_bullet_deleted");
					
				}
				
				//player bullet & coin
				else if (x1.getBody().getUserData().equals("coin")
						&& x2.getBody().getUserData().equals("player_bullet")) {
					x2.getBody().setUserData("player_bullet_deleted");
				}
				
				else if (x2.getBody().getUserData().equals("coin")
						&& x1.getBody().getUserData().equals("player_bullet")) {
					x1.getBody().setUserData("player_bullet_deleted");
				}
				
				//player bullet & enemy bullet
				else if (x2.getBody().getUserData().equals("enemy_bullet")
						&& x1.getBody().getUserData().equals("player_bullet")) {
					x1.getBody().setUserData("player_bullet_deleted");
					x2.getBody().setUserData("enemy_bullet_deleted");
				}
				
				else if (x1.getBody().getUserData().equals("enemy_bullet")
						&& x2.getBody().getUserData().equals("player_bullet")) {
					x2.getBody().setUserData("player_bullet_deleted");
					x1.getBody().setUserData("enemy_bullet_deleted");
				}
				
				
				// enemy bullet with enemy
				else if ((x2.getBody().getUserData().equals("blueEnemy")
						|| x2.getBody().getUserData().equals("redEnemy") || x2
						.getBody().getUserData().equals("yellowEnemy")||x2.getBody().getUserData().equals("boss"))
						&& x1.getBody().getUserData().equals("enemy_bullet")) {
					//x1.getBody().setUserData("enemy_bullet_deleted");
				}
				
				else if ((x1.getBody().getUserData().equals("blueEnemy")
						|| x1.getBody().getUserData().equals("redEnemy") || x1
						.getBody().getUserData().equals("yellowEnemy")||x1.getBody().getUserData().equals("boss"))
						&& x2.getBody().getUserData().equals("enemy_bullet")) {
					//x2.getBody().setUserData("enemy_bullet_deleted");
				}

				// enemy bullet & breakable
				else if (x1.getBody().getUserData().equals("breakable")
						&& x2.getBody().getUserData().equals("enemy_bullet")) {
					x1.getBody().setUserData("breakable_deleted");
					x2.getBody().setUserData("enemy_bullet_deleted");
				}
				
				else if (x2.getBody().getUserData().equals("breakable")
						&& x1.getBody().getUserData().equals("enemy_bullet")) {
					x2.getBody().setUserData("breakable_deleted");
					x1.getBody().setUserData("enemy_bullet_deleted");
				}


				// enemy bullet & unbreakable stone
				else if (x1.getBody().getUserData().equals("stone")
						&& x2.getBody().getUserData().equals("enemy_bullet")) {

					x2.getBody().setUserData("enemy_bullet_deleted");
				}
				
				else if (x2.getBody().getUserData().equals("stone")
						&& x1.getBody().getUserData().equals("enemy_bullet")) {

					x1.getBody().setUserData("enemy_bullet_deleted");
				}
				

				
				// enemy bullet & flag
				else if (x1.getBody().getUserData().equals("flag")
						&& x2.getBody().getUserData().equals("enemy_bullet")) {

					x2.getBody().setUserData("enemy_bullet_deleted");
				}
				
				else if (x2.getBody().getUserData().equals("flag")
						&& x1.getBody().getUserData().equals("enemy_bullet")) {

					x1.getBody().setUserData("enemy_bullet_deleted");
				}
				
				// enemy bullet & player
				else if (x1.getBody().getUserData().equals("player")
						&& x2.getBody().getUserData().equals("enemy_bullet")) {
					float health = player.getHealth();
					if (health > 1) {
						player.setHealth(health - 1);
					} else {
						x1.getBody().setUserData("player_dead");
					}
					x2.getBody().setUserData("enemy_bullet_deleted");

				}
				
				else if (x2.getBody().getUserData().equals("player")
						&& x1.getBody().getUserData().equals("enemy_bullet")) {
					float health = player.getHealth();
					if (health > 1) {
						player.setHealth(health - 1);
					} else {
						x2.getBody().setUserData("player_dead");
					}
					x1.getBody().setUserData("enemy_bullet_deleted");

				}
				
				// enemy bullet & base
				else if (x1.getBody().getUserData().equals("base")
						&& x2.getBody().getUserData().equals("enemy_bullet")) {
					if (base_health > 1) {
						base_health-=1;
					} else {
						player.getBody().setUserData("player_dead");
					}
					x2.getBody().setUserData("enemy_bullet_deleted");

				}
				
				else if (x2.getBody().getUserData().equals("base")
						&& x1.getBody().getUserData().equals("enemy_bullet")) {
					if (base_health > 1) {
						base_health-=1;
					} else {
						player.getBody().setUserData("player_dead");
					}
					x1.getBody().setUserData("enemy_bullet_deleted");

				}
			}

			public void endContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				
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

	public void player_shoot(float x, float y) {

		float xVel = 0, yVel = 0;
		int a = 0, b = 0;
		float px = x * x;
		float py = y * y;
		if (x >= 0 && y >= 0) {
			xVel = px / (px + py);
			yVel = py / (px + py);
			a = 1;
			b = 1;
		} else if (x >= 0 && y <= 0) {
			xVel = px / (px + py);
			yVel = -py / (px + py);
			a = 1;
			b = -1;
		} else if (x <= 0 && y <= 0) {
			xVel = -px / (px + py);
			yVel = -py / (px + py);
			a = -1;
			b = -1;
		} else if (x <= 0 && y >= 0) {
			xVel = -px / (px + py);
			yVel = py / (px + py);
			a = -1;
			b = 1;
		}
		if (x == 0) {
			a = 0;
		}
		if (y == 0) {
			b = 0;
		}
		Bullet bullet = new Bullet(player.getX() + 10 * xVel, player.getY() + 10
				* yVel, vbom, camera, physicsWorld, "player_bullet");
		attachChild(bullet);
		player_bullets.put(bullet.bullet_get_body(), bullet);
		bullet.bullet_get_body().setLinearVelocity(xVel * 20, yVel * 20);
	}
	
	public void enemy_shoot(float x, float y) {
		
		//Iterator it = enemies.entrySet().iterator(); 

		if(bulletCounter<60) { 
			bulletCounter++; 
		} else { 
			bulletCounter = 1; 
		}
		
		if(bulletCounter == 30) { 
		for(Enemy enemy : enemies.values()) { 
			//Body body = (Body) it.next(); 
			
			
			float xD, yD, xV, yV; 
			xD = x - enemy.getX(); 
			yD = y - enemy.getY(); 
			
			if(xD >= 0) { 
				xV = xD*xD/(xD*xD + yD*yD);
			} else { 
				xV = -xD*xD/(xD*xD + yD*yD);
			}
			
			if(yD >= 0) { 
				yV = yD*yD/(xD*xD + yD*yD); 
			} else { 
				yV = -yD*yD/(xD*xD + yD*yD); 
			}
			
			Bullet bullet = new Bullet(enemy.getX() + 10*xV, enemy.getY() + 10*yV, vbom, camera, physicsWorld, "enemy_bullet");
			attachChild(bullet);
			enemy_bullets.put(bullet.bullet_get_body(), bullet);
			bullet.bullet_get_body().setLinearVelocity(xV * 20, yV * 20);
		}}
			
	}

	public void delete_entity() {
		Iterator<Body> list = physicsWorld.getBodies();
		while (list.hasNext()) {
			Body currentBody = list.next();
			if (currentBody.getUserData().equals("player_dead")) {
				player.onDie();
				physicsWorld.unregisterPhysicsConnector(physicsWorld
						.getPhysicsConnectorManager()
						.findPhysicsConnectorByShape(player));
				physicsWorld.destroyBody(currentBody);
				detachChild(player);
				
			} else if (currentBody.getUserData()
					.equals("player_bullet_deleted")) {
				Bullet b = (Bullet) player_bullets.get(currentBody);
				player_bullets.remove(currentBody);
				physicsWorld.unregisterPhysicsConnector(physicsWorld
						.getPhysicsConnectorManager()
						.findPhysicsConnectorByShape(b));
				physicsWorld.destroyBody(currentBody);
				detachChild(b);
			}

			else if (currentBody.getUserData().equals("breakable_deleted")) {
				Sprite b = (Sprite) breakables.get(currentBody);
				breakables.remove(currentBody);
				physicsWorld.unregisterPhysicsConnector(physicsWorld
						.getPhysicsConnectorManager()
						.findPhysicsConnectorByShape(b));
				physicsWorld.destroyBody(currentBody);
				detachChild(b);
			}

			else if (currentBody.getUserData().equals("red_enemy_deleted")
					|| currentBody.getUserData().equals("blue_enemy_deleted")
					|| currentBody.getUserData().equals("yellow_enemy_deleted")||
					currentBody.getUserData().equals("boss_deleted")) {
				Enemy e = (Enemy) enemies.get(currentBody);
				physicsWorld.unregisterPhysicsConnector(physicsWorld
						.getPhysicsConnectorManager()
						.findPhysicsConnectorByShape(e));
				physicsWorld.destroyBody(currentBody);
				detachChild(e);
				enemies.remove(currentBody);
				e.onDie();
			}

			else if (currentBody.getUserData().equals("enemy_bullet_deleted")) {
				Bullet b = (Bullet) enemy_bullets.get(currentBody);
				enemy_bullets.remove(currentBody);
				physicsWorld.unregisterPhysicsConnector(physicsWorld
						.getPhysicsConnectorManager()
						.findPhysicsConnectorByShape(b));
				physicsWorld.destroyBody(currentBody);
				detachChild(b);
			}

		}
	}
	
	public boolean isLevelComplete() {
		return isLevelComplete;
	}
	
	public void deactivateControl() {
		analogLeftControl.setVisible(false);
		analogLeftControl.setIgnoreUpdate(true);
		analogRightControl.setVisible(false);
		analogLeftControl.setIgnoreUpdate(true);
	}
	
	public boolean isFirstTouch() {
		return firstTouch;
	}

}
