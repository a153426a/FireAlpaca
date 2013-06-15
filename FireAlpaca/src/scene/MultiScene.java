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

	// game graphic fields
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";

	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_STONE = "stone";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BREAKABLE = "breakable";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE = "levelComplete";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER_2 = "player_2";
	public Player player;

	// many many many hashMaps
	private HashMap player_bullets;
	private HashMap breakables;


	private Text gameOverText;
	private LevelCompleteWindow levelCompleteWindow;
	private boolean gameOverDisplayed = false;
	public int bulletCounter; 

	// analog on screen control
	private AnalogOnScreenControl analogLeftControl;
	private AnalogOnScreenControl analogRightControl;
	
	//multiplayer fields
	private static final String LOCALHOST_IP = "127.0.0.1";
	private MultiClient mClient;
	private MultiServer mServer;
	private static final int SERVER_PORT = 4444;
	private String mServerIP = LOCALHOST_IP;
	private static final int SERVER_ID = 0;
	private static final int CLIENT_ID = 1;
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
		player_bullets = new HashMap();
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
		
	}

	
	public void createDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle("Choose server or client");
		dialog.setCancelable(false);
		dialog.setPositiveButton("Client", new OnClickListener() {

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
		
		dialog.setNegativeButton("Server", new OnClickListener() {

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
						player.getBody().setLinearVelocity(pValueX * 3,
								pValueY * 3);
						
						//multi
						if (mServer != null) {
							AddPointServerMessage message = (AddPointServerMessage) MultiScene.this.mMessagePool.obtainMessage(ServerMessages.SERVER_MESSAGE_ADD_POINT);
							message.set(SERVER_ID,pValueX, pValueY);
							mServer.sendMessage(message);
							MultiScene.this.mMessagePool.recycleMessage(message);
						}
						
						else if (mClient != null) {
							AddPointClientMessage message = (AddPointClientMessage) MultiScene.this.mMessagePool.obtainMessage(ClientMessages.CLIENT_MESSAGE_ADD_POINT);
							message.set(CLIENT_ID, pValueX, pValueY);
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
		
		Text healthText = new Text(510, 460, resourcesManager.font, "Health:", vbom);
		healthText.setScale(0.7f);
		health_bar = new Rectangle(580, 470, (player.getHealth()-1)/player.total_health*200, 10, vbom);
		health_bar.setAnchorCenterX(0);
		health_bar.setColor(Color.GREEN);
		
		attachChild(healthText);
		attachChild(health_bar);

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
								.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER_2)) {
							player = new Player(x, y, vbom, camera,
									physicsWorld) {
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
									health_bar.setWidth((player.getHealth()-1)/player.total_health*200);
									
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
	
}
