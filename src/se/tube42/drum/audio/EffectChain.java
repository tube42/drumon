
package se.tube42.drum.audio;

import java.io.*;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

import se.tube42.drum.data.*;

import static se.tube42.drum.data.Constants.*;


public class EffectChain
{
    public static final int SIZE = 4;

    private int enabled;
    private Effect [] effects;

    public EffectChain()
    {
        enabled = 0;

        // configure our FIR filter
        FIRFilter fir = new FIRFilter(7);
        for(int i = 0; i < 7; i++)
            fir.set(i, 1f / 7);


        // build the chain
        effects = new Effect[SIZE];
        effects[FX_LOFI] = new Lofi();
        effects[FX_FILTER] = fir;
        effects[FX_DELAY] = new Delay(World.freq);
        effects[FX_COMP] = new Compressor();
    }

    // ------------------------------------------

    public Effect [] getEffects()
    {
        return effects;
    }

    public Effect getEffect(int index)
    {
        return effects[index];
    }

    // ------------------------------------------

    public int getEnabledRaw() // for serialization
    {
        return enabled;
    }

    public void setEnabledRaw(int e) // for serialization
    {
        enabled = e;
    }

    public boolean isEnabled(int n)
    {
        return (enabled  & (1 << n)) != 0;
    }

    public void toggle(int n)
    {
        enabled ^= 1 << n;
    }


    public void process(float [] data, int offset, int size)
    {
        for(int i = 0; i < effects.length; i++) {
            if(isEnabled(i))
                effects[i].process(data, offset, size);
        }
    }
}
