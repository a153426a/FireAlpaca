package Object;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scene.GameScene.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.manager.ResourcesManager;
public abstract class Enemy extends AnimatedSprite{
	private Body body;
	private float health;
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
		this.player = player;
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
				enemy_move(body);
				
			}	
		});
		
	}
	
	//determine if the player is in range
	private boolean checkRange () 
	{
		return (Math.abs(player.getX()-this.getX())<=80 ||Math.abs(player.getY()-this.getX())<=80);
	
	}
	
	//determine if the enemy is close enough to start shooting
	private boolean checkShoot()
	{	
		boolean result = true;
		int player_x = (int)(player.getX()-10)/20;
		int player_y = (int)(player.getY()-10)/20;
		int enemy_x = (int)(this.getX()-10)/20;
		int enemy_y = (int) (this.getY()-10)/20;
		int x_distance = Math.abs(player_x - enemy_x);
		int y_distance = Math.abs(player_y - enemy_y);
		
		
		if (y_distance != 0 && x_distance != 0) {
			result = false;
		}
		else if (y_distance == 0) {
			if (player_x > enemy_x) {
				for (int x = enemy_x+1; x <player_x; x++){
					if (map[x][enemy_y] == Map.STONE) {
						result = false;
					}
				}	
			}
			else {
				for (int x = player_x+1; x <enemy_x; x++){
					if (map[x][enemy_y] == Map.STONE) {
						result = false;
					}
				}	
			}
		}
		
		else if (x_distance ==0) {
			if (player_y > enemy_y) {
				for (int y = enemy_y+1; y <player_y; y++){
					if (map[enemy_x][y] == Map.STONE) {
						result = false;
					}
				}
			}
			else {
				for (int y = player_y+1; y <enemy_y; y++){
					if (map[enemy_x][y] == Map.STONE) {
						result = false;
					}
				}	
			}
		}
		return result;
	}
	
	private void enemy_shoot() 
	{
	}
	
	//enemy action update 
	private void enemy_move(Body body)
	{	
		
		if (checkRange()) 
		{
			if (checkShoot()) {
				enemy_shoot();
			}
	
		}
		/*if (enemy_collide()){
				//deal with collision
		}*/
		else {enemy_random_move(body);}
	}
	


	private void enemy_random_move(Body body) {
		int XVelocity; 
        int YVelocity;  
        Random randomGenerator = new Random();
        
        XVelocity = (int)randomGenerator.nextGaussian();
        YVelocity = (int)randomGenerator.nextGaussian();
        body.setLinearVelocity(new Vector2(XVelocity*3, YVelocity*3)); 
        

	}



	private boolean enemy_collide() {
		// TODO Auto-generated method stub
		return false;
	}


	
	
	
	
	
	
	
}