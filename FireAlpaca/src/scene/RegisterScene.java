package scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import com.example.base.BaseScene;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

import extra.InputText;

public class RegisterScene extends BaseScene implements IOnMenuItemClickListener{


	private MenuScene menuChildScene;
	private final int REGISTER = 0;
	
	@Override
	public void createScene(int lv) {
		setBackground(new Background(Color.BLACK));
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400, 240);
		
		final IMenuItem register = new ScaleMenuItemDecorator(new SpriteMenuItem(REGISTER, resourcesManager.register_region, vbom), 1.2f, 1);
		
		Text user_text = new Text(-300, 100, resourcesManager.font, "User:", vbom);
		user_text.setColor(Color.WHITE);
		user_text.setScale(0.7f);
		Text firstname_text = new Text (-300, 0, resourcesManager.font, "Firstname:", vbom);
		firstname_text.setColor(Color.WHITE);
		firstname_text.setScale(0.7f);
		Text surname_text = new Text(-300, -100, resourcesManager.font, "Surname:", vbom);
		surname_text.setColor(Color.WHITE);
		surname_text.setScale(0.7f);
		Text email_text = new Text(100, 100, resourcesManager.font, "Email:", vbom);
		email_text.setColor(Color.WHITE);
		email_text.setScale(0.7f);
		Text password_text = new Text(80, 0, resourcesManager.font, "Password:", vbom);
		password_text.setColor(Color.WHITE);
		password_text.setScale(0.7f);
		
		
		InputText user = new InputText(-130, 100, "User", "Enter User Name (within 10 characters)", ResourcesManager.getInstance().user_region, resourcesManager.font, 80, 20, vbom, resourcesManager.activity);
		InputText surname = new InputText(-130, -100, "Surname", "Enter Your Surname (within 10 characters)", ResourcesManager.getInstance().user_region, resourcesManager.font, 80, 20, vbom,  resourcesManager.activity);
		InputText firstname = new InputText(-130, 0, "Firstname", "Enter Your Firstname (within 10 characters)", ResourcesManager.getInstance().user_region, resourcesManager.font, 80, 20, vbom, resourcesManager.activity);
		InputText email = new InputText(250, 100, "User", "Enter Youre Email (within 10 characters)", ResourcesManager.getInstance().user_region, resourcesManager.font, 80, 20, vbom, resourcesManager.activity);
		InputText password = new InputText(250, 0, "User", "Enter User Name (within 10 characters)", ResourcesManager.getInstance().user_region, resourcesManager.font, 80, 20, vbom, resourcesManager.activity);
		password.setPassword(true);
		registerTouchArea(password);
		registerTouchArea(user);
		registerTouchArea(surname);
		registerTouchArea(firstname);
		registerTouchArea(email);
		
		menuChildScene.attachChild(password_text);
		menuChildScene.attachChild(user_text);
		menuChildScene.attachChild(firstname_text);
		menuChildScene.attachChild(surname_text);
		menuChildScene.attachChild(email_text);
		menuChildScene.addMenuItem(register);
		menuChildScene.attachChild(password);
		menuChildScene.attachChild(user);
		menuChildScene.attachChild(firstname);
		menuChildScene.attachChild(surname);
		menuChildScene.attachChild(email);
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		register.setPosition(250, -100);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);		
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadLoginScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_REGISTER;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
		{
		case REGISTER:
			//Database thingy 
			SceneManager.getInstance().createMenuScene(); 
			return true;
		default:
		return false;
	}

	}
	
	
}
