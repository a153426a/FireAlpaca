package com.example.base;

import java.sql.Connection;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

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
	protected int maxLevel;
	protected Connection c;
	
	public BaseScene() { 
		
		this.resourcesManager = ResourcesManager.getInstance(); 
		this.sceneManager = SceneManager.getInstance();
		this.engine = resourcesManager.engine; 
		this.activity = resourcesManager.activity; 
		this.vbom = resourcesManager.vbom; 
		this.camera = resourcesManager.camera; 
		this.level = sceneManager.getLevel();
		this.maxLevel = sceneManager.getMaxLevel();
		createScene(level); 
		
	}
	
	public abstract void createScene(int lv); 
	
	public abstract void onBackKeyPressed(); 
	
	public abstract SceneType getSceneType(); 
	
	public abstract void disposeScene();

	public void msbox(String str,String str2)
	{
	    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(activity);                      
	    dlgAlert.setTitle(str); 
	    dlgAlert.setMessage(str2); 
	    dlgAlert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	              
	        }
	   });
	    dlgAlert.setCancelable(true);
	    dlgAlert.create().show();
	}
	
}
