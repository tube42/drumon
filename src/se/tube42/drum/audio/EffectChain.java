
package se.tube42.drum.audio;

import java.io.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

import se.tube42.drum.data.*;

public class EffectChain
{    
    public static final int SIZE = 3;
    
    private boolean [] enabled;
    private IIRFilter iir;
    private Compressor comp;
    private Delay delay;
    
    public EffectChain()
    {
        enabled = new boolean[SIZE];
        
        
        delay = new Delay(World.freq, 0.32f, 0.2f);
        comp = new Compressor(0.2f, 0.8f);        
        iir = new IIRFilter(7);
        for(int i = 0; i < iir.getSize(); i++)
            iir.set(i, 1f / 7);
    }
    
    
    public boolean isEnabled(int n)
    {
        return (n >= 0 && n < SIZE) ? enabled[n] : false;

    }
    public void toggle(int n)
    {
        if(n >= 0 && n < SIZE)
            enabled[n] = !enabled[n];
        
    }
    
    
    public void process(float [] data, int offset, int size)
    {        
        if(isEnabled(0)) {
            iir.process(data, offset, size);
        }
        
        if(isEnabled(1)) {            
            delay.process(data, offset, size);            
        }
        
        if(isEnabled(2)) {
            comp.process(data, offset, size);            
        }
    }    
}
