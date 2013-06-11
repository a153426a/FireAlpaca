package scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.text.Text;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;

import android.util.Log;

import com.example.base.BaseScene;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

import extra.InputText;

public class LoginScene extends BaseScene implements IOnMenuItemClickListener{

	private MenuScene menuChildScene;
	private final int LOGIN = 0;
	private final int REGISTER = 1;
	
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
		InputText password = new InputText(0, 0, "Password", "Enter password (within 10 characters)", ResourcesManager.getInstance().password_region, resourcesManager.font, 80, 20, vbom,  resourcesManager.activity);
		InputText user = new InputText(0, 100, "User", "Enter User Name (within 10 characters)", ResourcesManager.getInstance().user_region, resourcesManager.font, 80, 20, vbom, resourcesManager.activity);
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
			SceneManager.getInstance().createMenuScene(); 
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