
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

        bcnt = 15; // next will be 0
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
        final int max = 60 * World.freq;
        int n = (max - tcnt) / (prog.getTempo() * prog.getTempoMultiplier());

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

        final int max = 60 * World.freq;

        tcnt += samples * prog.getTempo() * prog.getTempoMultiplier();

        if(tcnt > max) {
            tcnt -= max;

            // this should not happen...
            if(tcnt > max) {
                tcnt = 0;
            }

            bcnt = (bcnt + 1) & 15;

            if(listener != null)
            	listener.onBeatStart(bcnt);

            for(int i = 0; i < VOICES; i++) {
                if(prog.get(i, bcnt) != 0) {
                    float amp = prog.getVolume(i);
                    final int variant = prog.getSampleVariant(i);

                    // check if there is any volume variation
                    final int max_var = prog.getVolumeVariation(i);
                    if(max_var > 0) {
                        final int rand_var = 100 - max_var + ServiceProvider.getRandomInt(2 * max_var);
                        amp *=  rand_var / 100f;
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
