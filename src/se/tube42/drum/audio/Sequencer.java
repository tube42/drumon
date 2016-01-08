
package se.tube42.drum.audio;

import se.tube42.drum.logic.*;
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
    private Program prog;
	private SequencerListener listener;


    public Sequencer(Program prog)
    {
        this.listener = null;

        setProgram(prog);
        setPause(false);
        reset();

        bcnt = 31; // next will be 0
    }

	public void setListener(SequencerListener listener)
	{
		this.listener = listener;
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

    public boolean isPaused() { return pause; }
    public void setPause(boolean p) { this.pause = p; }

    //

    public int getBeat()
    {
        return bcnt;
    }

    public int nextBeat()
    {
        final int max = 30 * World.freq;
        final int m = 1 << prog.getTempoMultiplier();
        int n = (max - tcnt) / (prog.getTempo() * m);

        // error handler??
        if(n < 1) n = World.freq / 4;

        return n;
    }

    //

    public boolean update(int samples)
    {
        if(pause) {
            // tcnt = 0;
            return true;
        }

        final int max = 30 * World.freq;
        final int m = 1 << prog.getTempoMultiplier();

        tcnt += samples * prog.getTempo() * m;

        if(tcnt > max) {
            tcnt -= max;

            // this should not happen...
            if(tcnt > max) {
                tcnt = 0;
            }

            bcnt = (bcnt + 1) & 31;
            
            // if we are doing 4/4 skip the odd ones
            if((bcnt & 1) != 0 && !prog.getFlag(FLAG_48))
                return false;

            if(listener != null)
            	listener.onBeatStart(bcnt);

            for(int i = 0; i < VOICES; i++) {
                if(prog.get(i, bcnt)) {
                    float amp = prog.getVolume(i);
                    final int variant = prog.getSampleVariant(i);

                    // check if there is any volume variation
                    final float vr = prog.getVolumeVariation(i) / 100.0f;
                    if(vr > 0) {
                        amp *= ServiceProvider.getRandom( 1 - vr, 1 + vr);
                    }

                    World.sounds[i].start(variant, amp);
                    if(listener != null)
            			listener.onSampleStart(bcnt, i);
                }
            }
            return true;
        }
        return false;
    }
}
