
package se.tube42.drum.data;

import se.tube42.drum.audio.*;
import static se.tube42.drum.data.Constants.*;


public final class DrumMachine
{
    public DrumMachine(Sample [] sounds, Output output) {
        this.sounds = sounds;
        this.output = output;

        this.prog = new Program(DEF_AMPS);
        this.ts = new TimeSignature();
        this.seq = new Sequencer(this.prog, this.ts);
        this.effects = new EffectChain();
        this.mixer = new Mixer(this.seq, this.effects, this.sounds, this.output);
    }

    public Output output;
    public Sample [] sounds;

    public TimeSignature ts;
    public Program prog;
    public Sequencer seq;
    public EffectChain effects;
    public Mixer mixer;


}
