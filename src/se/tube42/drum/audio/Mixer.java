
package se.tube42.drum.audio;

import java.io.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

import se.tube42.drum.data.*;

public class Mixer
implements Runnable, Disposable
{    
    private AudioDevice ad;
    private Thread thread;
    
    private int freq, buffer_size;    
    private float [] buffer;    
    private boolean stopped;
    
    public Mixer()
    {
        this.freq = World.freq;
        this.ad = Gdx.audio.newAudioDevice(freq, true);
        
        final int lat = ad.getLatency();
        this.buffer_size = World.samples;
        this.buffer = new float[buffer_size];        
        System.out.println("AudioDevice latency=" + lat + "samp/" + 
                  (1000 * lat / (float)freq) + " ms " + 
                  " freq=" + freq + 
                  " samples=" + buffer_size + 
                  " mono=" + ad.isMono());                
        thread = new Thread(this);
    }
    
    
    public void start()
    {
        stopped = false;
        thread.start();
    }
    
    public void stop()
    {
        stopped = true;
    }

    public void dispose()
    {
        if(ad != null) {
            ad.dispose();
            ad = null;
        }
        
        stop();
    }
    
    public void run()
    {
        
        while(!stopped)  {            
            
            for(int i = 0; i < buffer_size; i++) {
                buffer[i] = 0;
            }
            
            while(World.seq.isPaused()) {
                try {
                    ad.writeSamples(buffer, 0, buffer_size);
                } catch(Exception e) {
                    System.err.println("ERROR " + e);
                    stopped = true;
                }                
            }
            
            int cnt = World.seq.nextBeat();
            if(cnt >= buffer_size)
                cnt = buffer_size;
            
            World.seq.update(cnt); 
            for(int i = 0; i < World.sounds.length ; i++) {
                World.sounds[i].write(buffer, 0, cnt);
            }
            
            final int rem = buffer_size - cnt;            
            if(rem > 0) {                
                World.seq.update(rem); 
                for(int i = 0; i < World.sounds.length ; i++) {
                    World.sounds[i].write(buffer, cnt, rem);
                }
            }
            
            World.chain.process(buffer, 0, buffer_size);
            
            try {
                ad.writeSamples(buffer, 0, buffer_size);
            } catch(Exception e) {
                System.err.println("ERROR " + e);
                stopped = true;
            }
            
        }
    }
    
}
