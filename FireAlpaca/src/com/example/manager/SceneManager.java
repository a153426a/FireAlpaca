package com.example.manager;

import java.util.Iterator;

import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import scene.GameScene;
import scene.LoadingScene;
import scene.LoginScene;
import scene.MainMenuScene;
import scene.MultiScene;
import scene.RegisterScene;
import scene.SelectorScene;
import scene.ShopScene;
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
	private BaseScene loginScene;
	private BaseScene selectorScene;
	private BaseScene registerScene;
	private MultiScene multiScene;
	private BaseScene shopScene;
	private int level = 1; 
	private int maxLevel = 8;
	private int totalScore = 0; 
	private int totalCoin = 0;
	
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
		SCENE_LOGIN, 
		SCENE_SELECTOR,
		SCENE_REGISTER, SCENE_MULTI,
		SCENE_SHOP
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
			
		case SCENE_LOGIN:
			setScene(loginScene);
			break; 
		case SCENE_SELECTOR: 
			setScene(selectorScene);
		
		case SCENE_REGISTER:
			setScene(registerScene);
		
		case SCENE_MULTI:
			setScene(multiScene);
		case SCENE_SHOP:
			setScene(shopScene);
		
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
	
	public void createRegisterScene() {
		registerScene = new RegisterScene();
		currentScene = registerScene;
		SceneManager.getInstance().setScene(registerScene);
		
	}
	
	private void disposeRegisterScene() {
		registerScene.disposeScene();
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
	
	public void createLoginScene() { 
		ResourcesManager.getInstance().loadLoginScreen(); 
		loginScene = new LoginScene(); 
		currentScene = loginScene; 
		SceneManager.getInstance().setScene(loginScene);
		disposeSplashScene();
	}
	
	public void disposeLoginScene() { 
		ResourcesManager.getInstance().unloadLoginScene(); 
		loginScene.disposeScene();
	}
	
	public void createSelectorScene() { 
		ResourcesManager.getInstance().loadSelectorScene();
		selectorScene = new SelectorScene(); 
		SceneManager.getInstance().setScene(selectorScene); 
		ResourcesManager.getInstance().unloadMenuTextures(); 
	}
	
	
	public void createShopScene() { 
		ResourcesManager.getInstance().loadShopGraphics();
		shopScene = new ShopScene(); 
		SceneManager.getInstance().setScene(shopScene); 
		ResourcesManager.getInstance().unloadMenuTextures(); 
	}
	
	public void createMenuScene() {
		ResourcesManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
		loadingScene = new LoadingScene();
		SceneManager.getInstance().setScene(menuScene);
		disposeLoginScene();
		if (registerScene!=null) {
		disposeRegisterScene();}
	}
	
	public void loadGameScene(final Engine mEngine) {
		
		
		setScene(loadingScene); 
		ResourcesManager.getInstance().unloadSelectorAtlas();
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
			        	
			        	if (gameScene.isLevelComplete() || gameScene.isGameOver()) {
							gameScene.deactivateControl();
						}
			        	else {
			        	
						if(level == 2 || level == 6) { 
							gameScene.enemy_shoot(400, 40); 
						} else { 
							gameScene.enemy_shoot(gameScene.player.getX(), gameScene.player.getY()); 
						}
						
			        	gameScene.delete_entity();
			        }}
			    });
				setScene(gameScene); 
				
			}
			
		}));
		
	}

public void loadMultiScene(final Engine mEngine) {
		
		ResourcesManager.getInstance().unloadMenuTextures();
		setScene(loadingScene); 
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() { 
			
			public void onTimePassed(final TimerHandler pTimerHandler) { 
				
				mEngine.unregisterUpdateHandler(pTimerHandler); 
				ResourcesManager.getInstance().loadGameResources(); 
				multiScene = new MultiScene();
				multiScene.registerUpdateHandler(new IUpdateHandler() {
			        @Override
			        public void reset() { }

			        @Override
			        public void onUpdate(final float pSecondsElapsed) {
			        	
			        	if (multiScene.isLevelComplete()||multiScene.isGameOver()) {
							multiScene.deactivateControl();
						}
			        	else {
						
			        	multiScene.delete_entity();
			        }}
			    });
				setScene(multiScene); 
				
			}
			
		}));
		
	}

	
	public void loadLoginScene(final Engine mEngine) {
		
		registerScene.disposeScene();
		setScene(loginScene);
		
	}
	public void loadMenuScene(final Engine mEngine) { 
		
		setScene(loadingScene); 
		if (gameScene != null) 
			gameScene.disposeScene();
		else if (multiScene != null)
			multiScene.disposeScene();
		ResourcesManager.getInstance().unloadGameTextures(); 
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() { 
			
			public void onTimePassed(final TimerHandler pTimerHandler) { 
				
				mEngine.unregisterUpdateHandler(pTimerHandler); 
				ResourcesManager.getInstance().loadMenuTextures(); 
				setScene(menuScene);
				
			}

		}));
		
	}
	
	public void loadMenuSceneFromSelector(final Engine mEngine) { 
		
		ResourcesManager.getInstance().unloadSelectorAtlas();
		ResourcesManager.getInstance().loadMenuTextures();
		setScene(menuScene);
		
	}
	
	public void loadMenuSceneFromShop(final Engine mEngine) { 
		
		ResourcesManager.getInstance().unloadShopAtlas();
		ResourcesManager.getInstance().loadMenuTextures();
		setScene(menuScene);
		
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int lv) {
		level = lv;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public void setMaxLevel(int lv) {
		maxLevel = lv;
	}
	public int getTotalScore() {
		return totalScore;
	}
	
	public void incTotalScore(int sc) {
		totalScore += sc;
	}
	public int getTotalCoin() {
		return totalCoin;
	}
	
	public void incTotalCoin(int co) {
		totalCoin += co;
	}
}
