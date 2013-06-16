package Object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import scene.GameScene;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;

public class Bullet extends Sprite {
	
	private Body body;
	private PhysicsWorld pw;

	public Bullet(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld, String userData, ITextureRegion region) {
		super(pX, pY, region, vbom);
		
		createPhysics(camera, physicsWorld, userData);
		this.setScale(0.8f);
		this.pw = physicsWorld;
		// TODO Auto-generated constructor stub
	}

	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld, String userData)
	{
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		body.setUserData(userData);
		body.isBullet();

		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
			public void onUpdate(float pSecondsElapsed) 
			{
	            super.onUpdate(pSecondsElapsed);
				
			}	
		});
		
	}
	
	
	
	
	public void bullet_destroy() {
		this.setVisible(false);
		this.setIgnoreUpdate(true);
		this.detachSelf();
		
	}
	
	public PhysicsWorld bullet_get_pw() {
		return pw;
	}
	
	public Body bullet_get_body() {
		return body;
	}
	
	
}
