package com.example.feijidazhan;

import multiSupport.ClientMessages;
import multiSupport.ClientMessages.AddPointClientMessage;
import multiSupport.ServerMessages;
import multiSupport.ServerMessages.AddPointServerMessage;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.andengine.ui.activity.BaseGameActivity;

import android.util.Log;
import android.view.KeyEvent;

import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;

public class GameActivity extends BaseGameActivity {
	
	private BoundCamera camera; 
	
	
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		
		camera = new BoundCamera(0, 0, 800, 480); 
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(800, 480), this.camera); 
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true); 
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON); 
		return engineOptions;
		
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
		
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager()); 
		ResourcesManager resourcesManager = ResourcesManager.getInstance(); 
		pOnCreateResourcesCallback.onCreateResourcesFinished();
		
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback); 
		
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {

		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
	    {
	            public void onTimePassed(final TimerHandler pTimerHandler) 
	            {
	                mEngine.unregisterUpdateHandler(pTimerHandler);
	                SceneManager.getInstance().createLoginScene();
	            }
	    }));
	    pOnPopulateSceneCallback.onPopulateSceneFinished();
		
	}
	
	@Override 
	public Engine onCreateEngine(EngineOptions pEngineOptions) { 
		
		return new LimitedFPSEngine(pEngineOptions, 60); 
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if (this.isGameLoaded())
		{
			System.exit(0);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
		}
		return false;
	}
	


}
