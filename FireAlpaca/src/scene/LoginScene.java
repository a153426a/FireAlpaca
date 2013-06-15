package scene;

import java.sql.SQLException;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.text.Text;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.example.base.BaseScene;
import com.example.feijidazhan.R;
import com.example.manager.DatabaseManager;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

import extra.DialogCreater;
import extra.InputText;

public class LoginScene extends BaseScene implements IOnMenuItemClickListener{

	private MenuScene menuChildScene;
	private final int LOGIN = 0;
	private final int REGISTER = 1;
	
	InputText user;
	InputText password;
	
	@Override
	public void createScene(int lv) {
		setBackground(new Background(Color.BLACK));
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400, 240);
		
		final IMenuItem login = new ScaleMenuItemDecorator(new SpriteMenuItem(LOGIN, resourcesManager.login_region, vbom), 1.2f, 1);
		final IMenuItem register = new ScaleMenuItemDecorator(new SpriteMenuItem(REGISTER, resourcesManager.register_region, vbom), 1.2f, 1);
		
		Text user_text = new Text(-130, 100, resourcesManager.font, "User", vbom);
		user_text.setColor(Color.WHITE);
		user_text.setScale(0.7f);
		Text password_text = new Text (-180, 0, resourcesManager.font, "Password", vbom);
		password_text.setColor(Color.WHITE);
		password_text.setScale(0.7f);
		password = new InputText(0, 0, "Password", "Enter password (within 10 characters)", ResourcesManager.getInstance().password_region, resourcesManager.font, 80, 20, vbom,  resourcesManager.activity);
		user = new InputText(0, 100, "Username", "Enter User Name (within 10 characters)", ResourcesManager.getInstance().user_region, resourcesManager.font, 80, 20, vbom, resourcesManager.activity);
		password.setPassword(true);
		registerTouchArea(password);
		registerTouchArea(user);
		
		menuChildScene.attachChild(password_text);
		menuChildScene.attachChild(user_text);
		menuChildScene.addMenuItem(login);
		menuChildScene.addMenuItem(register);
		menuChildScene.attachChild(password);
		menuChildScene.attachChild(user);
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		login.setPosition(-100, -170);
		register.setPosition(login.getX() + 200, login.getY());
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
		
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOGIN;
	}

	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
		{
		case LOGIN:
			//if (user.getText()!= null &&password.getText()!=null){
			//if (DatabaseManager.getInstance().openConnection()) {
			//try {
			//	if (DatabaseManager.getInstance().login(user.getText(), password.getText())){
				SceneManager.getInstance().createMenuScene(); /*}
				else {
					this.activity.runOnUiThread(new Runnable() {
						public void run() {
							msbox("Error", "Username does not exist or wrong password combination");
						}
					});
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} }
			else {
				this.activity.runOnUiThread(new Runnable() {
					public void run() {
						msbox("Warning", "No Connection");
					}
				});
			}}
			else {
			
				this.activity.runOnUiThread(new Runnable() {
				    public void run() {
				    	msbox("Warning","Please enter your username and password");
				    }
				});
			}*/
			return true;
		case REGISTER:
			SceneManager.getInstance().createRegisterScene();
			return true;
		default:
		return false;
	}

}

	
	
	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	

}