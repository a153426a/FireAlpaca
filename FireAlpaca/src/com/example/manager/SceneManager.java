package com.example.manager;

import java.util.Iterator;

import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import scene.GameScene;
import scene.LoadingScene;
import scene.MainMenuScene;
import scene.SplashScene;

import Object.Bullet;

import com.badlogic.gdx.physics.box2d.Body;
import com.example.base.BaseScene;

public class SceneManager {
	
	private BaseScene splashScene; 
	private BaseScene menuScene; 
	private GameScene gameScene; 
	private BaseScene loadingScene; 
	private BaseScene profileScene; 
	private int level = 1; 
	
	private static final SceneManager INSTANCE = new SceneManager(); 
	
	private SceneType currentSceneType = SceneType.SCENE_SPLASH; 
	
	private BaseScene currentScene; 
	
	private Engine engine = ResourcesManager.getInstance().engine; 
			
	public enum SceneType { 
		SCENE_SPLASH, 
		SCENE_MENU, 
		SCENE_GAME, 
		SCENE_LOADING, 
		SCENE_PROFILE,
	}
	
	public void setScene(BaseScene scene) { 
		
		engine.setScene(scene); 
		currentScene = scene; 
		currentSceneType = scene.getSceneType(); 
		
	}
	
	public void setScene(SceneType sceneType) { 
		
		switch (sceneType) { 
			
		case SCENE_SPLASH: 
			setScene(splashScene); 
			break; 
		case SCENE_MENU: 
			setScene(menuScene); 
			break; 
		case SCENE_GAME: 
			setScene(gameScene); 
			break; 
		case SCENE_LOADING: 
			setScene(loadingScene); 
			break; 
		case SCENE_PROFILE: 
			setScene(profileScene); 
			break; 
		
		}
		
	}
	
	public static SceneManager getInstance() { 
		
		return INSTANCE; 
		
	}
	
	public SceneType getCurrentSceneType() { 
		
		return currentSceneType; 
		
	}
	
	public BaseScene getCurrentScene() { 
		
		return currentScene; 
		
	}
	
	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
	{
	    ResourcesManager.getInstance().loadSplashScreen();
	    splashScene = new SplashScene();
	    currentScene = splashScene;
	    pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}
	
	private void disposeSplashScene()
	{
	    ResourcesManager.getInstance().unloadSplashScreen();
	    splashScene.disposeScene();
	    splashScene = null;
	}
	
	public void createMenuScene() {
		ResourcesManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
		loadingScene = new LoadingScene();
		SceneManager.getInstance().setScene(menuScene);
		disposeSplashScene();
	}
	
	public void loadGameScene(final Engine mEngine) {
		
		
		setScene(loadingScene); 
		ResourcesManager.getInstance().unloadMenuTextures(); 
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() { 
			
			public void onTimePassed(final TimerHandler pTimerHandler) { 
				
				mEngine.unregisterUpdateHandler(pTimerHandler); 
				ResourcesManager.getInstance().loadGameResources(); 
				gameScene = new GameScene();
				gameScene.registerUpdateHandler(new IUpdateHandler() {
			        @Override
			        public void reset() { }

			        @Override
			        public void onUpdate(final float pSecondsElapsed) {
			           
			        	gameScene.delete_entity();
			        }
			    });
				setScene(gameScene); 
				
			}
			
		}));
		
	}

	
	public void loadMenuScene(final Engine mEngine) { 
		
		setScene(loadingScene); 
		gameScene.disposeScene(); 
		ResourcesManager.getInstance().unloadGameTextures(); 
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() { 
			
			public void onTimePassed(final TimerHandler pTimerHandler) { 
				
				mEngine.unregisterUpdateHandler(pTimerHandler); 
				ResourcesManager.getInstance().loadMenuTextures(); 
				setScene(menuScene);
				
			}

		}));
		
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int lv) {
		level = lv;
	}
	
}
