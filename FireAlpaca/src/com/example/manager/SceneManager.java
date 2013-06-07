package com.example.manager;

import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

import scene.GameScene;
import scene.LoadingScene;
import scene.MainMenuScene;
import scene.SplashScene;

import com.example.base.BaseScene;

public class SceneManager {

	private BaseScene splashScene;
	private BaseScene menuScene;
	private BaseScene gameScene;
	private BaseScene loadingScene;
	private BaseScene profileScene;

	private static final SceneManager INSTANCE = new SceneManager();

	private SceneType currentSceneType = SceneType.SCENE_SPLASH;

	private BaseScene currentScene;

	private Engine engine = ResourcesManager.getInstance().engine;

	public enum SceneType {
		SCENE_SPLASH, SCENE_MENU, SCENE_GAME, SCENE_LOADING, SCENE_PROFILE,
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

	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		ResourcesManager.getInstance().loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}

	private void disposeSplashScene() {
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
		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {

					public void onTimePassed(final TimerHandler pTimerHandler) {

						mEngine.unregisterUpdateHandler(pTimerHandler);
						ResourcesManager.getInstance().loadGameResources();
						gameScene = new GameScene();
						setScene(gameScene);

					}

				}));

	}

	public void loadMenuScene(final Engine mEngine) {

		setScene(loadingScene);
		gameScene.disposeScene();
		ResourcesManager.getInstance().unloadGameTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f,
				new ITimerCallback() {

					public void onTimePassed(final TimerHandler pTimerHandler) {

						mEngine.unregisterUpdateHandler(pTimerHandler);
						ResourcesManager.getInstance().loadMenuTextures();
						setScene(menuScene);

					}

				}));

	}

}
