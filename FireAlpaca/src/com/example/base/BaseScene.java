package com.example.base;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.app.Activity;

import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

public abstract class BaseScene extends Scene { 
	
	protected Engine engine; 
	protected Activity activity; 
	protected ResourcesManager resourcesManager; 
	protected VertexBufferObjectManager vbom; 
	protected BoundCamera camera; 
	protected SceneManager sceneManager;
	protected int level; 
	
	public BaseScene() { 
		
		this.resourcesManager = ResourcesManager.getInstance(); 
		this.sceneManager = SceneManager.getInstance();
		this.engine = resourcesManager.engine; 
		this.activity = resourcesManager.activity; 
		this.vbom = resourcesManager.vbom; 
		this.camera = resourcesManager.camera; 
		this.level = sceneManager.getLevel();
		createScene(level); 
		
	}
	
	public abstract void createScene(int lv); 
	
	public abstract void onBackKeyPressed(); 
	
	public abstract SceneType getSceneType(); 
	
	public abstract void disposeScene(); 
	
}
