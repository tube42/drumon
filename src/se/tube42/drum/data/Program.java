
package se.tube42.drum.data;

import se.tube42.lib.util.*;
import static se.tube42.drum.data.Constants.*;

/*
 * the sequencer program
 */

public class Program extends Flags
{
    private Parameters tempo;
    private Parameters[] volumes;

    private int [][]data;
    private int bank_active;
    private int [] sample_variants;
    private int voice;

    public Program(float [] amps)
    {
        this.data = new int[VOICE_BANKS][VOICES];
        this.sample_variants = new int[VOICES];

        // temp parameters
        tempo = new Parameters(2);
        tempo.configure(0, MIN_TEMPO, MAX_TEMPO, 120);
        tempo.configure(1, MIN_TEMPO_MUL, MAX_TEMPO_MUL, 1);

        // volume parameters:
        volumes = new Parameters[VOICES];
        for(int i = 0; i < VOICES; i++) {
            volumes[i] = new Parameters(2);
            volumes[i].configure(0, MIN_VOLUME_VAR, MAX_VOLUME_VAR, 20);
            volumes[i].configure(1, MIN_VOLUME, MAX_VOLUME, amps[i]);
        }

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

    // flags
    public int getMeasure()
    {
        return getRawFlags() & 7;
    }

    public void setMeasure(int s)
    {
        setRawFlags( (s & 7) |  (getRawFlags() & ~7));
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
    // Volume
    //
    public Parameters getVolumeParameters(int voice)
    {
        return volumes[voice];
    }
    public int getVolumeVariation(int voice)
    {
        return volumes[voice].getInt(0);
    }

    public void setVolumeVariation(int voice, int var)
    {
        volumes[voice].set(0, var);
    }

    public float getVolume(int voice)
    {
        return volumes[voice].get(1);
    }

    public void setVolume(int voice, float v)
    {
        volumes[voice].set(1, v);
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
    // temp
    //

    public Parameters getTempoParameters()
    {
        return tempo;
    }
    public int getTempo()
    {
        return tempo.getInt(0);
    }

    public void setTempo(int t)
    {
        tempo.set(0, t);
    }

    public int getTempoMultiplier()
    {
        return tempo.getInt(1);
    }

    public void setTempoMultiplier(int t)
    {
        tempo.set(1, t);
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
