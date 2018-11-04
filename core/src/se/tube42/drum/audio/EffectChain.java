
package se.tube42.drum.audio;

import se.tube42.drum.data.World;

import static se.tube42.drum.data.Constants.FX_COMP;
import static se.tube42.drum.data.Constants.FX_DELAY;
import static se.tube42.drum.data.Constants.FX_FILTER;
import static se.tube42.drum.data.Constants.FX_LOFI;


public class EffectChain {
    public static final int SIZE = 4;

    private int enabled;
    private Effect[] effects;

    public EffectChain() {
        // build the chain
        effects = new Effect[SIZE];
        effects[FX_LOFI] = new Lofi();
        effects[FX_FILTER] = new Filter();
        effects[FX_DELAY] = new Delay(World.freq);
        effects[FX_COMP] = new Compressor();

        reset();
    }

    // ------------------------------------------
    public void reset() {
        enabled = 0;
        for (Effect e : effects)
            e.reset();
    }

    public Effect[] getEffects() {
        return effects;
    }

    public Effect getEffect(int index) {
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

    public boolean isEnabled(int n) {
        return (enabled & (1 << n)) != 0;
    }

    public void toggle(int n) {
        enabled ^= 1 << n;
    }


    public void process(float[] data, int offset, int size) {
        for (int i = 0; i < effects.length; i++) {
            if (isEnabled(i))
                effects[i].process(data, offset, size);
        }
    }
}
