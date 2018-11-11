package se.tube42.drum.android;

import android.content.*;
import android.media.*;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.*;

import se.tube42.drum.*;
import se.tube42.drum.data.*;
import se.tube42.lib.service.*;

// import se.tube42.drum.logic.SystemHandler;

public class MainActivity extends AndroidApplication
{
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        
        try {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            String s1 = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            String s2 = am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            
            if(s1 != null && s2 != null) {
                World.freq = Integer.parseInt(s1);
                World.samples = Integer.parseInt(s2);
                System.out.println("AudioManager suggested fs=" + World.freq +
                          ", samples=" + World.samples);
            }
            
        } catch(Throwable t) {
            System.err.println("Could not get device defaults: " + t.toString() );
        }
        
        AndroidService.setInstance(new AndroidService(this));

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        cfg.useWakelock = true;
        initialize(new DrumApp(), cfg);
    }


    public void onPause()
    {
        super.onPause();
        StorageService.flush();
    }

    public void onDestroy()
    {
        super.onDestroy();
    }
}
