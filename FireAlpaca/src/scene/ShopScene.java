package scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

import com.example.base.BaseScene;
import com.example.manager.ResourcesManager;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

import extra.InputText;

public class ShopScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menuChildScene;
	private final int MENU_1 = 0; 
	private final int MENU_2 = 1; 
	private Sprite xiaojinbi;
	private Sprite caonima;
	private Sprite attack;
	private Sprite health;
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createScene(int lv) {
		setBackground(new Background(Color.BLACK));
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400, 240);
		
		final IMenuItem buy1 = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_1, resourcesManager.buy_region, vbom), 1.2f, 1);
		final IMenuItem buy2 = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_2, resourcesManager.buy_region, vbom), 1.2f, 1);
		
		Text attack_text = new Text(100, 200, resourcesManager.font, "Attack: 20", vbom);
		attack_text.setColor(Color.WHITE);
		Text health_text = new Text (100, 120, resourcesManager.font, "Health: 10", vbom);
		health_text.setColor(Color.WHITE);
		Text money_text = new Text (100, 40, resourcesManager.font, "Money: 100", vbom);
		money_text.setColor(Color.WHITE);
		Text price1_text = new Text (-280, -200, resourcesManager.font, "$50", vbom);
		price1_text.setColor(Color.WHITE);
		Text price2_text = new Text (280, -200, resourcesManager.font, "$100", vbom);
		price2_text.setColor(Color.WHITE);
		
		caonima = new Sprite(-300, 150, resourcesManager.caonima_region, vbom) { 
			
			@Override 
			protected void preDraw(GLState pGLState, Camera pCamera) { 
				super.preDraw(pGLState, pCamera); 
				pGLState.enableDither(); 
			}
		};
		
		attack = new Sprite(-250, -100, resourcesManager.attack_region, vbom) { 
			
			@Override 
			protected void preDraw(GLState pGLState, Camera pCamera) { 
				super.preDraw(pGLState, pCamera); 
				pGLState.enableDither(); 
			}
		};
		
		health = new Sprite(250, -100, resourcesManager.health_region, vbom) { 
			
			@Override 
			protected void preDraw(GLState pGLState, Camera pCamera) { 
				super.preDraw(pGLState, pCamera); 
				pGLState.enableDither(); 
			}
		};
		
		menuChildScene.attachChild(attack);
		menuChildScene.attachChild(health);
		menuChildScene.attachChild(caonima);
		menuChildScene.attachChild(attack_text);
		menuChildScene.attachChild(health_text);
		menuChildScene.addMenuItem(buy1);
		menuChildScene.addMenuItem(buy2);
		menuChildScene.attachChild(money_text);
		menuChildScene.attachChild(price1_text);
		menuChildScene.attachChild(price2_text);
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		buy1.setPosition(-250, -200);
		buy2.setPosition(300,-100);
		
		setChildScene(menuChildScene);
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuSceneFromShop(engine);	}

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return SceneType.SCENE_SHOP;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	


}
