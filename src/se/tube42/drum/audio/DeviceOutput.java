
package se.tube42.drum.audio;

import java.io.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

import se.tube42.drum.data.*;


public class DeviceOutput implements Output
{
    private AudioDevice ad;

    public DeviceOutput()
    {
        this.ad = null;
    }

    public void open()
    {
        if(ad != null) {
            close();
        }

        int freq = World.freq;
        ad = Gdx.audio.newAudioDevice(freq, true);
        int lat = ad.getLatency();

        System.out.println(
                  "AudioDevice latency=" + lat + "samp/" +
                  (1000 * lat / (float)freq) + " ms " +
                  " freq=" + freq +
                  " mono=" + ad.isMono()
                  );

    }

    public boolean write(float []buffer, int offset, int size)
    {
        try {
            ad.writeSamples(buffer, 0, size);
            return true;
        } catch(Exception e) {
            System.err.println("ERROR " + e);
            return false;
        }
    }

    public void close()
    {
        if(ad != null) {
            ad.dispose();
            ad = null;
        }
    }
}
