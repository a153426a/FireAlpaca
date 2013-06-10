package scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.example.base.BaseScene;
import com.example.manager.SceneManager.SceneType;

public class SplashScene extends BaseScene {
	
	private Sprite splash; 

	@Override
	public void createScene(int lv) {
		
		splash = new Sprite(0, 0, resourcesManager.splash_region, vbom) { 
			
			@Override 
			protected void preDraw(GLState pGLState, Camera pCamera) { 
				super.preDraw(pGLState, pCamera); 
				pGLState.enableDither(); 
			}
			
		};
		
		splash.setScale(0.8f); 
		splash.setPosition(400, 240); 
		attachChild(splash);
		
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public SceneType getSceneType() {
		
		return SceneType.SCENE_SPLASH; 
	
	}

	@Override
	public void disposeScene() {
		
		splash.detachSelf(); 
		splash.dispose(); 
		this.detachSelf(); 
		this.dispose(); 
		
	}

}
