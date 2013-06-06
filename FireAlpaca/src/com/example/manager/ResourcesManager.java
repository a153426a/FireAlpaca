package com.example.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.example.feijidazhan.GameActivity;

public class ResourcesManager {
	
	
	private static final ResourcesManager INSTANCE = new ResourcesManager(); 
	
	public Engine engine; 
	public GameActivity activity; 
	public BoundCamera camera; 
	public VertexBufferObjectManager vbom; 
	
	//splash scene fields
	public ITextureRegion splash_region; 
	private BitmapTextureAtlas splashTextureAtlas; 
	
	//main menu scene fields
	public ITextureRegion menu_background_region;
	public ITextureRegion single_region;
	public ITextureRegion multi_region;
	public ITextureRegion music_region;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	//loading scene font
	public Font font;
	
	
	//game scene texture regions
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	public ITextureRegion game_background_region;
	public ITextureRegion stone_region;
	public ITextureRegion breakable_region;
	public ITextureRegion coin_region;
	public ITextureRegion complete_window_region;
	
	//player region 
	public ITiledTextureRegion player_region;
	public ITiledTextureRegion complete_stars_region;
	
	//enemy region
	public ITiledTextureRegion red_enemy_region;
	public ITiledTextureRegion blue_enemy_region;
	public ITiledTextureRegion yellow_enemy_region;
	
	
	//on screen analog control
	public BitmapTextureAtlas analogControlTextureAtlas;
	public ITextureRegion analog_base_region;
	public ITextureRegion analog_knob_region;
	
	public void loadMenuResources() { 
		
		loadMenuGraphics(); 
		loadMenuAudio();
		loadMenuFonts();
	}

	public void loadGameResources() { 
		
		loadGameGraphics(); 
		loadGameFonts(); 
		loadGameAudio(); 
		
	}
	
	private void loadGameAudio() {
		// TODO Auto-generated method stub
		
	}

	private void loadGameFonts() {
		// TODO Auto-generated method stub
		
	}

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		
		game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "gamebackground.png");
		stone_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "stone.png");
		breakable_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "breakable.png");
		coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "coin.png");
		player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 3, 1); 

		red_enemy_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "red_enemy.png", 3, 1); 
		blue_enemy_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "blue_enemy.png", 3, 1);
		yellow_enemy_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "yellow_enemy.png", 3, 1); 
		

		complete_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "levelComplete.png");
		complete_stars_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "star.png", 2, 1);
		
		//analog control 
		analogControlTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		analog_base_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(analogControlTextureAtlas, activity, "analog_base.png", 0, 0);
		analog_knob_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(analogControlTextureAtlas, activity, "analog_knob.png", 128, 0);
		
		
		try
		{
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,1,0));
			this.gameTextureAtlas.load();
			
			//analog control
			this.analogControlTextureAtlas.load();
		}
		catch(final TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		
	}

	private void loadMenuAudio() {
		// TODO Auto-generated method stub
		
	}

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
		single_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "single.png");
		multi_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "multi.png");
		music_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "music.png");
		
		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		}
		catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
	}
	
	public void loadSplashScreen() { 
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/"); 
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR); 
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0); 
		splashTextureAtlas.load(); 
		
	}
	
	public void unloadSplashScreen() {
		
		splashTextureAtlas.unload(); 
		splash_region = null; 
		
	}
	
	public static void prepareManager(Engine engine, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom) { 
		getInstance().engine = engine; 
		getInstance().activity = activity; 
		getInstance().camera = camera; 
		getInstance().vbom = vbom; 
	}
	
	public static ResourcesManager getInstance() {
		
		return INSTANCE; 
	}
	
	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = FontFactory.create(activity.getFontManager(), mainFontTexture, 50, Color.WHITE);
		//font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.BLACK, 2, Color.WHITE);
		font.load();
	}
	
	public void unloadMenuTextures() { 
		
		menuTextureAtlas.unload(); 
		
	}
	
	public void loadMenuTextures() { 
		
		menuTextureAtlas.load();
		
	}
	
	public void unloadGameTextures() {
		
	}
	
}
