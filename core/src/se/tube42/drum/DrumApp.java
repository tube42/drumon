package se.tube42.drum;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.ks.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.util.*;
import se.tube42.lib.service.*;


import se.tube42.drum.view.*;
import se.tube42.drum.logic.*;
import se.tube42.drum.audio.*;
import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

public class DrumApp extends BaseApp
{

    public DrumApp()
    {

    }


    public void onCreate(SceneManager mgr, Item bgc)
    {
    	ServiceProvider.init();

        // set size before loading assets
        onResize(World.sw, World.sh);

        World.bgc = bgc;
        load_assets();

        // update size once more
        onResize(World.sw, World.sh);

        // create mixer
        World.prog = new Program(DEF_AMPS);
        World.seq = new Sequencer(World.prog);
        final DeviceOutput dev = new DeviceOutput();
        World.mixer = new Mixer(dev);

        // set the default amps


        World.scene_drum = new DrumScene();
        World.scene_choice = new ChoiceScene();
        World.scene_choice2 = new Choice2Scene();
        World.scene_save = new SaveScene();
        World.mgr = mgr;

        World.mgr.setScene(World.scene_drum);

    	// TEMP until we fix the code handling back:
		Gdx.input.setCatchBackKey(false);

        // start the mixer!
        World.mixer.start();
    }

    public void onUpdate(float dt, long dtl)
    {
        ServiceProvider.service(dtl); // this will update job manager, tween manager and so on
    }

    public void onResize(int w, int h)
    {
        LayoutService.resize(w, h);
    }

    // ----------------------------------------------

    private void load_assets()
    {
        int ascale = World.ui_scale;
        if(ascale == 3) ascale = 2;
        if(ascale > 4)  ascale = 4;

        String aname = "" + ascale;
        System.out.println("USING " + aname);


        Texture tmp;
        tmp = AssetService.load(aname + "/tiles.png", true);
        World.tex_tiles = AssetService.divide(tmp, 4, 2);

        tmp = AssetService.load(aname + "/icons.png", true);
        World.tex_icons = AssetService.divide(tmp, 8, 8);

        tmp = AssetService.load(aname + "/rect.png", false);
        World.tex_rect = new TextureRegion[1];
        World.tex_rect[0] = new TextureRegion(tmp);


        World.font1 = AssetService.createFonts(
                  "fonts/Roboto-Regular.ttf",
                  CHARSET1, ascale * 16)[0];

        World.font2 = AssetService.createFonts(
                  "fonts/RobotoCondensed-Light.ttf",
                  CHARSET2, ascale * 12)[0];

        try {
            World.sounds = new Sample[VOICES];
            for(int i = 0; i < VOICES ; i++) {
                final int vcount = SAMPLES[i].length;
                final float [][] data = new float[vcount][];
                for(int j = 0; j < vcount; j++) {
                    data[j] = ServiceProvider.loadSample(
                              "samples/" + SAMPLES[i][j], World.freq);
                }

                World.sounds[i] = new Sample(data);
            }

        } catch(Exception e) {
            System.err.println("ERROR " + e);
            System.err.flush();
            System.exit(20);
        }
    }


    @Override public void pause()
    {
		ServiceProvider.autoSave();
		if(!Settings.bg_play) {
        	World.mgr.onPause();
			World.mixer.stop();
		}
        super.pause();
    }


    @Override public void resume()
    {
        super.resume();
        World.mixer.start();
        World.mgr.onResume();

    }

    public void dispose()
    {
		System.out.println("Disposing...\n");

		// assuming we allowed bg-play, we might need to update stop music now
		World.mgr.onPause();
		World.mixer.stop();

        World.mixer.dispose();
        super.dispose();
    }
}

