
package se.tube42.drum.data;

import se.tube42.lib.util.*;
import static se.tube42.drum.data.Constants.*;

/*
 * the sequencer program
 */

public class Program extends Flags
{
    private int [][]data;
    private int bank_active;
    private int [] sample_variants;
    private float [] sample_vol;
    private int [] sample_vol_variation;

    private int tempo, tmul;
    private int voice;

    public Program(float [] amps)
    {
        this.data = new int[VOICE_BANKS][VOICES];
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
            clear(i, true);
        }
    }


    //
    public int getSampleVariant(int voice)
    {
        return sample_variants[voice];
    }

    public void setSampleVariant(int voice, int v)
    {
        if(World.sounds[voice].hasVariant(v))
            sample_variants[voice] = v;
        else
            System.err.println("Sample variant out of range");
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
        if(v < MIN_VOLUME / 100f || v > MAX_VOLUME / 100f)
            return false;

        sample_vol[voice] = v;
        return true;
    }

    //
    public int getBank(int voice)
    {
        return get_bit(bank_active, voice) ? 1 : 0;
    }

    public void setBank(int voice, int bank)
    {
        if(bank >= 0 && bank < VOICE_BANKS)
            bank_active = set_bit(bank_active, voice, bank != 0);
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

    // returns the number of banks used for this voice
    public int getUsedBanks(int voice)
    {
        int ret = 0;
        for(int i = 0; i < VOICE_BANKS; i++)
            if(data[i][voice] != 0)
                ret = i + 1;
        return ret;
    }

    public void clear(int voice, boolean allbanks)
    {
        if(allbanks) {
            for(int i = 0; i < VOICE_BANKS; i++)
                data[i][voice] = 0;
        } else {
            final int bank = getBank(voice);
            data[bank][voice] = 0;
        }
    }

    //
    public boolean get(int voice, int step)
    {
        final int bank = getBank(voice);
        return get_bit(data[bank][voice], step);
    }

    public void set(int voice, int step, boolean set)
    {
        final int bank = getBank(voice);
        data[bank][voice] = set_bit(data[bank][voice], step, set);
    }

    // these are used for serialization
    public int getRawData(int bank, int voice)
    {
        return data[bank][voice];
    }
    public void setRawData(int bank, int voice, int data)
    {
        this.data[bank][voice] = data;
    }

    public int getRawBanks()
    {
        return bank_active;
    }

    public void setRawBanks(int banks)
    {
        bank_active = banks;
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
