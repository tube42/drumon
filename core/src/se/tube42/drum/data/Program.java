
package se.tube42.drum.data;

import se.tube42.lib.util.*;
import static se.tube42.drum.data.Constants.*;

/*
 * the sequencer program
 */

public class Program extends Parameters
{
    public final static int
          PARAM_VOLUME_n = 0,
          PARAM_VOLUME_VAR_n = VOICES,
          PARAM_TEMPO = VOICES * 2 + 0,
          PARAM_TEMPO_MUL = VOICES * 2 + 1
          ;

    private int [][]data;
    private int bank_active;
    private int [] sample_variants;
    private int voice;
    private int flags;
    private float [] default_amps;

    public Program(float [] amps)
    {
        super(2 + 2 * VOICES);
        this.default_amps = amps;
        this.flags = 0;
        this.data = new int[VOICE_BANKS][VOICES];
        this.sample_variants = new int[VOICES];

        // tempo parameters
        configure(PARAM_TEMPO, MIN_TEMPO, MAX_TEMPO, 120);
        configure(PARAM_TEMPO_MUL, MIN_TEMPO_MUL, MAX_TEMPO_MUL, 1);

        // volume parameters:
        for(int i = 0; i < VOICES; i++) {
            configure(PARAM_VOLUME_VAR_n + i, MIN_VOLUME_VAR, MAX_VOLUME_VAR, 20);
            configure(PARAM_VOLUME_n + i, MIN_VOLUME, MAX_VOLUME, amps[i]);
        }

        setVoice(0);
        reset();
    }

    public void reset()
    {
        for(int i = 0; i < VOICES; i++) {
            set(PARAM_VOLUME_VAR_n + i, 20);
            set(PARAM_VOLUME_n + i, default_amps[i]);
            setBank(i, 0);
            setSampleVariant(i, 0);
            clear(i, true);
        }

        set(PARAM_TEMPO, 120);
        set(PARAM_TEMPO_MUL, 1);
        setVoice(0);
    }

    public String getLabel(int index)
    {
        if(index < VOICES)
            return "volume";
        else if(index < 2 * VOICES)
            return "variation";
        else if(index == PARAM_TEMPO)
            return "tempo";
        else if(index == PARAM_TEMPO_MUL)
            return "multiplicate";
        else
            return null;
    }
    // flags
    public int getRawFlags()
    {
        return flags;
    }
    public void setRawFlags(int flags)
    {
        this.flags = flags;
    }
    public int getMeasure()
    {
        return flags & 7;
    }

    public void setMeasure(int s)
    {
        flags =  (s & 7) | ( flags & ~7);
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
    public final int getVolumeVariation(int voice)
    {
        return getInt(PARAM_VOLUME_VAR_n + voice);
    }

    public final void setVolumeVariation(int voice, int var)
    {
        set(PARAM_VOLUME_VAR_n + voice, var);
    }

    public float getVolume(int voice)
    {
        return get(PARAM_VOLUME_n + voice);
    }

    public void setVolume(int voice, float v)
    {
        set(PARAM_VOLUME_n + voice, v);
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
    public final int getTempo()
    {
        return getInt(PARAM_TEMPO);
    }

    public final void setTempo(int t)
    {
        set(PARAM_TEMPO, t);
    }

    public final int getTempoMultiplier()
    {
        return getInt(PARAM_TEMPO_MUL);
    }

    public final void setTempoMultiplier(int t)
    {
        set(PARAM_TEMPO_MUL, t);
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
