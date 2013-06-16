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
	private Sprite caonima2;
	private Text attack_text; 
	private Text health_text;
	private Text money_text; 
	private Text price1_text;
	private Text price2_text;
	private Text attackIntro_text; 
	private Text healthIntro_text;
	private int attackPoint; 
	private int healthPoint;
	private int attackGold; 
	private int healthGold; 
	private int totalGold;
	private boolean max; 

	@Override
	public void createScene(int lv) {
		setBackground(new Background(Color.BLACK));
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400, 240);
		attackPoint = 3;
		healthPoint = 30;
		attackGold = 100; 
		healthGold = 100;
		totalGold = 15000;
		max = false; 
		
		final IMenuItem buy1 = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_1, resourcesManager.buy_region, vbom), 1.2f, 1);
		final IMenuItem buy2 = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_2, resourcesManager.buy_region, vbom), 1.2f, 1);
		
		attack_text = new Text(150, 200, resourcesManager.font, "Attack: 0123456789", vbom);
		attack_text.setText("Attack:" + attackPoint);
		attack_text.setColor(Color.WHITE);
		health_text = new Text (150, 120, resourcesManager.font, "Health: 0123456789", vbom);
		health_text.setText("Health: " + healthPoint);
		health_text.setColor(Color.WHITE);
		money_text = new Text (170, 40, resourcesManager.font, "Money: 0123456789", vbom);
		money_text.setText("Money: " + totalGold); 
		money_text.setColor(Color.WHITE);
		price1_text = new Text (-100, -150, resourcesManager.font, "$ 0123456789", vbom);
		price1_text.setText("$ "+ attackGold);
		price1_text.setColor(Color.WHITE);
		price2_text = new Text (300, -150, resourcesManager.font, "$ 0123456789" + healthGold, vbom);
		price2_text.setText("$ "+ healthGold);
		price2_text.setColor(Color.WHITE);
		attackIntro_text = new Text (-150, -50, resourcesManager.font, "Fire\nGrass!", vbom);
		attackIntro_text.setScale(0.7f);
		healthIntro_text = new Text (250, -50, resourcesManager.font, "Gym\nGrass!", vbom);
		healthIntro_text.setScale(0.7f);
		
		caonima = new Sprite(-200, 120, resourcesManager.caonima_region, vbom) { 
			
			@Override 
			protected void preDraw(GLState pGLState, Camera pCamera) { 
				super.preDraw(pGLState, pCamera); 
				pGLState.enableDither(); 
			}
		};
		
		attack = new Sprite(-250, -50, resourcesManager.attack_region, vbom) { 
			
			@Override 
			protected void preDraw(GLState pGLState, Camera pCamera) { 
				super.preDraw(pGLState, pCamera); 
				pGLState.enableDither(); 
			}
		};
		
		health = new Sprite(150, -50, resourcesManager.health_region, vbom) { 
			
			@Override 
			protected void preDraw(GLState pGLState, Camera pCamera) { 
				super.preDraw(pGLState, pCamera); 
				pGLState.enableDither(); 
			}
		};
		
		caonima2 = new Sprite(-200, 120, resourcesManager.caonima2_region, vbom) { 
			
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
		menuChildScene.attachChild(attackIntro_text);
		menuChildScene.attachChild(healthIntro_text);
		menuChildScene.addMenuItem(buy1);
		menuChildScene.addMenuItem(buy2);
		menuChildScene.attachChild(money_text);
		menuChildScene.attachChild(price1_text);
		menuChildScene.attachChild(price2_text);
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		buy1.setPosition(-250, -150);
		buy2.setPosition(150,-150);
		
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
	
	private void incAttack() { 
		if(attackPoint < 19) { 
			if(totalGold >= attackGold) {
				attackPoint += 1;
				attack_text.setText("Attack: " + attackPoint);
				totalGold -= attackGold; 
				money_text.setText("Money: " + totalGold); 
				attackGold += 10; 
				price1_text.setText("$" + attackGold);
			}
		} 
		else if(attackPoint == 19){ 
			attackPoint += 1;
			attack_text.setText("Attack: " + attackPoint);
			totalGold -= attackGold; 
			money_text.setText("Money: " + totalGold); 
			attackGold += 10; 
			price1_text.setText("MAX!");
		}
	}
	
	private void incHealth() { 
		if(healthPoint < 190) {
			if(totalGold >= healthGold) {
				healthPoint += 10; 
				health_text.setText("Health: " + healthPoint); 
				totalGold -= healthGold; 
				money_text.setText("Money: " + totalGold); 
				healthGold += 10; 
				price2_text.setText("$" + healthGold);
			}
		} else if(healthPoint == 190){ 
			healthPoint += 10; 
			health_text.setText("Health: " + healthPoint); 
			totalGold -= healthGold; 
			money_text.setText("Money: " + totalGold); 
			healthGold += 10; 
			price2_text.setText("MAX!");
		}
	}
	
	private void checkMax() { 
		if(attackPoint == 20 && healthPoint == 200) { 
			caonima.detachSelf(); 
			menuChildScene.attachChild(caonima2);
			max=true;
		}
	}
	
	public int getAttack() { 
		return attackPoint; 
	}
	
	public int getHealth() { 
		return healthPoint;
	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
		{
		case MENU_1:
			incAttack();
			if(!max){
			checkMax();
			}
			return true;
		case MENU_2:
			incHealth();
			if(!max){ 
			checkMax();
			}
			
			return true;
		default:
			return false;
		}
	}
	


}
