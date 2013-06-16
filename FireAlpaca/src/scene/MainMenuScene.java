package scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import com.example.base.BaseScene;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {
	
	private MenuScene menuChildScene;
	private final int MENU_SINGLE = 0;
	private final int MENU_MULTI = 1;
	private final int MENU_MUSIC = 2;
	private final int MENU_SHOP = 3;
	private final int MENU_HELP = 4;
	private final int MENU_LEADERBOARD = 5;

	@Override
	public void createScene(int lv) {
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	
	private void createBackground() {
		attachChild(new Sprite(400, 240, resourcesManager.menu_background_region, vbom)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}
	
	private void createMenuChildScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400, 240);
		
		final IMenuItem singleMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SINGLE, resourcesManager.single_region, vbom), 1f, 0.8f);
		final IMenuItem multiMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MULTI, resourcesManager.multi_region, vbom), 1f, 0.8f);
		final IMenuItem musicMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MUSIC, resourcesManager.music_region, vbom), 1f, 0.8f);
		final IMenuItem helpMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HELP, resourcesManager.help_region, vbom), 1f, 0.8f);
		final IMenuItem shopMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SHOP, resourcesManager.shop_region, vbom), 1f, 0.8f);
		final IMenuItem leaderboardMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_LEADERBOARD, resourcesManager.leaderboard_region, vbom), 1f, 0.8f);
		
		menuChildScene.addMenuItem(singleMenuItem);
		menuChildScene.addMenuItem(multiMenuItem);
		menuChildScene.addMenuItem(musicMenuItem);
		menuChildScene.addMenuItem(shopMenuItem);
		menuChildScene.addMenuItem(leaderboardMenuItem);
		menuChildScene.addMenuItem(helpMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		singleMenuItem.setPosition(-200, 0);
		multiMenuItem.setPosition(singleMenuItem.getX(), singleMenuItem.getY()-60);
		musicMenuItem.setPosition(singleMenuItem.getX()-160,singleMenuItem.getY()+220);
		helpMenuItem.setPosition(singleMenuItem.getX()-100, singleMenuItem.getY()+220);
		shopMenuItem.setPosition(-200, -120);
		leaderboardMenuItem.setPosition(-200, -180);
		musicMenuItem.setScale(0.8f);
		helpMenuItem.setScale(0.8f);
		singleMenuItem.setScale(0.8f);
		multiMenuItem.setScale(0.8f);
		shopMenuItem.setScale(0.8f);
		leaderboardMenuItem.setScale(0.8f);
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
 	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
		{
		case MENU_SINGLE:
			SceneManager.getInstance().createSelectorScene(); 
			return true;
		case MENU_MULTI:
			SceneManager.getInstance().loadMultiScene(engine); 
			return true;
		case MENU_MUSIC:
			return true;
		case MENU_HELP:
			this.activity.runOnUiThread(new Runnable() {
			    public void run() {
			    	msbox("How to Play","hello");
			    }
			});
			return true;
			
		case MENU_SHOP:
			SceneManager.getInstance().createShopScene();
			return true;
		case MENU_LEADERBOARD: 
			SceneManager.getInstance().createLeaderScene();
		default:
			return false;
		}
	}

}
