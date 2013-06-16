package scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import com.example.base.BaseScene;
import com.example.manager.SceneManager;
import com.example.manager.SceneManager.SceneType;

public class LeaderScene extends BaseScene {
	
	private MenuScene menuChildScene;
	
	private String name1; 
	private int score1; 
	private String name2; 
	private int score2; 
	private String name3; 
	private int score3; 
	private Text first_text; 
	private Text second_text; 
	private Text thrid_text; 
	private Text firsts_text; 
	private Text seconds_text; 
	private Text thrids_text; 

	@Override
	public void createScene(int lv) {
		setBackground(new Background(Color.BLACK));
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(400, 240);
		name1 = "name1"; 
		score1 = 200; 
		name2 = "name2"; 
		score2 = 150; 
		name3 = "name3"; 
		score3 = 100; 
		first_text = new Text(-200, 120, resourcesManager.font, "1: " + name1, vbom); 
		second_text = new Text(-200, 0, resourcesManager.font, "2: " + name2, vbom);
		thrid_text = new Text(-200, -120, resourcesManager.font, "3: " + name3, vbom);
		firsts_text = new Text(200, 120, resourcesManager.font, ""+score1, vbom);
		seconds_text = new Text(200, 0, resourcesManager.font, ""+score2, vbom);
		thrids_text = new Text(200, -120, resourcesManager.font, ""+score3, vbom);
		
		menuChildScene.attachChild(first_text); 
		menuChildScene.attachChild(firsts_text); 
		menuChildScene.attachChild(second_text);
		menuChildScene.attachChild(seconds_text);
		menuChildScene.attachChild(thrid_text); 
		menuChildScene.attachChild(thrids_text);
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		setChildScene(menuChildScene);
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		SceneManager.getInstance().loadMenuSceneFromLeader(engine);
	}

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return SceneType.SCENE_LEADER;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

}
