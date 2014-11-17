package se.tube42.drum;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

import se.tube42.lib.tweeny.*;
import se.tube42.lib.ks.*;
import se.tube42.lib.scene.*;
import se.tube42.lib.util.*;


import se.tube42.drum.view.*;
import se.tube42.drum.logic.*;
import se.tube42.drum.audio.*;
import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

public class DrumApp extends BaseApp
{     
    
    public DrumApp()
    {
        super(200, 300);        
    }
    
    
    public void onCreate(SceneManager mgr, Item bgc)
    {
    	ServiceProvider.init();
        
        
        // force one first resize!
        onResize(World.sw, World.sh);
        
        World.bgc = bgc;
        load_assets();
        mgr.setScene( new DrumScene());
        
        // create this one last!
        World.mixer = new Mixer();
        World.mixer.start();

    	// TEMP until we fix the code handling back:
		Gdx.input.setCatchBackKey(false);
        
        
        
    }


    public void onUpdate(float dt, long dtl)
    {
        ServiceProvider.service(dtl); // this will update job manager, tween manager and so on 
    }
    
    public void onResize(int w, int h)
    {
        final int s1 = ~1 & (int)Math.min( World.sw / 4, World.sh / 8);
        final int s2 = ~3 & (int)(s1 * 0.98f);        
        World.tile_stripe = s1;
        World.tile_size = s2;        
        World.tile_x0 = ((int)(World.sw - s1 * 4 + s1 - s2)) / 2;
        World.tile_y0 = ((int)(World.sh - s1 * 8 + s1 - s2)) / 2;
    }
    
    // ----------------------------------------------
    
    private void load_assets()
    {
        System.out.println("Asset scale: " + World.s_scale_bin);
        
        final String base = "" + World.s_scale_bin;
        
        Texture tmp = ServiceProvider.loadTexture(base + "/tiles.png", true);
        World.tex_tiles =  ServiceProvider.divideTexture(tmp, 
                  World.s_scale_bin * 32, 
                  World.s_scale_bin * 32);
        
        tmp = ServiceProvider.loadTexture(base + "/icons.png", true);
        World.tex_icons =  ServiceProvider.divideTexture(tmp, 
                  World.s_scale_bin * 16,
                  World.s_scale_bin * 16);
        
        World.font = ServiceProvider.loadFont(base + "/font1");
        World.font.setScale(1f / World.s_scale_bin);
        
        try {
            final int scount = SAMPLES.length;
            World.sounds = new Sample[scount];
            for(int i = 0; i < scount ; i++) {
                final String filename = "samples/" + SAMPLES[i];
                final float [] sam = ServiceProvider.loadSample(filename, World.freq);
                World.sounds[i] = new Sample(sam, DEF_AMPS[i]);
            }
        } catch(Exception e) {
            System.err.println("ERROR " + e);
            System.err.flush();
            System.exit(20);
        }
    }
    
    public void dispose()
    {
        System.out.println("Disposing...\n");
        super.dispose();
        World.mixer.dispose();
    }
}

