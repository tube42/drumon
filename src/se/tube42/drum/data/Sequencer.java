
package se.tube42.drum.data;

import static se.tube42.drum.data.Constants.*;

public class Sequencer
{
    private int [][]data;
    private int [] banks;
    private int [] samples;
    private int voice;
    private int num_voices, num_steps, num_banks;
    private int tempo, tmul, tcnt, bcnt;
    private boolean pause;
    private boolean [] started;
    public Sequencer(int num_voices, int num_steps, int banks)
    {
        this.num_voices = num_voices;
        this.num_steps = num_steps;
        this.num_banks = banks;
        this.data = new int[num_voices][num_steps * num_banks];
        this.banks = new int[num_voices];
        this.samples = new int[num_voices];
        this.started = new boolean[num_voices];
        
        setTempoMultiplier(1);
        setTempo(120);
        setVoice(0);
        setPause(false);
        reset();
    }
    
    public void reset()
    {
        tcnt = 0;
        bcnt = 0;
        
        for(int i = 0; i < num_voices; i++)
            setBank(i, 0);
    }
    
    //
    public int numOfVoices() { return num_voices; }
    public int numOfSteps() { return num_steps; }
    public int numOfBanks() { return num_banks; }
    
    public boolean checkStarted(int voice)
    {
        if(started[voice]) {
            started[voice] = false;
            return true;
        }
        return false;
    }
    //
    public boolean isPaused() { return pause; }
    public void setPause(boolean p) { this.pause = p; }
    
    //
    public int getSample(int voice) 
    { 
        return samples[voice];
    }
    public void setSample(int voice, int s) 
    { 
        samples[voice] = s;
    }
    
    //
    public int getBank(int voice){ return banks[voice]; }
    public void setBank(int voice, int b)
    {
        if(b >= 0 && b < num_banks)
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
    
    public int getBeat()
    {
        return bcnt;
    }
    
    public int getVoice() 
    { 
        return voice; 
    }
    
    public void setVoice(int v) 
    {
        if(v >= 0 && v < num_voices) this.voice = v;
    }
    
    // 
    
    public int get(int voice, int step)
    {
        final int off = banks[voice] * num_steps;
        return data[voice][step + off];
    }
    
    public void set(int voice, int step, int val)
    {
        final int off = banks[voice] * num_steps;        
        data[voice][step + off] = val;
    }
    
    //
    public int nextBeat()
    {
        final int max = 60 * World.freq;
        int n = (max - tcnt) / (tempo * tmul);
        
        // error handler??
        if(n < 1) n = World.freq / 4;
                
        return n;
    }
    
    public boolean update(int samples)
    {
        final int max = 60 * World.freq;
        
        tcnt += samples * tempo * tmul;
        
        if(tcnt > max) {
            tcnt -= max;
            
            // this should not happen...
            if(tcnt > max) {
                tcnt = 0;
            }
            
            bcnt = (bcnt + 1) & 15;
            
            for(int i = 0; i < VOICES; i++) {
                if(get(i, bcnt) != 0) {
                    final int index = i + this.samples[i] * VOICES;
                    World.sounds[index].start();
                    started[i] = true;
                }
            }
            return true;
        }
        return false;
    }    
}
