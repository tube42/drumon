
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

public class Sequencer {

    private int tcnt;
    private boolean pause;

    private Program prog;
    private TimeSignature ts;
    private Hypermeasure hm;
    private SequencerListener listener;


    public Sequencer(Program prog, TimeSignature ts) {
        this.listener = null;
        this.ts = ts;
        this.hm = new Hypermeasure();

        setProgram(prog);
        setPause(false);
        reset();
    }

    public void setListener(SequencerListener listener) {
        this.listener = listener;
    }

    public void reset() {
        restart();
        prog.reset();
    }

    public void restart() {
        tcnt = 0;
        hm.reset();
    }

    //
    public void setProgram(Program prog) {
        this.prog = prog;
    }

    public Program getProgram() {
        return prog;
    }

    //

    public boolean isPaused() {
        return pause;
    }

    public void setPause(boolean p) {
        this.pause = p;
    }

    //

    public int getBeat() {
        return hm.getBeat();
    }

    // this is called when we enter a new beat and may want to play new notes
    private void visit_beat(int position) {
        if (listener != null)
            listener.onBeatStart(position);

        for (int i = 0; i < VOICES; i++) {
            if (prog.get(i, position)) {
                float amp = prog.getVolume(i);
                final int variant = prog.getSampleVariant(i);

                // check if there is any volume variation
                final float vr = prog.getVolumeVariation(i) / 100.0f;
                if (vr > 0) {
                    amp *= ServiceProvider.getRandom(1 - vr, 1 + vr);
                }

                World.sounds[i].start(variant, amp);
                if (listener != null)
                    listener.onSampleStart(position, i);
            }
        }
    }

    // progress the sequencer with up to "samples" samples, returns the actual number of samples processed
    public int update(int samples) {
        if (pause)
            return samples;

        // num-samples = (freq * 30 ) / (temp * 2 ^ multiplier)
        // (tempo is beats per minute and we start with laves, hence 30)
        final int max = 30 * World.freq;
        final int samples_per_beat = prog.getTempo() << prog.getTempoMultiplier();

        tcnt += samples * samples_per_beat;
        if (tcnt < max)
            return samples;

        // we started a new beat
        samples = (tcnt - max) / samples_per_beat;
        tcnt = 0;

        // move to next beat and figure our where it is
        if( hm.next(ts)) {
            visit_beat(hm.getBeat() );
        }

        // bcnt = (bcnt + 1) & 31;
        // bcnt = Measure.tenaryCorrection(mes, bcnt); // correct beat for tenary where we skip 4th (or 7th & 8th)
        // i// f (Measure.plays(mes, bcnt)) { // check if we skip this beat (e.g. skip odd ones in x/4)
        // visit_beat(bcnt);
        // }

        return samples;
    }
}
