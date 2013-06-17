package scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import multiSupport.ClientMessages;
import multiSupport.MultiClient;
import multiSupport.MultiServer;
import multiSupport.ServerMessages;
import multiSupport.ClientMessages.AddPointClientMessage;
import multiSupport.ServerMessages.AddPointServerMessage;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouchController;
import org.andengine.opengl.util.GLState;
import org.andengine.util.SAXUtils;
import org.andengine.util.WifiUtils;
import org.andengine.util.WifiUtils.WifiUtilsException;
import org.andengine.util.adt.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import Object.Bullet;
import Object.Enemy;
import Object.Player;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.opengl.GLES20;
import android.widget.EditText;

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

public class MultiScene extends BaseScene implements IOnSceneTouchListener {
	private HUD gameHUD;
	public PhysicsWorld physicsWorld;
	public boolean isLevelComplete = false;
	private Rectangle health_bar;
	private Rectangle health_bar2;

	// game graphic fields
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";

	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_STONE = "stone";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BREAKABLE = "breakable";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE = "levelComplete";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER2 = "player2";
	public Player player;
	public Player player2;

	// many many many hashMaps
	public Map<Body, Bullet> player_bullets;
	private HashMap breakables;
	public Map<Body, Bullet> player2_bullets;


	private Text gameOverText1;
	private Text gameOverText2;
	private LevelCompleteWindow levelCompleteWindow;
	private boolean gameOverDisplayed = false;
	public int bulletCounter; 
	private boolean backKey = false;

	// analog on screen control
	private AnalogOnScreenControl analogLeftControl;
	private AnalogOnScreenControl analogRightControl;
	
