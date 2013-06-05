package Object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scene.GameScene.Map;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.manager.ResourcesManager;
public abstract class Enemy extends AnimatedSprite{
	private Body body;
	private float health;
	private boolean canShoot;
	// see the the player is in range so the enemy can chase/shoot
	private boolean inRange;
	private Map[][] map;
	private Player player;
	
	public Enemy(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld, 
			ITiledTextureRegion region, Map[][] map, Player player)
	{	
		super(pX, pY, region, vbo);	
		String userData;
		if (region == ResourcesManager.getInstance().blue_enemy_region)
		{
			userData = "blueEnemy";
			health = 1;
		}
		else if (region == ResourcesManager.getInstance().red_enemy_region)
		{
			userData = "redEnemy";
			health = 5;
		}
		else {
			userData = "yellowEnemy";
			health = 3;}
		createPhysics(camera, physicsWorld, userData);
		this.map = map;
	}
	
	public abstract void onDie();
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld, String userData)
	{
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		body.setUserData(userData);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
			public void onUpdate(float pSecondsElapsed) 
			{
				super.onUpdate(pSecondsElapsed);
				//TODO: enemy AI
				enemyAI();
				
			}	
		});
		
	}
	
	//determine if the player is in range
	private boolean checkRange () 
	{
		
		return true;
	}
	
	//determine if the enemy is close enough to start shooting
	private boolean checkShoot()
	{
		//TODO
		return true;
	}
	
	//enemy action update 
	private void enemyAI()
	{
		//TODO
	}
	
	
	
	
	
	
	
}