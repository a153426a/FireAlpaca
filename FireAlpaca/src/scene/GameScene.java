package scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
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

import Object.Enemy;
import Object.Player;

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

import extra.LevelCompleteWindow;
import extra.LevelCompleteWindow.StarsCount;

public class GameScene extends BaseScene implements IOnSceneTouchListener {
	
	private HUD gameHUD; 
	private PhysicsWorld physicsWorld; 
	private Text scoreText; 
	private int score = 0; 
	private boolean firstTouch = false;
	
	//game graphic fields
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
	private Player player;
	private List<Enemy> enemyList;
	
	//Enum for enemy AI
	public enum Map {
		BREAKABLE, STONE, COIN, BASE, FLAG, ENEMY
	}
	//(gigantic?) matrix field for enemy AI.
	public Map[][] map;
	
	private Text gameOverText;
	private LevelCompleteWindow levelCompleteWindow;
	private boolean gameOverDisplayed = false;

	@Override
	public void createScene() {
		enemyList = new ArrayList<Enemy>();
		createBackground(); 
		createHUD(); 
		createPhysics(); 
		loadLevel(1);
		setOnSceneTouchListener(this);
		createGameOverText();
		levelCompleteWindow = new LevelCompleteWindow(vbom);
		
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
		
		attachChild(new Sprite(400,240, resourcesManager.game_background_region, vbom)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
		
	}
	
	private void createHUD() { 
		
		gameHUD = new HUD(); 
		
		scoreText = new Text(20, 420, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
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
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL) 
		{   
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
			{
				final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
				final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
				
				camera.setBounds(0, 0, width, height); // here we set camera bounds
		        camera.setBoundsEnabled(true);
		        
				return GameScene.this;
			}
		});
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
		{
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
	        {
	            final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
	            final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
	            final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	            
	            final Sprite levelObject;
	            
	            if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_STONE))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.stone_region, vbom); 
	                PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF).setUserData("stone"); 
	                map[(x-10)/20][(y-10)/20] = Map.STONE;
	                
	            } 
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BREAKABLE))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.breakable_region, vbom);
	                final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, FIXTURE_DEF);
	                body.setUserData("breakable");
	                map[(x-10)/20][(y-10)/20] = Map.BREAKABLE;
	            }
	          
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_COIN))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.coin_region, vbom)
	                {
	                    @Override
	                    protected void onManagedUpdate(float pSecondsElapsed) 
	                    {
	                        super.onManagedUpdate(pSecondsElapsed);
	                        
	                        if (player.collidesWith(this)) { 
	                        	
	                            addToScore(100);
	                            this.setVisible(false);
	                            this.setIgnoreUpdate(true);
	                        }
	                        
	                    }
	                };
	                levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
	                map[(x-10)/20][(y-10)/20] = Map.COIN;
	            }
	            
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER))
	            {
	                player = new Player(x, y, vbom, camera, physicsWorld)
	                {
	                    @Override
	                    public void onDie() {
	                    	if (!gameOverDisplayed) {
	                    		this.stopRunning();
	                            displayGameOverText();
	                        }
	                    }
	                };
	                levelObject = player;
	            }
	            
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_LEVEL_COMPLETE))
	            {
	                levelObject = new Sprite(x, y, resourcesManager.complete_stars_region, vbom)
	                {
	                    @Override
	                    protected void onManagedUpdate(float pSecondsElapsed) 
	                    {
	                        super.onManagedUpdate(pSecondsElapsed);

	                        if (player.collidesWith(this))
	                        {
	                            levelCompleteWindow.display(StarsCount.TWO, GameScene.this, camera);
	                            this.setVisible(false);
	                            this.setIgnoreUpdate(true);
	                        }
	                    }
	                };
	                levelObject.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(1, 1, 1.3f)));
	                
	            }
	                     
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_RED_ENEMY))
	            {
	                levelObject = new Enemy(x, y, vbom, camera, physicsWorld, ResourcesManager.getInstance().red_enemy_region, map)
	                {
	                    @Override
	                    public void onDie() {
	                    	addToScore(100);
                            this.setVisible(false);
                            this.setIgnoreUpdate(true);
	                    	enemyList.remove(this);
	                    	map[(int) ((this.getX()-10)/20)][(int) ((this.getY()-10)/20)] = null;
	                    }
	                };
	                enemyList.add((Enemy)levelObject);
	                map[(x-10)/20][(y-10)/20] = Map.ENEMY;
	            }
	            
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_BLUE_ENEMY))
	            {
	                levelObject = new Enemy(x, y, vbom, camera, physicsWorld, ResourcesManager.getInstance().blue_enemy_region, map)
	                {
	                    @Override
	                    public void onDie() {
	                    	addToScore(30);
                            this.setVisible(false);
                            this.setIgnoreUpdate(true);
	                    	enemyList.remove(this);
	                    	map[(int) ((this.getX()-10)/20)][(int) ((this.getY()-10)/20)] = null;
	                    }
	                };
	                enemyList.add((Enemy)levelObject);
	                map[(x-10)/20][(y-10)/20] = Map.ENEMY;
	            }
	            
	            else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_YELLOW_ENEMY))
	            {
	                levelObject = new Enemy(x, y, vbom, camera, physicsWorld, ResourcesManager.getInstance().yellow_enemy_region, map)
	                {
	                    @Override
	                    public void onDie() {
	                    	addToScore(50);
                            this.setVisible(false);
                            this.setIgnoreUpdate(true);
	                    	enemyList.remove(this);
	                    	map[(int) ((this.getX()-10)/20)][(int) ((this.getY()-10)/20)] = null;
	                    }
	                };
	                enemyList.add((Enemy)levelObject);
	                map[(x-10)/20][(y-10)/20] = Map.ENEMY;
	            }
	            
	            else
	            {
	                throw new IllegalArgumentException();
	            }

	            levelObject.setCullingEnabled(true);

	            return levelObject;
	        }
	    });

	    levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
	 }
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		
		if (pSceneTouchEvent.isActionDown()) {
			
	        if (!firstTouch) {
	        	
	            player.setRunning();
	            firstTouch = true;
	            
	        } else {
	        	
	            player.jump();
	            
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
	
	private ContactListener contactListener()
	{
	    ContactListener contactListener = new ContactListener()
	    {
	        public void beginContact(Contact contact)
	        {
	            final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();
	            
	            
	            if (x1.getBody().getUserData().equals("stone") && x2.getBody().getUserData().equals("player"))
	            {
	                player.onDie();
	            }
	            
	            if (x1.getBody().getUserData().equals("breakable") && x2.getBody().getUserData().equals("player"))
	            {
	                engine.registerUpdateHandler(new TimerHandler(0.2f, new ITimerCallback()
	                {                                    
	                    public void onTimePassed(final TimerHandler pTimerHandler)
	                    {
	                    	pTimerHandler.reset();
	                        engine.unregisterUpdateHandler(pTimerHandler);
	                        player.onDie();
	                    }
	                }));
	            }
	            
	            
	            else if ((x1.getBody().getUserData().equals("blueEnemy")||
	            		x1.getBody().getUserData().equals("redEnemy")||x1.getBody().getUserData().equals("yellowEnemy"))
	            		&& x2.getBody().getUserData().equals("player"))
	            {
	            	player.onDie();
	            }
	            
	            if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
	            {
	                if (x2.getBody().getUserData().equals("player"))
	                {
	                    //TODO;
	                }
	            }
	            
	            if ((x1.getBody().getUserData().equals("blueEnemy")||
	            		x1.getBody().getUserData().equals("redEnemy")||x1.getBody().getUserData().equals("yellowEnemy"))
	            		&& x2.getBody().getUserData().equals("player"))
	            {
	            	player.onDie();
	            }
	            
	        }

	        public void endContact(Contact contact)
	        {
	            final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();

	            if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
	            {
	                if (x2.getBody().getUserData().equals("player"))
	                {
	                    //TODO;
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
	
}

