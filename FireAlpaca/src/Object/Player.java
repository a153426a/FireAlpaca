package Object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.manager.ResourcesManager;


public abstract class Player extends AnimatedSprite {
	
	private Body body; 
	public abstract void onDie();
	private boolean canRun; 
	private boolean stop;
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld) {
		
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

	    body.setUserData("player");
	    body.setFixedRotation(true);
	    
	    physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	    	
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	        	
	            super.onUpdate(pSecondsElapsed);
	            camera.onUpdate(0.1f);
	            
	            if (getY() <= 0 || getX() <=0) {                    
	                onDie();
	            } 
	            
	            if (canRun) {    
	                body.setLinearVelocity(new Vector2(5, body.getLinearVelocity().y)); 
	            }
	            
	            if (stop) { 
	            	body.setLinearVelocity(new Vector2(0, 0)); 
	            }
	            
	        }
	        
	    });
	    
	}
	
	public void setRunning() {
		
	    canRun = true;
	        
	    final long[] PLAYER_ANIMATE = new long[] { 20, 20, 20 };
	        
	    animate(PLAYER_ANIMATE, 0, 2, true);
	    
	}
	
	public void stopRunning() { 
		
		canRun = false; 
		stop = true;
		
	}
	
	public void jump()
	{
	    body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 12)); 
	}

	protected float getLocationX()
	{
		return getX();
	}
	
	protected float getLocationY()
	{
		return getY();
	}
	
	public Body getBody() { 
		
		return body;
		
	}
	
}

