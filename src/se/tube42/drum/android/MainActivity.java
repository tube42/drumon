package se.tube42.drum.android;

import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.widget.Toast;

import android.content.*;
import android.media.*;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import se.tube42.drum.*;
import se.tube42.drum.logic.*;
import se.tube42.drum.data.*;

// import se.tube42.drum.logic.SystemHandler;

public class MainActivity extends AndroidApplication
{
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            String s1 = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            String s2 = am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);

            if(s1 != null && s2 != null) {
                World.freq = Integer.parseInt(s1);
                World.samples = Integer.parseInt(s2);

                System.out.println("AudioManager suggested fs=" + World.freq +
                          ", samples=" + World.samples);
            }
        }

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useAccelerometer = false;
        cfg.useCompass = false;
        // cfg.useGL20 = true;
        cfg.useWakelock = true;

        /*
           // immersive mode:
           int opt = getWindow().getDecorView().getSystemUiVisibility();
           getWindow().getDecorView().setSystemUiVisibility(
           opt | 0x00001000 | 0x00000004 | 0x00000002);
         */

        initialize(new DrumApp(), cfg);
    }


    public void onPause()
    {
        super.onPause();
        ServiceProvider.flushStorage();
    }

    public void onDestroy()
    {
        super.onDestroy();
    }
}
