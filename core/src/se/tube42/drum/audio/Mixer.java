
package se.tube42.drum.audio;

import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.*;

import se.tube42.drum.data.*;
import static se.tube42.drum.data.Constants.*;

/*
 * mixer is the audio loop.
 * It request new samples from the sequencer and
 * feeds them in the effect chain and out to the
 * speakers
 */

public final class Mixer implements Runnable, Disposable {
    private Thread thread;

    private int buffer_size;
    private float[] buffer;
    private volatile boolean stopped;

    private Output output;
    private EffectChain chain;

    public Mixer(Output output) {
        this.buffer_size = (World.samples + SIMD_WIDTH - 1) & ~(SIMD_WIDTH - 1);
        this.buffer = new float[buffer_size];
        System.out.println("MIXER: buffer-size=" + buffer_size + " SIMD-width=" + SIMD_WIDTH);
        this.output = output;
        this.chain = new EffectChain();
        this.thread = null;
    }

    public void start() {
        if (thread == null) {
            System.out.println("MIXER: started " + stopped);
            stopped = false;
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        stopped = true;
        thread = null;
        System.out.println("MIXER: stopped " + stopped);
    }

    public void dispose() {
        stop();
    }

    // ---------------------------------------------------------

    public EffectChain getEffectChain() {
        return chain;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output o) {
        output = o;
    }

    // ---------------------------------------------------------
    // fill the sample buffer with data
    private void update_buffer() {
        for (int i = 0; i < buffer_size; i++) {
            buffer[i] = 0;
        }
        // feed samples and check with sequencer
        for (int samples = 0; samples < buffer_size;) {
            int spent = World.seq.update(buffer_size - samples);
            for (int i = 0; i < World.sounds.length; i++) {
                World.sounds[i].write(buffer, samples, spent);
            }

            samples += spent;
        }

        // process effects
        chain.process(buffer, 0, buffer_size);
    }

    public void run() {
        final Output output = this.output;
        int errcnt = 0;

        try {
            Thread.sleep(1500);
        } catch (Exception exx) {
        }

        output.open();

        while (!stopped) {
            try {

                update_buffer();

                /// write results
                if (!output.write(buffer, 0, buffer_size))
                    break;

                /// note that we didn't crash this time :)
                if (errcnt > 0)
                    errcnt--;
            } catch (Exception e) {
                System.err.println("ERROR: " + e);
                errcnt++;
                if (errcnt > 10)
                    stopped = true;
                try {
                    Thread.sleep(50 + 50 * errcnt);
                } catch (Exception ignored) {
                }
            }
        }
        output.close();
    }

}
