
package se.tube42.drum.logic;

import java.io.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

public class TempoDetector
{    
    public static final long MAX_DIFF = 3 * 1000; 
    public static long l0, l1;
    
    public int tempo;
    
    public TempoDetector()
    {
        reset();
    }
    
    public void reset()
    {
        l0 = l1 = -1;
        tempo = -1;
    }
    
    public boolean add()
    {
        long n = System.currentTimeMillis();
        
        if(n - l0 < MAX_DIFF) {
            long d0 = Math.max(1, n - l0);
            long d1 = Math.max(1, l0 - l1);
            l1 = l0;
            l0 = n;
            
            int t0 = (int)(60000L / d0);
            int t1 = (int)(60000L / d1);
            
            if( Math.max(t0, t1)  <= 1.2f * Math.min(t0, t1)) {
                int t2 = (t0 + t1) / 2;
                if(t2 > MIN_TEMPO && t2 < MAX_TEMPO) {
                    tempo = t2;
                    return true;
                }
            }            
            return false;
        } else {
            reset();
            l0 = n;
            return false;
        }
    }
    
    public int get()
    {
        return tempo;
    }
}
