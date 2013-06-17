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
import com.example.manager.ResourcesManager;


public abstract class Player extends AnimatedSprite {
	
	private Body body; 
	public abstract void onDie();
	private float health;
	public float total_health;
	private String userdata;
	public float attack;
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld, ITiledTextureRegion region) {
		
		super(pX, pY, region, vbo);
		if (region == ResourcesManager.getInstance().player_region) {
			userdata = "player";
		}
		else {
			userdata = "player2";
		}
		createPhysics(camera, physicsWorld);
		final long[] PLAYER_ANIMATE = new long[] { 10, 1, 10 };    
	    animate(PLAYER_ANIMATE, 0, 2, true);
	    health = 30;
	    total_health=health;
	    attack = 3;
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

	    body.setUserData(userdata);
	    body.setFixedRotation(true);
	    
	    physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
	    	
	        @Override
	        public void onUpdate(float pSecondsElapsed) {
	        	
	            super.onUpdate(pSecondsElapsed);
	            camera.onUpdate(0.1f);
	            
	           /* if (getY() <= 0 || getX() <=0) {                    
	                onDie();
	            } 
	            
	            if (canRun) {    
	                body.setLinearVelocity(new Vector2(5, body.getLinearVelocity().y)); 
	            }
	            
	            if (stop) { 
	            	body.setLinearVelocity(new Vector2(0, 0)); 
	            }*/
	            
	        }
	        
	    });
	    
	}

	
	public Body getBody() { 
		
		return body;
		
	} 
	
	public float getHealth() {
		return health;
	}
	
	public void setHealth(float health) {
		this.health = health; 
	}
	
	public float getAttack() {
		return attack;
	}
	public void setAttack(float a) { 
		attack = a;
	}
	
	public float getTHealth() { 
		return total_health;
	}
	
	public void setTHealth(float h) { 
		total_health = h;
	}
	
		/*GameScene scene = (GameScene) SceneManager.getInstance().getCurrentScene(); 
		
		Bullet b = BulletPool.shareBulletPool().obtainPoolItem(); 
		b.sprite.setPosition(getX(), getY()); 
		MoveModifier mod = new MoveModifier(10, b.sprite.getX(), b.sprite.getY(), b.sprite.getX()+2000*x, b.sprite.getY()+2000*y); 
		
		b.sprite.setVisible(true); 
		b.sprite.detachSelf(); 
		scene.attachChild(b.sprite); 
		scene.bulletList.add(b); 
		b.sprite.registerEntityModifier(mod); 
		
		scene.bulletCount++;*/
		
	
}

