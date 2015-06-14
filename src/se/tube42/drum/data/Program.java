
package se.tube42.drum.data;

import static se.tube42.drum.data.Constants.*;

/*
 * the sequencer program
 */

public class Program
{
    private int [][]data;
    private int [] banks;
    private int [] sample_variants;
    private float [] sample_vol;
    private int [] sample_vol_variation;

    private int tempo, tmul;
    private int voice;

    public Program(float [] amps)
    {
        this.data = new int[VOICES][PADS * BANKS];
        this.banks = new int[VOICES];
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
            setBank(i, 0);
            setSampleVariant(i, 0);
        }
    }

    //
    public int numOfVoices() { return VOICES; }
    public int numOfSteps() { return PADS; }
    public int numOfBanks() { return BANKS; }

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
    public int getBank(int voice){ return banks[voice]; }

    public void setBank(int voice, int b)
    {
        if(b >= 0 && b < BANKS)
            this.banks[voice] = b;
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

    public int get(int voice, int step)
    {
        final int off = banks[voice] * PADS;
        return data[voice][step + off];
    }

    public void set(int voice, int step, int val)
    {
        final int off = banks[voice] * PADS;
        data[voice][step + off] = val;
    }
}
