package se.tube42.drum;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.util.*;
import se.tube42.lib.service.*;

import se.tube42.drum.view.*;
import se.tube42.drum.logic.*;
import se.tube42.drum.audio.*;
import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

public class DrumApp extends BaseApp {

	public DrumApp() {

	}

	public void onCreate(SceneManager mgr, Item bgc) {
		ServiceProvider.init();

		// set size before loading assets
		onResize(World.sw, World.sh);

		World.bgc = bgc;
		World.mgr = mgr;

		// update size once more
		onResize(World.sw, World.sh);

		// World.mgr.setScene(World.scene_drum);
		World.mgr.setScene(new InitScene() );

		// TEMP until we fix the code handling back:
		Gdx.input.setCatchBackKey(false);
	}

	public void onUpdate(float dt, long dtl) {
		ServiceProvider.service(dtl); // this will update job manager, tween manager and so on
	}

	public void onResize(int w, int h) {
		LayoutService.resize(w, h);
	}

	// ----------------------------------------------

	@Override
	public void pause() {
		ServiceProvider.autoSave();
		if (!Settings.bg_play) {
			World.mgr.onPause();
			if(World.dm != null)
				World.dm.mixer.stop();
		}
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
		if(World.dm != null)
			World.dm.mixer.start();
		World.mgr.onResume();

	}

	public void dispose() {
		System.out.println("Disposing...\n");

		// assuming we allowed bg-play, we might need to update stop music now
		World.mgr.onPause();
		if(World.dm != null)  {
			World.dm.mixer.stop();
			World.dm.mixer.dispose();
		}

		super.dispose();
	}
}
