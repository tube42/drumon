
package se.tube42.drum.audio;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * the sequencer handles the sequence steps and
 * starts the corresponding sample accordingly.
 *
 * this class is mostly called from the audio loop, to allow
 * animation we export checkStarted() which can be handled in UI
 * thread once we are there...
 */

public class Sequencer
{
    private int tcnt, bcnt;
    private boolean pause;
    private boolean [] started;
    private Program prog;
    
    public Sequencer(Program prog)
    {
        this.started = new boolean[VOICES];
        
        setProgram(prog);
        setPause(false);
        reset();
    }

    public void reset()
    {
        tcnt = 0;
        bcnt = 0;
        prog.reset();
    }
    
    //
    public void setProgram(Program prog)
    {
        this.prog = prog;
    }
    
    public Program getProgram()
    {
        return prog;
    }
        
    //
    
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

    public int getBeat()
    {
        return bcnt;
    }

    public int nextBeat()
    {
        final int max = 60 * World.freq;
        int n = (max - tcnt) / (prog.getTempo() * prog.getTempoMultiplier());

        // error handler??
        if(n < 1) n = World.freq / 4;

        return n;
    }
    
    // 
    
    public boolean update(int samples)
    {
        final int max = 60 * World.freq;

        tcnt += samples * prog.getTempo() * prog.getTempoMultiplier();

        if(tcnt > max) {
            tcnt -= max;

            // this should not happen...
            if(tcnt > max) {
                tcnt = 0;
            }

            bcnt = (bcnt + 1) & 15;

            for(int i = 0; i < VOICES; i++) {
                if(prog.get(i, bcnt) != 0) {
                    final int index = i + prog.getSample(i) * VOICES;
                    World.sounds[index].start();
                    started[i] = true;
                }
            }
            return true;
        }
        return false;
    }
}
