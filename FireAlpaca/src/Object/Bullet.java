package Object;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.util.adt.color.Color;

import com.example.manager.ResourcesManager;

public class Bullet {

	public Rectangle sprite; 
	
	public Bullet(){ 
		sprite = new Rectangle(0, 0, 4, 4, ResourcesManager.getInstance().vbom);  
		sprite.setColor(Color.RED);
		sprite.setUserData("bullet");
	}
	
}
