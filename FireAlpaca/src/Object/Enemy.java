package Object;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scene.GameScene;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;

import extra.CoolDown;
public abstract class Enemy extends AnimatedSprite{
	private Body body;
	private float health;
	public Enemy(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld, 
			ITiledTextureRegion region)
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
		else if(region == ResourcesManager.getInstance().yellow_enemy_region)
		{
			userData = "yellowEnemy";
			health = 3;
		} else { 
			userData = "boss"; 
			health = 100;
		}
		createPhysics(camera, physicsWorld, userData);
		final long[] ENEMY_ANIMATE = new long[] { 500, 500, 500 };
	    animate(ENEMY_ANIMATE, 0, 2, true);
		
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
				enemy_move();
			}	
		});
		
	}
	
	
	//enemy action update 
	private void enemy_move()
	{	

		if(SceneManager.getInstance().getLevel() == 2 || SceneManager.getInstance().getLevel() == 6) {
			float x, y, xV, yV; 
			x = 400 - getX(); 
			y = 40 - getY(); 
			if(x >= 0) { 
				xV = x*x/(x*x + y*y);  
			} else { 
				xV = -x*x/(x*x + y*y);  
			}
			
			if(y >= 0) { 
				yV = y*y/(x*x + y*y);
			} else { 
				yV = -y*y/(x*x + y*y);
			}
			
			body.setLinearVelocity(xV*4, yV*4);
		} else { 
			enemy_random_move(body);
		}
		
	}
	


	private void enemy_random_move(Body body) {
		int XVelocity; 
        int YVelocity;  
        Random randomGenerator = new Random();
        
        XVelocity = (int)randomGenerator.nextGaussian();
        YVelocity = (int)randomGenerator.nextGaussian();
        body.setLinearVelocity(new Vector2(XVelocity*5, YVelocity*5)); 
        

	}


	
	public Body get_body() {
		return body;
	}

	public float get_health() {
		return health;
	}
	
	public void set_health(float health) {
		this.health = health;
	}

	
	
	
	
	
	
	
}