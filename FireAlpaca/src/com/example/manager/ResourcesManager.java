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
import org.andengine.opengl.texture.region.TiledTextureRegion;
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
	public BuildableBitmapTextureAtlas menuTextureAtlas;
	public ITextureRegion shop_region;
	public ITextureRegion help_region;
	public ITextureRegion leaderboard_region;
	
	//loading scene font
	public Font font;
	
	
	
	//game scene texture regions
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	public ITextureRegion game_background_region;
	public ITextureRegion stone_region;
	public ITextureRegion breakable_region;
	public ITextureRegion coin_region;
	public ITextureRegion complete_window_region;
	public ITextureRegion base_region; 
	public ITextureRegion flag_region;
	public ITextureRegion bullet_region;
	public ITextureRegion bullet2_region;
	
	//player region 
	public ITiledTextureRegion player2_region;
	public ITiledTextureRegion player_region;
	public ITiledTextureRegion complete_stars_region;
	
	//login region
	public BuildableBitmapTextureAtlas loginTextureAtlas;
	public TiledTextureRegion password_region;
	public TiledTextureRegion user_region;
	public ITextureRegion login_region;
	public ITextureRegion register_region;
	

	
	//enemy region
	public ITiledTextureRegion red_enemy_region;
	public ITiledTextureRegion blue_enemy_region;
	public ITiledTextureRegion yellow_enemy_region;
	public ITiledTextureRegion boss_region;
	
	//selector region 
	public ITextureRegion selector_background_region;
	public ITextureRegion one_region;
	public ITextureRegion two_region;
	public ITextureRegion three_region;
	public ITextureRegion four_region;
	public ITextureRegion five_region;
	public ITextureRegion six_region;
	public ITextureRegion seven_region;
	public ITextureRegion eight_region;
	private BuildableBitmapTextureAtlas selectorTextureAtlas;
	
	//on screen analog control
	public BitmapTextureAtlas analogControlTextureAtlas;
	public ITextureRegion analog_base_region;
	public ITextureRegion analog_knob_region;
	
	
	//shop region
	public BuildableBitmapTextureAtlas shopTextureAtlas;
	public ITextureRegion caonima_region;
	public ITextureRegion attack_region;
	public ITextureRegion health_region;
	public ITextureRegion xiaojinbi_region;
	public ITextureRegion buy_region;
	
	
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
	
	
	
	
	public void loadLoginScreen() {
		loadLoginGraphics();
		loadMenuFonts();
	}
	
	public void loadSelectorScene() {
		loadSelectorGraphics();
	}

	private void loadSelectorGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/select/");
		selectorTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		selector_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(selectorTextureAtlas, activity, "selector_background.png");
		one_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(selectorTextureAtlas, activity, "1.png");
		two_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(selectorTextureAtlas, activity, "2.png");
		three_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(selectorTextureAtlas, activity, "3.png");
		four_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(selectorTextureAtlas, activity, "4.png");
		five_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(selectorTextureAtlas, activity, "5.png");
		six_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(selectorTextureAtlas, activity, "6.png");
		seven_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(selectorTextureAtlas, activity, "7.png");
		eight_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(selectorTextureAtlas, activity, "8.png");
		
		try {
			this.selectorTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.selectorTextureAtlas.load();
		}
		catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}
	
	public void loadSelectorAtlas() {
		this.selectorTextureAtlas.load();
	}
	
	public void unloadSelectorAtlas() {
		this.selectorTextureAtlas.unload();
	}
	
	
	public void unloadShopAtlas() {
		this.shopTextureAtlas.unload();
	}
	private void loadLoginGraphics() {
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/login/");
		loginTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		user_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(loginTextureAtlas, activity, "blank.png", 1, 1);
		password_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(loginTextureAtlas, activity, "blank.png", 1, 1);
		login_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loginTextureAtlas, activity, "login.png");
		register_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loginTextureAtlas, activity, "register.png");
		
		try {
			this.loginTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.loginTextureAtlas.load();
		}
		catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}

		
	}
	
	
	public void loadShopGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/shop/");
		shopTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		attack_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "attackup.png");
		health_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "healthup.png");
		buy_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "buy.png");
		xiaojinbi_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "xiaojinbi.png");
		caonima_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shopTextureAtlas, activity, "caonima.png");
		try {
			this.shopTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.shopTextureAtlas.load();
		}
		catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}

	}

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		
		game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "gamebackground.png");
		stone_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "stone.png");
		breakable_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "breakable.png");
		coin_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "coin.png");
		player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 3, 1); 
		player2_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 3, 1); 
		base_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "base.png");
		flag_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "flag.png");
		bullet_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "bullet.png");
		bullet2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "bullet.png");
		
		red_enemy_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "red_enemy.png", 3, 1); 
		blue_enemy_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "blue_enemy.png", 3, 1);
		yellow_enemy_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "yellow_enemy.png", 3, 1); 
		boss_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "boss.png", 1, 1); 
		
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
		shop_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "multi.png");
		help_region =  BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "music.png");
		leaderboard_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "multi.png");
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
	
	public void unloadLoginScene() {
		loginTextureAtlas.unload(); 
		
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