	//multiplayer fields
	private static final String LOCALHOST_IP = "127.0.0.1";
	private MultiClient mClient;
	private MultiServer mServer;
	private static final int SERVER_PORT = 4444;
	private String mServerIP = LOCALHOST_IP;
	public static final int SERVER_ID = 0;
	public static final int CLIENT_ID = 1;
	private static final int DIALOG_CHOOSE_ENVIRONMENT_ID = 0;
	private static final int DIALOG_ENTER_SERVER_IP = DIALOG_CHOOSE_ENVIRONMENT_ID + 1;
	public MessagePool<IMessage> mMessagePool;
	
	
	@Override
	public void createScene(int lv) {
		//multi
		mMessagePool = new MessagePool<IMessage>();
		mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_ADD_POINT, AddPointServerMessage.class);
		mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_ADD_POINT, AddPointClientMessage.class);
		this.activity.runOnUiThread(new Runnable() {
			public void run() {
				createDialog();
			}
		});
		breakables = new HashMap();
		player_bullets = new HashMap<Body, Bullet>();
		player2_bullets = new HashMap<Body, Bullet>();
		engine.setTouchController(new MultiTouchController());
		createBackground();
		createLeftControl();
		createRightControl();
		createPhysics();
		loadLevel(9);
		createHUD();
		setOnSceneTouchListener(this);
		createGameOverText();
		levelCompleteWindow = new LevelCompleteWindow(vbom);
		loadPlayerData(); 
		
	}
	

	
	
	private void loadPlayerData() {
		if(mServer != null) { 
			player.setAttack(3); 
			player.setTHealth(30); 
			player.setHealth(player.getTHealth());
		} else if(mServer != null) { 
			player.setAttack(3); 
			player.setTHealth(30); 
			player.setHealth(player.getTHealth());
		}
		
	}




	public void createDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle("Choose server or client");
		dialog.setCancelable(false);
		dialog.setPositiveButton("Client (Yellow Alpaca)", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				enterIPDialog();
			
			}

			private void enterIPDialog() {
				final EditText et = new EditText(activity);
				AlertDialog.Builder ipDialog = new AlertDialog.Builder(activity);
				ipDialog.setCancelable(false);
				ipDialog.setView(et);
				ipDialog.setPositiveButton("Connect", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					MultiScene.this.mServerIP = et.getText().toString();
					mClient = new MultiClient(mServerIP, SERVER_PORT, engine, MultiScene.this);
					mClient.initClient();
					}
				});
				ipDialog.setNegativeButton("Back", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						createDialog();
						
					}
					
				});
				ipDialog.create().show();
			}
			
		});
		
		dialog.setNegativeButton("Server (White Alpaca)", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mServer = new MultiServer(SERVER_PORT, engine);
				mServer.initServer();
				serverIPDialog();
			}

			private void serverIPDialog() {
				AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
				dialog.setCancelable(false);
				try {
					dialog.setMessage("The IP of your Server is:\n" + WifiUtils.getWifiIPv4Address(activity));
					dialog.setPositiveButton(android.R.string.ok, null);
					dialog.create().show();
				} catch (WifiUtilsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		dialog.create().show();
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
						
						
						//multi
						if (mServer != null) {
							player.getBody().setLinearVelocity(pValueX * 3,
									pValueY * 3);
							AddPointServerMessage message = (AddPointServerMessage) MultiScene.this.mMessagePool.obtainMessage(ServerMessages.SERVER_MESSAGE_ADD_POINT);
							message.set(SERVER_ID, player.getX(), player.getY(), 0, 0, 0, backKey, player.getHealth(), player2.getHealth());
							mServer.sendMessage(message);
							MultiScene.this.mMessagePool.recycleMessage(message);
						}
						
						else if (mClient != null) {
							player2.getBody().setLinearVelocity(pValueX * 3,
									pValueY * 3);
							AddPointClientMessage message = (AddPointClientMessage) MultiScene.this.mMessagePool.obtainMessage(ClientMessages.CLIENT_MESSAGE_ADD_POINT);
							message.set(CLIENT_ID, player2.getX(), player2.getY(), 0, 0, 0, backKey, player.getHealth(), player2.getHealth());
							mClient.sendMessage(message);
							MultiScene.this.mMessagePool.recycleMessage(message);
						}
						
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
							
							float px = pValueX * pValueX;
							float py = pValueY * pValueY; 
							float xVel, yVel; 
							
							if(pValueX >= 0) { 
								xVel = px / (px + py);
							} else { 
								xVel = -px / (px + py);
							}
							
							if(pValueY >= 0) { 
								yVel = py / (px + py);
							} else { 
								yVel = -py / (px + py);
							}
							
							//multi
							if (mServer != null) {
								player_shoot(xVel, yVel);
								AddPointServerMessage message = (AddPointServerMessage) MultiScene.this.mMessagePool.obtainMessage(ServerMessages.SERVER_MESSAGE_ADD_POINT);
								message.set(SERVER_ID, player.getX(), player.getY(), 1, xVel, yVel, backKey, player.getHealth(), player2.getHealth());
								mServer.sendMessage(message);
								MultiScene.this.mMessagePool.recycleMessage(message);
							}
							
							else if (mClient != null) {
								player2_shoot(xVel, yVel);
								AddPointClientMessage message = (AddPointClientMessage) MultiScene.this.mMessagePool.obtainMessage(ClientMessages.CLIENT_MESSAGE_ADD_POINT);
								message.set(CLIENT_ID, player2.getX(), player2.getY(), 1, xVel, yVel, backKey, player.getHealth(), player2.getHealth());
								mClient.sendMessage(message);
								MultiScene.this.mMessagePool.recycleMessage(message);
							}
							
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
		backKey = true;
		if (this.mClient !=null) 
			this.mClient.terminate();
		if (this.mServer != null) 
			this.mServer.terminate();
		SceneManager.getInstance().setLevel(1);
		SceneManager.getInstance().loadMenuScene(engine);

	}

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return SceneType.SCENE_MULTI;
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
		camera.setHUD(gameHUD);
		
		Text healthText = new Text(100, 460, resourcesManager.font, "White:", vbom);
		healthText.setScale(0.7f);
		Text healthText2 = new Text(510, 460, resourcesManager.font, "Yellow:", vbom);
		healthText2.setScale(0.7f);
		health_bar = new Rectangle(180, 470, player.getHealth()/player.total_health*200, 10, vbom);
		health_bar.setAnchorCenterX(0);
		health_bar.setColor(Color.RED);
		
		health_bar2 = new Rectangle(580, 470, player2.getHealth()/player2.total_health*200, 10, vbom);
		health_bar2.setAnchorCenterX(0);
		health_bar2.setColor(Color.GREEN);
		
		
		attachChild(healthText);
		attachChild(health_bar);
		attachChild(healthText2);
		attachChild(health_bar2);

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

						return MultiScene.this;
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
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER)) {
							player = new Player(x, y, vbom, camera,
									physicsWorld, ResourcesManager.getInstance().player_region) {
								// TODO
								@Override
								public void onDie() {
									if(!gameOverDisplayed) { 
										displayGameOverText("Yellow"); 
									}
								}

							};
							levelObject = player;
						} 
						
						else if (type
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER2)) {
							player2 = new Player(x, y, vbom, camera,
									physicsWorld, ResourcesManager.getInstance().player2_region) {
								@Override
								public void onDie() {
									if(!gameOverDisplayed) { 
										displayGameOverText("White"); 
									}
								}

							};
							levelObject = player2;
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
									
									
									
									
								}

							};
							levelObject
									.registerEntityModifier(new LoopEntityModifier(
											new ScaleModifier(1, 1, 1.3f)));

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


		if (pSceneTouchEvent.isActionDown()) {
			
		}

		return false;

	}

	private void createGameOverText() {

		gameOverText1 = new Text(0, 0, resourcesManager.font, "The White Alpaca Wins!", vbom);
		gameOverText2 = new Text(0, 0, resourcesManager.font, "The Yellow Alpaca Wins!", vbom);
	}

	private void displayGameOverText(String s) {
		
		if (s.equals("White")) {
			gameOverText1.setPosition(camera.getCenterX(), camera.getCenterY());
			attachChild(gameOverText1);
		}
		else if (s.equals("Yellow")) {
			gameOverText2.setPosition(camera.getCenterX(), camera.getCenterY());
			attachChild(gameOverText2);
		}
		gameOverDisplayed = true;
		
	}

	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();

				

				// stone + player bullet;
				if (x1.getBody().getUserData().equals("stone")
						&& x2.getBody().getUserData().equals("player_bullet")) {

					x2.getBody().setUserData("player_bullet_deleted");

				}
				
				else if (x2.getBody().getUserData().equals("stone")
						&& x1.getBody().getUserData().equals("player_bullet")) {

					x1.getBody().setUserData("player_bullet_deleted");

				}

				// player bullet & player bullet
				else if (x1.getBody().getUserData().equals("player_bullet")
						&& x2.getBody().getUserData().equals("player_bullet")) {

					x1.getBody().setUserData("player_bullet_deleted");
					x2.getBody().setUserData("player_bullet_deleted");
				}

				// player bullet & player  breakable
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
				
				// p1 bullet & p2 bullet
				else if (x1.getBody().getUserData().equals("player_bullet")
						&& x2.getBody().getUserData().equals("player2_bullet")) {

					x1.getBody().setUserData("player_bullet_deleted");
					x2.getBody().setUserData("player2_bullet_deleted");
				}
				else if (x2.getBody().getUserData().equals("player_bullet")
						&& x1.getBody().getUserData().equals("player2_bullet")) {

					x2.getBody().setUserData("player_bullet_deleted");
					x1.getBody().setUserData("player2_bullet_deleted");
				}
				
				// p2 bullet & unbreakable stone
				if (x1.getBody().getUserData().equals("stone")
						&& x2.getBody().getUserData().equals("player2_bullet")) {
					x2.getBody().setUserData("player2_bullet_deleted");

				}
				
				else if (x2.getBody().getUserData().equals("stone")
						&& x1.getBody().getUserData().equals("player2_bullet")) {

					x1.getBody().setUserData("player2_bullet_deleted");
				}
				
				// p2 bullet & breakable stone
				else if (x1.getBody().getUserData().equals("breakable")
						&& x2.getBody().getUserData().equals("player2_bullet")) {
					x1.getBody().setUserData("breakable_deleted");
					x2.getBody().setUserData("player2_bullet_deleted");
				}
				
				
				else if (x2.getBody().getUserData().equals("breakable")
						&& x1.getBody().getUserData().equals("player2_bullet")) {
					x2.getBody().setUserData("breakable_deleted");
					x1.getBody().setUserData("player2_bullet_deleted");
				}
				
				// p2 bullet & p2 bullet
				else if (x1.getBody().getUserData().equals("player2_bullet")
						&& x2.getBody().getUserData().equals("player2_bullet")) {

					x1.getBody().setUserData("player2_bullet_deleted");
					x2.getBody().setUserData("player2_bullet_deleted");
				}
				
				
				
				
					
					// p1 bullet & p2
				 if (x1.getBody().getUserData().equals("player2")
							&& x2.getBody().getUserData().equals("player_bullet")) {
					 x2.getBody().setUserData("player_bullet_deleted");
						float health = player2.getHealth();
						if (health > 1) {
							player2.setHealth(health - player.getAttack());
							health_bar2.setWidth(player2.getHealth()/player.total_health*200);
						} else {
							player2.setHealth(0);
							x1.getBody().setUserData("player2_dead");
						}
						x2.getBody().setUserData("player_bullet_deleted");

					}
					
					else if (x2.getBody().getUserData().equals("player2")
							&& x1.getBody().getUserData().equals("player_bullet")) {
						x1.getBody().setUserData("player_bullet_deleted");
						float health = player2.getHealth();
						if (health > 1) {
							player2.setHealth(health - player.getAttack());
							health_bar2.setWidth(player2.getHealth()/player.total_health*200);
						} else {
							player2.setHealth(0);
							x2.getBody().setUserData("player2_dead");
						}
						

					}
					
					// p2 bullet & p1
					else if (x1.getBody().getUserData().equals("player")
							&& x2.getBody().getUserData().equals("player2_bullet")) {
						x2.getBody().setUserData("player2_bullet_deleted");
						float health = player.getHealth();
						if (health > 1) {
							player.setHealth(health - player2.getAttack());
							health_bar.setWidth(player.getHealth()/player.total_health*200);
						} else {
							player.setHealth(0);
							x1.getBody().setUserData("player_dead");
						}

					}
					
					else if (x2.getBody().getUserData().equals("player")
							&& x1.getBody().getUserData().equals("player2_bullet")) {
						x1.getBody().setUserData("player2_bullet_deleted");
						float health = player.getHealth();
						if (health > 1) {
							player.setHealth(health - player2.getAttack());
							health_bar.setWidth(player.getHealth()/player.total_health*200);
						} else {
							player.setHealth(0);
							x2.getBody().setUserData("player_dead");
						}

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

		Bullet bullet = new Bullet(player.getX() + 10 * x, player.getY() + 10
				* y, vbom, camera, physicsWorld, "player_bullet", ResourcesManager.getInstance().bullet_region);
		attachChild(bullet);
		player_bullets.put(bullet.bullet_get_body(), bullet);
		bullet.bullet_get_body().setLinearVelocity(x * 20, y * 20);
	}
	
	public void player2_shoot(float x, float y) {

		
		Bullet bullet = new Bullet(player2.getX() + 10 * x, player2.getY() + 10
				* y, vbom, camera, physicsWorld, "player2_bullet", ResourcesManager.getInstance().bullet2_region);
		attachChild(bullet);
		player2_bullets.put(bullet.bullet_get_body(), bullet);
		bullet.bullet_get_body().setLinearVelocity(x * 20, y * 20);
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
				
			}
			else if (currentBody.getUserData()
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
			else if (currentBody.getUserData().equals("player2_dead")) {
				player2.onDie();
				physicsWorld.unregisterPhysicsConnector(physicsWorld
						.getPhysicsConnectorManager()
						.findPhysicsConnectorByShape(player2));
				physicsWorld.destroyBody(currentBody);
				detachChild(player2);
			} 
			
			else if (currentBody.getUserData()
					.equals("player2_bullet_deleted")) {
				Bullet b = (Bullet) player2_bullets.get(currentBody);
				player2_bullets.remove(currentBody);
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
		analogLeftControl.setIgnoreUpdate(true);

		for (Bullet bullet: player2_bullets.values()) {
			detachChild(bullet);
		}
		for (Bullet bullet: player_bullets.values()) {
			detachChild(bullet);
		}
	}
	
	public boolean isGameOver() {
		return gameOverDisplayed;
	}
	
}
