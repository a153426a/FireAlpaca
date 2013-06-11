package scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

import com.example.base.BaseScene;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

import org.andengine.engine.camera.Camera;

public class SelectorScene extends BaseScene implements
		IOnMenuItemClickListener {

	private MenuScene menuChildScene;
	private final int ONE = 0;
	private final int TWO = 1;
	private final int THREE = 2;
	private final int FOUR = 3;
	private final int FIVE = 4;
	private final int SIX = 5;
	private final int SEVEN = 6;
	private final int EIGHT = 7;

	@Override
	public void createScene(int level) {
		setBackground(new Background(Color.WHITE));
		// createBackground();
		createMenuChildScene();

	}

	private void createMenuChildScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400, 240);
		final IMenuItem oneMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(ONE, resourcesManager.one_region, vbom), 1f,
				1);
		final IMenuItem twoMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(TWO, resourcesManager.two_region, vbom), 1f,
				1);
		final IMenuItem threeMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(THREE, resourcesManager.three_region, vbom),
				1f, 1);
		final IMenuItem fourMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(FOUR, resourcesManager.four_region, vbom),
				1f, 1);
		final IMenuItem fiveMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(FIVE, resourcesManager.five_region, vbom),
				1f, 1);
		final IMenuItem sixMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(SIX, resourcesManager.six_region, vbom), 1f,
				1);
		final IMenuItem sevenMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(SEVEN, resourcesManager.seven_region, vbom),
				1f, 1);
		final IMenuItem eightMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(EIGHT, resourcesManager.eight_region, vbom),
				1f, 1);

		menuChildScene.addMenuItem(oneMenuItem);
		menuChildScene.addMenuItem(twoMenuItem);
		menuChildScene.addMenuItem(threeMenuItem);
		menuChildScene.addMenuItem(fourMenuItem);
		menuChildScene.addMenuItem(fiveMenuItem);
		menuChildScene.addMenuItem(sixMenuItem);
		menuChildScene.addMenuItem(sevenMenuItem);
		menuChildScene.addMenuItem(eightMenuItem);

		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		oneMenuItem.setPosition(-300, 120);
		twoMenuItem.setPosition(oneMenuItem.getX() + 200, oneMenuItem.getY());
		threeMenuItem.setPosition(twoMenuItem.getX() + 200, twoMenuItem.getY());
		fourMenuItem.setPosition(threeMenuItem.getX() + 200,
				threeMenuItem.getY());
		fiveMenuItem.setPosition(oneMenuItem.getX(), oneMenuItem.getY() - 240);
		sixMenuItem.setPosition(twoMenuItem.getX(), oneMenuItem.getY() - 240);
		sevenMenuItem.setPosition(threeMenuItem.getX(),
				oneMenuItem.getY() - 240);
		eightMenuItem
				.setPosition(fourMenuItem.getX(), oneMenuItem.getY() - 240);

		menuChildScene.setOnMenuItemClickListener(this);

		setChildScene(menuChildScene);
	}

	@Override
	public void onBackKeyPressed() {

		sceneManager.getInstance().loadMenuSceneFromSelector(engine);

	}

	@Override
	public SceneType getSceneType() {

		return SceneType.SCENE_SELECTOR;
	}

	private void createBackground() {
		attachChild(new Sprite(400, 240,
				resourcesManager.selector_background_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}

	@Override
	public void disposeScene() {

	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
		case ONE:
			SceneManager.getInstance().setLevel(1);
			SceneManager.getInstance().loadGameScene(engine);
			return true;
		case TWO:
			if (maxLevel >= 2) {
				SceneManager.getInstance().setLevel(2);
				SceneManager.getInstance().loadGameScene(engine);
			}
			return true;
		case THREE:
			if (maxLevel >= 3) {
				SceneManager.getInstance().setLevel(3);
				SceneManager.getInstance().loadGameScene(engine);
			}
			return true;
		case FOUR:
			if (maxLevel >= 4) {
				SceneManager.getInstance().setLevel(4);
				SceneManager.getInstance().loadGameScene(engine);
			}
			return true;
		case FIVE:
			if (maxLevel >= 5) {
				SceneManager.getInstance().setLevel(5);
				SceneManager.getInstance().loadGameScene(engine);
			}
			return true;
		case SIX:
			if (maxLevel >= 6) {
				SceneManager.getInstance().setLevel(6);
				SceneManager.getInstance().loadGameScene(engine);
			}
			return true;
		case SEVEN:
			if (maxLevel >= 7) {
				SceneManager.getInstance().setLevel(7);
				SceneManager.getInstance().loadGameScene(engine);
			}
			return true;
		case EIGHT:
			if (maxLevel == 8) {
				SceneManager.getInstance().setLevel(8);
				SceneManager.getInstance().loadGameScene(engine);
			}
			return true;
		default:
			return false;
		}
	}

}
