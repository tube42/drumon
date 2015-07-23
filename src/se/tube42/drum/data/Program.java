
package se.tube42.drum.data;

import static se.tube42.drum.data.Constants.*;

/*
 * the sequencer program
 */

public class Program
{
    private int []data;
    private int bank_active;
    private int [] sample_variants;
    private float [] sample_vol;
    private int [] sample_vol_variation;

    private int tempo, tmul;
    private int voice;
    
    public Program(float [] amps)
    {
        this.data = new int[VOICES];
        this.sample_variants = new int[VOICES];
        this.sample_vol = new float[VOICES];
        this.sample_vol_variation = new int[VOICES];

        // set default amps
        for(int i = 0; i < VOICES; i++) {
            sample_vol[i] = amps[i];
            sample_vol_variation[i] = DEFAULT_VARIATION;
        }

        setTempoMultiplier(2);
        setTempo(120);
        setVoice(0);
        reset();
    }

    public void reset()
    {
        for(int i = 0; i < VOICES; i++) {
            setBank(i, false);
            setSampleVariant(i, 0);
        }
    }

    //
    public int numOfVoices() { return VOICES; }
    public int numOfSteps() { return PADS; }

    //
    public int getSampleVariant(int voice)
    {
        return sample_variants[voice];
    }

    public void setSampleVariant(int voice, int v)
    {
        sample_variants[voice] = v;
    }

    //

    public int getVolumeVariation(int voice)
    {
        return sample_vol_variation[voice];
    }

    public boolean setVolumeVariation(int voice, int var)
    {
        if(var < MIN_VARIATION || var > MAX_VARIATION)
            return false;

        sample_vol_variation[voice] = var;
        return true;
    }

    public float getVolume(int voice)
    {
        return sample_vol[voice];
    }

    public boolean setVolume(int voice, float v)
    {
        if(v < 0.1f || v > 2.8f)
            return false;

        sample_vol[voice] = v;
        return true;
    }

    //
    public boolean getBank(int voice)
    {
        return get_bit(bank_active, voice);
    }

    public void setBank(int voice, boolean b)
    {
        bank_active = set_bit(bank_active, voice, b);
    }

    //
    public int getTempo() { return tempo; }

    public void setTempo(int t)
    {
        if(t > MIN_TEMPO && t < MAX_TEMPO)
            tempo = t;
    }

    public int getTempoMultiplier()
    {
        return tmul;
    }

    public void setTempoMultiplier(int t)
    {
        if(t > 0 && t  < 64)
            this.tmul = t;
    }

    //
    public int getVoice()
    {
        return voice;
    }

    public void setVoice(int v)
    {
        if(v >= 0 && v < VOICES) this.voice = v;
    }

    //

    public boolean get(int voice, int step)
    {
        final int off = getBank(voice) ? 0 : PADS;
        return get_bit(data[voice], step + off);
    }

    public void set(int voice, int step, boolean set)
    {
        final int off = getBank(voice) ? 0 : PADS;
        data[voice] = set_bit(data[voice], step + off, set);
    }
    
    
    // some bitfield helpers to make the code above more clean
    private static final boolean get_bit(int data, int bit)
    {
        final int mask = 1 << bit;
        return (data & mask) != 0;
    }
    private static final int set_bit(int data, int bit, boolean set)
    {
        final int mask = 1 << bit;
        if(set) data |= mask;
        else data &= ~ mask;
        return data;
    }
}
