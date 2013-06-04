package Object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
public abstract class Enemy extends AnimatedSprite{
	private Body body;
	private int health;
	private boolean canShoot;
	// see the the player is in range so the enemy can chase/shoot
	private boolean inRange;
	
	public Enemy(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld, ITiledTextureRegion region)
	{	
		super(pX, pY, region, vbo);		
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
		//TODO
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